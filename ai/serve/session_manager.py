import redis
import json
import time
from typing import Dict, Optional, Any
from interviews.interview_chat_v2.dto import MetricsDto
from auth import verify_token

class SessionManager:
    
    def extract_user_id_from_token(self, auth_header: Optional[str]) -> int:
        """JWT 토큰에서 userId 추출"""
        if not auth_header or not auth_header.startswith("Bearer "):
            raise ValueError("Invalid authorization header")
        
        token = auth_header.split(" ")[1]
        member_session = verify_token(token)
        return member_session.member_id
    
    def generate_session_key(self, user_id: int, autobiography_id: int) -> str:
        """세션 키 생성: {userId}:{autobiographyId}"""
        return f"{user_id}:{autobiography_id}"
    

    def __init__(self, redis_host: str = 'localhost', redis_port: int = 6379, redis_db: int = 0):
        self.redis_client = redis.Redis(host=redis_host, port=redis_port, db=redis_db, decode_responses=True)
        self.session_ttl = None  # TTL 없음 (영구 저장)
    
    def save_session(self, session_key: str, metrics: Dict[str, Any], last_question: Optional[Dict[str, Any]] = None):
        """세션 상태 저장"""
        session_data = {
            "metrics": metrics,
            "last_question": last_question,
            "updated_at": time.time()
        }
        if self.session_ttl:
            self.redis_client.setex(f"session:{session_key}", self.session_ttl, json.dumps(session_data))
        else:
            self.redis_client.set(f"session:{session_key}", json.dumps(session_data))
    
    def load_session(self, session_key: str) -> Optional[Dict[str, Any]]:
        """세션 상태 로드"""
        data = self.redis_client.get(f"session:{session_key}")
        if data and isinstance(data, str):
            return json.loads(data)
        return None
    
    def create_session(self, session_key: str, user_id: int, autobiography_id: int, preferred_categories: Optional[list] = None, previous_metrics: Optional[Dict] = None):
        """새 세션 생성"""
        if previous_metrics:
            self.save_session(session_key, previous_metrics, None)
        else:
            initial_metrics = {
                "session_id": session_key,
                "user_id": user_id,
                "autobiography_id": autobiography_id,
                "preferred_categories": preferred_categories or []
            }
            self.save_session(session_key, initial_metrics, None)
    
    def get_session_for_flow(self, session_key: str) -> Dict[str, Any]:
        """flow에 전달할 세션 데이터 반환"""
        session_data = self.load_session(session_key)
        if not session_data:
            return {"sessionId": session_key, "isNewSession": True}
        
        return {
            "sessionId": session_key,
            "isNewSession": False,
            "metrics": session_data.get("metrics", {}),
            "last_question": session_data.get("last_question")
        }
    
    def delete_session(self, session_key: str):
        """세션 삭제"""
        self.redis_client.delete(f"session:{session_key}")