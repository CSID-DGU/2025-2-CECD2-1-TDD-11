import redis
import os
from dotenv import load_dotenv

load_dotenv()

redis_host = os.getenv('REDIS_HOST', 'localhost')
redis_port = int(os.getenv('REDIS_PORT', 6379))
redis_client = redis.Redis(host=redis_host, port=redis_port, db=0, decode_responses=True)

print(f"Connecting to Redis at {redis_host}:{redis_port}")

print("\nKEYS *")
all_keys = redis_client.keys("*")
for key in all_keys:
    print(key)

print("\nKEYS session:*")
session_keys = redis_client.keys("session:*")
for key in session_keys:
    print(key)

print("\n=== 각 세션 데이터 ===")
for key in session_keys:
    print(f"\nGET {key}")
    data = redis_client.get(key)
    print(data)
