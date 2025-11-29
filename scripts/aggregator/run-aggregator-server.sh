#!/bin/bash

# ==============================
# TalkToBook-Aggregator 서버 자동 재시작 스크립트
# ==============================

echo "[scripts/aggregator/run-aggregator-server] Run Aggregator Server Script"

# 현재 스크립트 위치 기준 절대경로 계산
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(dirname "$(dirname "$SCRIPT_DIR")")"
AGGREGATOR_DIR="$PROJECT_ROOT/aggregator"

cd "$AGGREGATOR_DIR" || { echo "[scripts/aggregator/run-aggregator-server] Failed to change directory to aggregator"; exit 1; }
echo "[scripts/aggregator/run-aggregator-server] Successfully moved to aggregator directory: $(pwd)"

# ===============================================================
# Step 1. Conda 환경 확인 및 활성화 (AI와 동일한 환경 사용)
# ===============================================================

ENV_NAME="talktobook-ai"
PORT=8001
ENV_FILE=".env"
LOG_FILE="aggregator_server.log"

if ! command -v conda &> /dev/null; then
    echo "[scripts/aggregator/run-aggregator-server] conda 명령어를 찾을 수 없습니다. 이 명령어로 가상 환경을 설치 해주세요. make install-ai"
    exit 1
fi

eval "$(conda shell.bash hook)"
conda activate "$ENV_NAME" || {
    echo "[scripts/aggregator/run-aggregator-server] Conda 환경 '$ENV_NAME' 활성화 실패"
    exit 1
}

# ===============================================================
# Step 2. 포트 및 프로세스 점검
# ===============================================================

echo "[scripts/aggregator/run-aggregator-server] Checking port $PORT..."

PIDS=$(lsof -ti :$PORT)

if [ -n "$PIDS" ]; then
  echo "[scripts/aggregator/run-aggregator-server] Port $PORT is in use by: $PIDS"
  echo "[scripts/aggregator/run-aggregator-server] Killing all processes on port $PORT..."
  echo "$PIDS" | xargs kill -9
  echo "[scripts/aggregator/run-aggregator-server] All PIDs killed."
else
  echo "[scripts/aggregator/run-aggregator-server] Port $PORT is free."
fi

# ===============================================================
# Step 3. 서버 실행 (백그라운드 + 로그 기록)
# ===============================================================

echo "[scripts/aggregator/run-aggregator-server] Starting Aggregator server on port $PORT..."

nohup python main.py > "$LOG_FILE" 2>&1 &

AGGREGATOR_PID=$!

echo "[scripts/aggregator/run-aggregator-server] Server started with PID: $AGGREGATOR_PID"
echo "[scripts/aggregator/run-aggregator-server] Logging to $LOG_FILE"

# ===============================================================
# Step 4. 서버 Health Check (최대 30초 대기)
# ===============================================================

HEALTH_URL="http://localhost:$PORT/health"

echo "[scripts/aggregator/run-aggregator-server] Health Check: $HEALTH_URL"

for i in {1..30}; do
  HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$HEALTH_URL")
  
  if [ "$HTTP_CODE" -eq 200 ]; then
    echo "[scripts/aggregator/run-aggregator-server] 200 OK — Server is healthy!"
    exit 0
  fi

  echo "[scripts/aggregator/run-aggregator-server] Waiting for server... ($i/30)"
  sleep 1
done

# ===============================================================
# Step 7. 실패 처리
# ===============================================================

echo "[scripts/aggregator/run-aggregator-server] Server failed to start within 30 seconds."
echo "[scripts/aggregator/run-aggregator-server] Check logs: $LOG_FILE"
exit 1
