from fastapi import APIRouter, HTTPException, Depends, Request
from ..dto import InterviewChatV2RequestDto, InterviewChatV2ResponseDto, SessionStartRequestDto, SessionStartResponseDto, SessionEndRequestDto, SessionEndResponseDto
from interviews.interview_chat_v2.publish_persistence_message import publish_persistence_message
import sys
from pathlib import Path
sys.path.append(str(Path(__file__).parent.parent.parent.parent))
from session_manager import SessionManager
from datetime import datetime, timezone
from auth import AuthRequired
import sys
import os
from pathlib import Path
from ...queue.dto import Conversation, InterviewQuestion, InterviewPayload

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

@router.post("/api/v2/interviews/start/{autobiography_id}", response_model=SessionStartResponseDto, dependencies=[Depends(AuthRequired())])
async def start_session(http_request: Request, autobiography_id: int, request: SessionStartRequestDto):
    """세션 시작"""
    try:
        now = datetime.now(timezone.utc)
        
        # JWT에서 userId 추출
        auth_header = http_request.headers.get("Authorization")
        user_id = session_manager.extract_user_id_from_token(auth_header)
        
        # UUID 기반 session_id 생성
        session_key = session_manager.generate_session_key(user_id, autobiography_id)
        
        # 세션 생성
        session_manager.create_session(
            session_key,
            user_id,
            autobiography_id,
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
        
        # queue publish 용 데이터 세팅
        question = InterviewQuestion(
            questionText=first_question,
            questionOrder=0, # 질문 순서 정보가 없으므로 0으로 설정
            timestamp=now
        )
        
        ai_conversation = Conversation(
            content=first_question,
            conversationType="BOT",
            timestamp=now
        )
        
        payload = InterviewPayload(
            autobiographyId=autobiography_id,
            userId=user_id,
            conversation=[ai_conversation],
            interviewQuestion=question
        )
        
        # queue에 메시지 발행
        publish_persistence_message(payload)
        
        return SessionStartResponseDto(
            first_question=first_question
        )
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"세션 시작 실패: {str(e)}")

@router.post("/api/v2/interviews/chat/{autobiography_id}", response_model=InterviewChatV2ResponseDto, dependencies=[Depends(AuthRequired())])
async def interview_chat(http_request: Request, autobiography_id: int, request: InterviewChatV2RequestDto):
    """인터뷰 대화"""
    try:
        # 현재 시간 UTC로 설정
        now = datetime.now(timezone.utc)
        
        # JWT에서 userId 추출
        auth_header = http_request.headers.get("Authorization")
        user_id = session_manager.extract_user_id_from_token(auth_header)
        
        # 세션 키 생성
        session_key = session_manager.generate_session_key(user_id, autobiography_id)
        
        # 세션 로드
        session_data = session_manager.load_session(session_key)
        if not session_data:
            raise HTTPException(status_code=404, detail="세션을 찾을 수 없습니다")
        
        # flow 실행 전 metrics 저장
        previous_metrics = session_data.get("metrics", {})
        
        # 다음 질문 생성
        result = flow(
            sessionId=session_key,
            answer_text=request.answer_text
        )
        
        # flow 실행 후 metrics 로드
        updated_session_data = session_manager.load_session(session_key)
        current_metrics = updated_session_data.get("metrics", {})
        
        # 증가 폭 계산
        deltas = calculate_metrics_delta(previous_metrics, current_metrics, user_id, autobiography_id)
        
        # 변경된 부분만 queue에 발행
        for delta in deltas:
            publish_persistence_message(delta)
        
        next_question = result.get("next_question")
        last_answer_materials_id = result.get("last_answer_materials_id", [])
        
        # queue publish 용 데이터 세팅
        question = InterviewQuestion(
            questionText=next_question,
            questionOrder=0, # 질문 순서 정보가 없으므로 0으로 설정
            timestamp=now
        )
        
        human_conversation = Conversation(
            content=request.answer_text,
            conversationType="HUMAN",
            timestamp=now
        )
        
        ai_conversation = Conversation(
            content=next_question,
            conversationType="BOT",
            timestamp=now
        )
        
        payload = InterviewPayload(
            autobiographyId=autobiography_id,
            userId=user_id,
            conversation=[human_conversation, ai_conversation],
            interviewQuestion=question
        )
        
        # queue에 메시지 발행
        publish_persistence_message(payload)
        
        return InterviewChatV2ResponseDto(next_question=next_question, last_answer_materials_id=last_answer_materials_id)
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"질문 생성 실패: {str(e)}")

@router.post("/api/v2/interviews/end/{autobiography_id}", response_model=SessionEndResponseDto, dependencies=[Depends(AuthRequired())])
async def end_session(http_request: Request, autobiography_id: int, request: SessionEndRequestDto):
    """세션 종료 및 최종 메트릭 반환"""
    try:
        # JWT에서 userId 추출
        auth_header = http_request.headers.get("Authorization")
        user_id = session_manager.extract_user_id_from_token(auth_header)
        
        # 세션 키 생성
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
            session_id=session_key,
            final_metrics=final_metrics,
            pool_to_save=[]  # V2에서는 pool 사용 안 함
        )
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"세션 종료 실패: {str(e)}")
