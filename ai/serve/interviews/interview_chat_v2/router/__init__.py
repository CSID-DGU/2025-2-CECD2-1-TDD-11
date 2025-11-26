from fastapi import APIRouter, HTTPException, Depends, Request
from ..dto import InterviewChatV2RequestDto, InterviewChatV2ResponseDto, SessionStartRequestDto, SessionStartResponseDto, SessionEndRequestDto, SessionEndResponseDto
from stream import publish_persistence_message
import sys
import json
from pathlib import Path

# serve 폴더를 sys.path에 추가 (session_manager, auth import용)
sys.path.append(str(Path(__file__).parent.parent.parent.parent))
from session_manager import SessionManager
from datetime import datetime, timezone
from auth import AuthRequired
import sys
import os
from pathlib import Path
from stream.dto import Conversation, InterviewQuestion, InterviewPayload
from promptflow import load_flow

# flow 경로 추가
current_dir = Path(__file__).parent.parent.parent.parent.parent
flows_dir = current_dir / "flows" / "interviews" / "chat" / "interview_chat_v2"
sys.path.insert(0, str(flows_dir))

router = APIRouter()
session_manager = SessionManager()

# flow 로드
flow_path = flows_dir / "flow.dag.yaml"
flow = load_flow(str(flow_path))

@router.post("/start/{autobiography_id}", 
             response_model=SessionStartResponseDto, 
             summary ="인터뷰 시작",
             dependencies=[Depends(AuthRequired())],
             description="인터뷰 세션을 시작하고 첫 질문을 반환합니다.",
             tags=["인터뷰 (Interview)"],
             )
async def start_session(http_request: Request, autobiography_id: int, request: SessionStartRequestDto):
    """세션 시작"""
    try:
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
            answer_text="",
            user_id=user_id,
            autobiography_id=autobiography_id
        )
        
        first_question = result.get("next_question")
        
        # 세션 저장 (첫 질문 포함)
        if first_question:
            session_data = session_manager.load_session(session_key)
            if session_data:
                metrics = session_data.get("metrics", {})
                metrics["asked_total"] = metrics.get("asked_total", 0) + 1
                session_manager.save_session(session_key, metrics, first_question)
            else:
                session_manager.save_session(
                    session_key,
                    metrics={
                        "session_id": session_key,
                        "user_id": user_id,
                        "autobiography_id": autobiography_id,
                        "preferred_categories": request.preferred_categories,
                        "categories": [],
                        "engine_state": {"last_material_id": None, "last_material_streak": 0},
                        "asked_total": 1
                    },
                    last_question=first_question
                )
        
        # queue publish 용 데이터 세팅
        import json
        first_question_text = first_question.get("text") if isinstance(first_question, dict) else first_question
                
        material_id = first_question.get("material_id", []) if isinstance(first_question, dict) else []
        question = InterviewQuestion(
            questionText=first_question_text,
            questionOrder=0, # 질문 순서 정보가 없으므로 0으로 설정
            materials=json.dumps(material_id)  # JSON 문자열로 변환
        )
        
        ai_conversation = Conversation(
            content=first_question_text,
            conversationType="BOT",
            materials=json.dumps(material_id)  # JSON 문자열로 변환
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
        import traceback
        print(f"[ERROR] 세션 시작 실패: {str(e)}")
        print(f"[ERROR] 스택트레이스: {traceback.format_exc()}")
        raise HTTPException(status_code=500, detail=f"세션 시작 실패: {str(e)}")

@router.post("/chat/{autobiography_id}", 
             response_model=InterviewChatV2ResponseDto, 
             summary ="인터뷰 대화 진행",
             dependencies=[Depends(AuthRequired())],
             description="인터뷰 대화 진행 및 다음 질문 반환",
             tags=["인터뷰 (Interview)"],
             )
async def interview_chat(http_request: Request, autobiography_id: int, request: InterviewChatV2RequestDto):
    """인터뷰 대화"""
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
        
        # 다음 질문 생성
        result = flow(
            sessionId=session_key,
            answer_text=request.answer_text,
            user_id=user_id,
            autobiography_id=autobiography_id
        )
        
        next_question = result.get("next_question")
        last_answer_materials_id = result.get("last_answer_materials_id", [])
        
        # Flow에서 Redis에 직접 업데이트하므로 별도 저장 불필요
        
        
        # flow 실행 후 metrics 로드
        material_id = [list(result.get("next_question", {}).get("material_id", []))] # 다음 질문에 대한 material
        
        print(f"[INFO] result: {result}")
        next_question_text = next_question.get("text") if isinstance(next_question, dict) else next_question
        
        # queue publish 용 데이터 세팅
        question = InterviewQuestion(
            questionText=next_question_text,
            questionOrder=0, # 질문 순서 정보가 없으므로 0으로 설정
            materials=json.dumps(last_answer_materials_id) # JSON 문자열로 변환
        )
        
        human_conversation = Conversation(
            content=request.answer_text,
            conversationType="HUMAN",
            materials=json.dumps(last_answer_materials_id) # JSON 문자열로 변환
        )
        
        ai_conversation = Conversation(
            content=next_question_text,
            conversationType="BOT",
            materials=json.dumps(material_id) # JSON 문자열로 변환
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
        import traceback
        print(f"[ERROR] 질문 생성 실패: {str(e)}")
        print(f"[ERROR] 스택트레이스: {traceback.format_exc()}")
        raise HTTPException(status_code=500, detail=f"질문 생성 실패: {str(e)}")

@router.post("/end/{autobiography_id}", 
            response_model=SessionEndResponseDto, 
            dependencies=[Depends(AuthRequired())],
            summary ="인터뷰 종료",
            description="인터뷰 세션을 종료하고 최종 메트릭을 반환합니다.",
            tags=["인터뷰 (Interview)"],)
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
        import traceback
        print(f"[ERROR] 세션 종료 실패: {str(e)}")
        print(f"[ERROR] 스택트레이스: {traceback.format_exc()}")
        raise HTTPException(status_code=500, detail=f"세션 종료 실패: {str(e)}")

