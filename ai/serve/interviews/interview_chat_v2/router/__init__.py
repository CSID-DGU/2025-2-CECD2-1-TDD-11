from fastapi import APIRouter, HTTPException, Depends
from ..dto import InterviewChatV2RequestDto, InterviewChatV2ResponseDto, SessionStartRequestDto, SessionStartResponseDto, SessionEndRequestDto, SessionEndResponseDto
import sys
from pathlib import Path
sys.path.append(str(Path(__file__).parent.parent.parent.parent))
from session_manager import SessionManager
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

@router.post("/start", response_model=SessionStartResponseDto)
async def start_session(request: SessionStartRequestDto):
    """세션 시작"""
    try:
        # 세션 생성
        session_manager.create_session(
            request.session_id, 
            request.preferredCategories,
            request.previousMetrics.dict() if request.previousMetrics else None
        )
        
        result = flow(
            sessionId=request.session_id,
            answer_text=""
        )
        
        first_question = result.get("next_question")
        
        # 세션 저장
        if first_question:
            session_manager.save_session(
                request.session_id,
                metrics={
                    "session_id": request.session_id,
                    "categories": {},
                    "engine_state": {"last_material_id": None},
                    "asked_total": 0
                },
                last_question=first_question
            )
        
        return SessionStartResponseDto(
            session_id=request.session_id,
            first_question=first_question
        )
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"세션 시작 실패: {str(e)}")

@router.post("/chat", response_model=InterviewChatV2ResponseDto)
async def interview_chat(request: InterviewChatV2RequestDto):
    """인터뷰 대화"""
    try:
        # 세션 로드
        session_data = session_manager.load_session(request.session_id)
        if not session_data:
            raise HTTPException(status_code=404, detail="세션을 찾을 수 없습니다")
        
        # 다음 질문 생성
        result = flow(
            sessionId=request.session_id,
            answer_text=request.answer_text
        )
        
        next_question = result.get("next_question")
        
        # Flow에서 Redis에 직접 업데이트하므로 별도 저장 불필요
        
        return InterviewChatV2ResponseDto(next_question=next_question)
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"질문 생성 실패: {str(e)}")

@router.post("/end", response_model=SessionEndResponseDto)
async def end_session(request: SessionEndRequestDto):
    """세션 종료 및 최종 메트릭 반환"""
    try:
        # 세션 로드
        session_data = session_manager.load_session(request.session_id)
        if not session_data:
            raise HTTPException(status_code=404, detail="세션을 찾을 수 없습니다")
        
        # 최종 메트릭 준비
        final_metrics = session_data.get("metrics")
        
        # 세션 삭제
        session_manager.delete_session(request.session_id)
        
        return SessionEndResponseDto(
            session_id=request.session_id,
            final_metrics=final_metrics,
            pool_to_save=[]  # V2에서는 pool 사용 안 함
        )
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"세션 종료 실패: {str(e)}")