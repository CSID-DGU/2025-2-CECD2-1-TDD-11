#!/bin/bash

# ===============================================================
# RabbitMQ Container 실행 스크립트
# ===============================================================

echo "[scripts/stream/setup-rabbitmq] Run RabbitMQ Container Script"
# RabbitMQ 컨테이너 이름
CONTAINER_NAME=talktobook-stream
RABBITMQ_PORT=5672
MANAGEMENT_PORT=15672

# 1. Docker 설치 확인
if ! command -v docker &> /dev/null; then
  echo "[scripts/stream/setup-rabbitmq] There is no Docker installed. Please install Docker first. [https://docs.docker.com/engine/install/]"
  exit 1
fi 

# 2. RabbitMQ 컨테이너가 이미 존재하는지 확인
EXISTING_CONTAINER=$(docker ps -a --filter "name=$CONTAINER_NAME" --format "{{.Names}}")
if [ "$EXISTING_CONTAINER" == "$CONTAINER_NAME" ]; then
  # 이미 실행 중인 경우
  if [ "$(docker inspect -f '{{.State.Running}}' $CONTAINER_NAME)" == "true" ]; then
    echo "[scripts/stream/setup-rabbitmq] RabbitMQ container ($CONTAINER_NAME) is already running."
  else
    echo "[scripts/stream/setup-rabbitmq] RabbitMQ container ($CONTAINER_NAME) is stopped. Starting it again..."
    docker start $CONTAINER_NAME
  fi
else
  # 새로 컨테이너 생성
  docker run -d \
    --name $CONTAINER_NAME \
    -p $RABBITMQ_PORT:5672 \
    -p $MANAGEMENT_PORT:15672 \
    --restart unless-stopped \
    rabbitmq:3-management
  echo "[scripts/stream/setup-rabbitmq] Successfully created RabbitMQ container ($CONTAINER_NAME)."
fi

echo "[scripts/stream/setup-rabbitmq] Waiting for RabbitMQ to be ready..."
for i in {1..10}; do
  if docker exec -i $CONTAINER_NAME rabbitmqctl status > /dev/null 2>&1; then
    echo "[scripts/stream/setup-rabbitmq] RabbitMQ is ready!"
    break
  fi
  echo "[scripts/stream/setup-rabbitmq] RabbitMQ is still starting... ($i/10)"
  sleep 3
done

# 3. 계정 생성
RABBITMQ_USER="talktobook"
RABBITMQ_PASSWORD="talktobook_password"

docker exec -i $CONTAINER_NAME rabbitmqctl add_user $RABBITMQ_USER $RABBITMQ_PASSWORD 2>/dev/null || \
  echo "[scripts/stream/setup-rabbitmq] User $RABBITMQ_USER already exists. Skipping user creation."

docker exec -i $CONTAINER_NAME rabbitmqctl set_user_tags $RABBITMQ_USER administrator
docker exec -i $CONTAINER_NAME rabbitmqctl set_permissions -p / $RABBITMQ_USER ".*" ".*" ".*"

# 4. 상태 확인
if docker exec -i $CONTAINER_NAME rabbitmqctl status > /dev/null 2>&1; then
  echo "[scripts/stream/setup-rabbitmq] RabbitMQ is running successfully: localhost:$RABBITMQ_PORT"
  echo "[scripts/stream/setup-rabbitmq] Management UI available at: http://localhost:$MANAGEMENT_PORT (User: $RABBITMQ_USER, Password: $RABBITMQ_PASSWORD)"
else
  echo "[scripts/stream/setup-rabbitmq] Failed to connect to RabbitMQ. Check the logs:"
  docker logs $CONTAINER_NAME 2>&1 | grep -E "ERROR|Fail|Crash" | tail -n 10 || echo "(no error logs found)"
fi