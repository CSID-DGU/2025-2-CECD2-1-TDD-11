import redis
import json
import time
from typing import Dict, Optional, Any
from interviews.interview_chat_v2.dto import MetricsDto

class SessionManager:
    def __init__(self, redis_host: str = 'localhost', redis_port: int = 6379, redis_db: int = 0):
        self.redis_client = redis.Redis(host=redis_host, port=redis_port, db=redis_db, decode_responses=True)
        self.session_ttl = 3600  # 1시간
    
    def save_session(self, session_id: str, metrics: Dict[str, Any], last_question: Optional[Dict[str, Any]] = None):
        """세션 상태 저장"""
        session_data = {
            "metrics": metrics,
            "last_question": last_question,
            "updated_at": time.time()
        }
        self.redis_client.setex(f"session:{session_id}", self.session_ttl, json.dumps(session_data))
    
    def load_session(self, session_id: str) -> Optional[Dict[str, Any]]:
        """세션 상태 로드"""
        data = self.redis_client.get(f"session:{session_id}")
        if data and isinstance(data, str):
            return json.loads(data)
        return None
    
    def create_session(self, session_id: str, preferred_categories: Optional[list] = None, previous_metrics: Optional[Dict] = None):
        """새 세션 생성"""
        if previous_metrics:
            self.save_session(session_id, previous_metrics, None)
        else:
            initial_metrics = {
                "sessionId": session_id,
                "preferred_categories": preferred_categories or []
            }
            self.save_session(session_id, initial_metrics, None)
    
    def get_session_for_flow(self, session_id: str) -> Dict[str, Any]:
        """flow에 전달할 세션 데이터 반환"""
        session_data = self.load_session(session_id)
        if not session_data:
            return {"sessionId": session_id, "isNewSession": True}
        
        return {
            "sessionId": session_id,
            "isNewSession": False,
            "metrics": session_data.get("metrics", {}),
            "last_question": session_data.get("last_question")
        }
    
    def delete_session(self, session_id: str):
        """세션 삭제"""
        self.redis_client.delete(f"session:{session_id}")
    
    def extend_session(self, session_id: str):
        """세션 TTL 연장"""
        self.redis_client.expire(f"session:{session_id}", self.session_ttl)