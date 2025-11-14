#!/bin/bash

# ==============================
# TalkToBook-AI 서버 자동 재시작 스크립트
# ==============================

echo "[scripts/ai/run-ai-server] Run AI Server Script"

# 현재 스크립트 위치 기준 절대경로 계산
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(dirname "$(dirname "$SCRIPT_DIR")")"
AI_DIR="$PROJECT_ROOT/ai/serve"

cd "$AI_DIR" || { echo "[scripts/ai/run-ai-server] Failed to change directory to serve"; exit 1; }
echo "[scripts/ai/run-ai-server] Successfully moved to serve directory: $(pwd)"

# ===============================================================
# Step 1. Conda 환경 확인 및 활성화
# ===============================================================

ENV_NAME="talktobook-ai"
PORT=3000
ENV_FILE=".env.development"
LOG_FILE="ai_server.log"

if ! command -v conda &> /dev/null; then
    echo "[scripts/ai/run-ai-server] conda 명령어를 찾을 수 없습니다. 이 명령어로 가상 환경을 설치 해주세요. make install-ai"
    exit 1
fi

eval "$(conda shell.bash hook)"
conda activate "$ENV_NAME" || {
    echo "[scripts/ai/run-ai-server] Conda 환경 '$ENV_NAME' 활성화 실패"
    exit 1
}

# ===============================================================
# Step 2. 포트 및 프로세스 점검
# ===============================================================

echo "[scripts/ai/run-ai-server] Checking port $PORT..."

PIDS=$(lsof -ti :$PORT)

if [ -n "$PIDS" ]; then
  echo "[scripts/ai/run-ai-server] Port $PORT is in use by: $PIDS"
  echo "[scripts/ai/run-ai-server] Killing all processes on port $PORT..."
  echo "$PIDS" | xargs kill -9
  echo "[scripts/ai/run-ai-server] All PIDs killed."
else
  echo "[scripts/ai/run-ai-server] Port $PORT is free."
fi

# ===============================================================
# Step 3. 서버 실행 (백그라운드 + 로그 기록)
# ===============================================================

echo "[scripts/ai/run-ai-server] Starting FastAPI server on port $PORT..."

nohup python -m uvicorn main:app \
  --env-file "$ENV_FILE" \
  --port "$PORT" \
  --reload \
  --log-level info \
  > "$LOG_FILE" 2>&1 &

AI_PID=$!

echo "[scripts/ai/run-ai-server] Server started with PID: $AI_PID"
echo "[scripts/ai/run-ai-server] Logging to $LOG_FILE"

# ===============================================================
# Step 4. 서버 Health Check (최대 30초 대기)
# ===============================================================

HEALTH_URL="http://localhost:$PORT/docs"

echo "[scripts/ai/run-ai-server] Health Check: $HEALTH_URL"

for i in {1..30}; do
  HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$HEALTH_URL")
  
  if [ "$HTTP_CODE" -eq 200 ]; then
    echo "[scripts/ai/run-ai-server] 200 OK — Server is healthy!"
    exit 0
  fi

  echo "[scripts/ai/run-ai-server] Waiting for server... ($i/30)"
  sleep 1
done

# ===============================================================
# Step 5. 실패 처리
# ===============================================================

echo "[scripts/ai/run-ai-server] Server failed to start within 30 seconds."
echo "[scripts/ai/run-ai-server] Check logs: $LOG_FILE"
exit 1