#!/bin/bash

# ===============================================================
# LocalStack Container 실행 스크립트
# ===============================================================

echo "[scripts/server/localstack] Run LocalStack Container Script"

CONTAINER_NAME=localstack
IMAGE_NAME=localstack/localstack:latest
SERVICES=s3
PORT_MAIN=4566
PORT_EDGE=4571
DATA_DIR=./localstack
INIT_DIR=./aws

# 1. Docker 설치 확인
if ! command -v docker &> /dev/null; then
  echo "[scripts/server/localstack] There is no Docker installed. Please install Docker first. [https://docs.docker.com/engine/install/]"
  exit 1
fi

# 2. 기존 컨테이너 존재 여부 확인
EXISTING=$(docker ps -a --filter "name=$CONTAINER_NAME" --format "{{.Names}}")

if [ "$EXISTING" == "$CONTAINER_NAME" ]; then
  # 이미 실행 중인지 확인
  RUNNING=$(docker inspect -f '{{.State.Running}}' $CONTAINER_NAME)
  if [ "$RUNNING" == "true" ]; then
    echo "[scripts/server/localstack] LocalStack container is already running (http://localhost:$PORT_MAIN)"
    exit 0
  else
    echo "[scripts/server/localstack] Existing LocalStack container is stopped. Starting it again..."
    docker start $CONTAINER_NAME
  fi
else
  echo "[scripts/server/localstack] There is no LocalStack container found. Creating a new one..."
  docker run -d \
    --name $CONTAINER_NAME \
    -p $PORT_MAIN:4566 \
    -p $PORT_EDGE:4571 \
    -e SERVICES=$SERVICES \
    -e DATA_DIR=/tmp/localstack/data \
    -e DEBUG=1 \
    -e CLEAR_TMP_FOLDER=0 \
    -v "$DATA_DIR:/tmp/localstack" \
    -v "$INIT_DIR:/etc/localstack/init/ready.d" \
    -v /var/run/docker.sock:/var/run/docker.sock \
    --restart unless-stopped \
    $IMAGE_NAME
fi

# 3. 상태 확인
echo "[scripts/server/localstack] Waiting for LocalStack to be ready... (10~20 seconds)"
for i in {1..10}; do
  STATUS=$(docker inspect -f '{{.State.Health.Status}}' $CONTAINER_NAME 2>/dev/null | tr -d '"')
  if [ "$STATUS" == "healthy" ]; then
    echo "[scripts/server/localstack] Successfully started LocalStack container!"
    echo "[scripts/server/localstack] Endpoint: http://localhost:$PORT_MAIN"
    exit 0
  fi
  echo "[scripts/server/localstack] LocalStack is still initializing... (${i}/10)"
  sleep 3
done

echo "[scripts/server/localstack] LocalStack did not transition to healthy state. Check the logs:"
docker logs $CONTAINER_NAME | tail -n 50
