#!/bin/bash

# ===============================================================
# Redis Container 실행 스크립트
# ===============================================================

echo "[scripts/ai/redis] Run Redis Container Script"

# Redis 컨테이너 이름
CONTAINER_NAME=redis-talktobook-ai
DB_PORT=6380

# 1. Docker 설치 확인
if ! command -v docker &> /dev/null; then
  echo "[scripts/ai/redis] There is no Docker installed. Please install Docker first. [https://docs.docker.com/engine/install/]"
  exit 1
fi

# 2. Redis 컨테이너가 이미 존재하는지 확인
EXISTING_CONTAINER=$(docker ps -a --filter "name=$CONTAINER_NAME" --format "{{.Names}}")

if [ "$EXISTING_CONTAINER" == "$CONTAINER_NAME" ]; then
  # 이미 실행 중인 경우
  if [ "$(docker inspect -f '{{.State.Running}}' $CONTAINER_NAME)" == "true" ]; then
    echo "[scripts/ai/redis] Redis container ($CONTAINER_NAME) is already running."
  else
    echo "[scripts/ai/redis] Redis container ($CONTAINER_NAME) is stopped. Starting it again..."
    docker start $CONTAINER_NAME
  fi
else
  # 새로 컨테이너 생성
  echo "[scripts/ai/redis] Creating a new Redis container..."
  docker run -d \
    --name $CONTAINER_NAME \
    -p $DB_PORT:6379 \
    --restart unless-stopped \
    redis:latest
fi

echo "[scripts/ai/redis] Waiting for Redis to be ready..."
for i in {1..10}; do
  if docker exec -i $CONTAINER_NAME redis-cli ping > /dev/null 2>&1; then
    echo "[scripts/ai/redis] Redis is ready!"
    break
  fi
  echo "[scripts/ai/redis] Redis is still starting... ($i/10)"
  sleep 3
done

# 3. 상태 확인
if docker exec -i $CONTAINER_NAME redis-cli ping > /dev/null 2>&1; then
  echo "[scripts/ai/redis] Redis is running successfully: localhost:$DB_PORT"
else
  echo "[scripts/ai/redis] Failed to connect to Redis. Check the logs:"
  docker logs $CONTAINER_NAME 2>&1 | grep -E "ERROR|Fail|Crash" | tail -n 10 || echo "(no error logs found)"
fi
