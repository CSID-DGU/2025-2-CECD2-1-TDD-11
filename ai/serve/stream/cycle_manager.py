import redis
import json
import os
from logs import get_logger

logger = get_logger()

class CycleManager:
    def __init__(self):
        redis_host = os.getenv('REDIS_HOST', 'localhost')
        redis_port = int(os.getenv('REDIS_PORT', 6379))
        self.redis_client = redis.Redis(host=redis_host, port=redis_port, db=0, decode_responses=True)
        logger.info(f"[CYCLE] Connected to Redis at {redis_host}:{redis_port}")
    
    def init_cycle(self, cycle_id: str, expected_count: int, autobiography_id: int, user_id: int):
        """Cycle 초기화"""
        cycle_data = {
            "cycleId": cycle_id,
            "expectedCount": expected_count,
            "autobiographyId": autobiography_id,
            "userId": user_id,
            "completedCount": 0
        }
        self.redis_client.set(f"cycle:{cycle_id}", json.dumps(cycle_data))
        logger.info(f"[CYCLE] Initialized cycle_id={cycle_id} expected={expected_count}")
    
    def increment_completed(self, cycle_id: str) -> bool:
        """완료 카운트 증가 및 완료 여부 반환"""
        cycle_key = f"cycle:{cycle_id}"
        cycle_data_raw = self.redis_client.get(cycle_key)
        
        if not cycle_data_raw:
            logger.warning(f"[CYCLE] Cycle not found cycle_id={cycle_id}")
            return False
        
        cycle_data = json.loads(cycle_data_raw)
        cycle_data["completedCount"] += 1
        
        self.redis_client.set(cycle_key, json.dumps(cycle_data))
        
        is_last = cycle_data["completedCount"] >= cycle_data["expectedCount"]
        logger.info(f"[CYCLE] Updated cycle_id={cycle_id} completed={cycle_data['completedCount']}/{cycle_data['expectedCount']} is_last={is_last}")
        
        if is_last:
            self.redis_client.delete(cycle_key)
            logger.info(f"[CYCLE] Deleted completed cycle cycle_id={cycle_id}")
        
        return is_last
