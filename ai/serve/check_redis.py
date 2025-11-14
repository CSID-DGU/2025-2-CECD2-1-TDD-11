import redis

redis_client = redis.Redis(host='localhost', port=6379, db=0, decode_responses=True)

print("KEYS *")
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
