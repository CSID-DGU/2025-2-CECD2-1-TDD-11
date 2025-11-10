from fastapi import APIRouter, HTTPException, Depends, Request
from ..dto import InterviewChatV2RequestDto, InterviewChatV2ResponseDto, SessionStartRequestDto, SessionStartResponseDto, SessionEndRequestDto, SessionEndResponseDto
import sys
from pathlib import Path
sys.path.append(str(Path(__file__).parent.parent.parent.parent))
from session_manager import SessionManager
from auth import AuthRequired
import sys
import os
from pathlib import Path

# flow 경로 추가
current_dir = Path(__file__).parent.parent.parent.parent.parent
flows_dir = current_dir / "flows" / "interviews" / "chat" / "interview_chat_v2"
sys.path.insert(0, str(flows_dir))

from promptflow import load_flow

router = APIRouter()
session_manager = SessionManager()

# flow 로드
flow_path = flows_dir / "flow.dag.yaml"
flow = load_flow(str(flow_path))

@router.post("/start", response_model=SessionStartResponseDto, dependencies=[Depends(AuthRequired())])
async def start_session(http_request: Request, request: SessionStartRequestDto):
    """세션 시작"""
    try:
        # JWT에서 userId 추출
        auth_header = http_request.headers.get("Authorization")
        user_id = session_manager.extract_user_id_from_token(auth_header)
        
        # session_id를 autobiography_id로 사용 (bigint)
        autobiography_id = int(request.session_id)
        session_key = session_manager.generate_session_key(user_id, autobiography_id)
        
        # 세션 생성
        session_manager.create_session(
            session_key, 
            request.preferred_categories,
            request.previous_metrics.dict() if request.previous_metrics else None
        )
        
        result = flow(
            sessionId=session_key,
            answer_text=""
        )
        
        first_question = result.get("next_question")
        
        # 세션 저장
        if first_question:
            session_manager.save_session(
                session_key,
                metrics={
                    "session_id": session_key,
                    "user_id": user_id,
                    "autobiography_id": autobiography_id,
                    "categories": {},
                    "engine_state": {"last_material_id": None},
                    "asked_total": 0
                },
                last_question=first_question
            )
        
        return SessionStartResponseDto(
            session_id=request.session_id,  # 응답에는 원래 session_id 사용
            first_question=first_question
        )
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"세션 시작 실패: {str(e)}")

@router.post("/chat", response_model=InterviewChatV2ResponseDto, dependencies=[Depends(AuthRequired())])
async def interview_chat(http_request: Request, request: InterviewChatV2RequestDto):
    """인터뷰 대화"""
    try:
        # JWT에서 userId 추출
        auth_header = http_request.headers.get("Authorization")
        user_id = session_manager.extract_user_id_from_token(auth_header)
        
        # session_id를 autobiography_id로 사용 (bigint)
        autobiography_id = int(request.session_id)
        session_key = session_manager.generate_session_key(user_id, autobiography_id)
        
        # 세션 로드
        session_data = session_manager.load_session(session_key)
        if not session_data:
            raise HTTPException(status_code=404, detail="세션을 찾을 수 없습니다")
        
        # 다음 질문 생성
        result = flow(
            sessionId=session_key,
            answer_text=request.answer_text
        )
        
        next_question = result.get("next_question")
        
        # Flow에서 Redis에 직접 업데이트하므로 별도 저장 불필요
        
        return InterviewChatV2ResponseDto(next_question=next_question)
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"질문 생성 실패: {str(e)}")

@router.post("/end", response_model=SessionEndResponseDto, dependencies=[Depends(AuthRequired())])
async def end_session(http_request: Request, request: SessionEndRequestDto):
    """세션 종료 및 최종 메트릭 반환"""
    try:
        # JWT에서 userId 추출
        auth_header = http_request.headers.get("Authorization")
        user_id = session_manager.extract_user_id_from_token(auth_header)
        
        # session_id를 autobiography_id로 사용 (bigint)
        autobiography_id = int(request.session_id)
        session_key = session_manager.generate_session_key(user_id, autobiography_id)
        
        # 세션 로드
        session_data = session_manager.load_session(session_key)
        if not session_data:
            raise HTTPException(status_code=404, detail="세션을 찾을 수 없습니다")
        
        # 최종 메트릭 준비
        final_metrics = session_data.get("metrics")
        
        # 세션 삭제
        session_manager.delete_session(session_key)
        
        return SessionEndResponseDto(
            session_id=request.session_id,  # 응답에는 원래 session_id 사용
            final_metrics=final_metrics,
            pool_to_save=[]  # V2에서는 pool 사용 안 함
        )
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"세션 종료 실패: {str(e)}")