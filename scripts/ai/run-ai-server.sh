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

echo "[scripts/ai/run-ai-server] Checking if port $PORT is already in use..."

if lsof -i :$PORT >/dev/null 2>&1; then
  echo "[scripts/ai/run-ai-server] Port $PORT is already in use. Checking health endpoint..."

  HEALTH=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$PORT/docs)

  if [ "$HEALTH" -eq 200 ]; then
    echo "[scripts/ai/run-ai-server] curl -v http://localhost:$PORT/docs"
    echo "[scripts/ai/run-ai-server] 200 Success — 이미 실행 중인 서버가 정상 동작 중입니다."
    echo "[scripts/ai/run-ai-server] You can check logs in ai/serve/$LOG_FILE"
    exit 0
  else
    echo "[scripts/ai/run-ai-server] Port $PORT 점유 중이지만 Health Check 실패. 기존 프로세스 종료..."
    PID=$(lsof -ti :$PORT)
    kill -9 $PID
    echo "[scripts/ai/run-ai-server] Killed process on port $PORT (PID: $PID)"
  fi
else
  echo "[scripts/ai/run-ai-server] Port $PORT is free. Proceeding with server start."
fi

# ===============================================================
# Step 3. 서버 실행 (백그라운드 + 로그 기록)
# ===============================================================

echo "[scripts/ai/run-ai-server] Starting FastAPI (Uvicorn) server on port $PORT..."
nohup python -m uvicorn main:app --env-file "$ENV_FILE" --port "$PORT" --reload --log-level info > "$LOG_FILE" 2>&1 &

AI_PID=$!
echo "[scripts/ai/run-ai-server] Server process started (PID: $AI_PID)"
echo "[scripts/ai/run-ai-server] Logging to: ai/serve/$LOG_FILE"

# ===============================================================
# Step 4. 서버 Health Check (최대 30초 대기)
# ===============================================================

echo "[scripts/ai/run-ai-server] Waiting for server to start..."
for i in {1..30}; do
  HEALTH=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$PORT/docs)
  if [ "$HEALTH" -eq 200 ]; then
    echo "[scripts/ai/run-ai-server] curl -v http://localhost:$PORT/docs"
    echo "[scripts/ai/run-ai-server] 200 Success — 서버가 정상적으로 실행되었습니다!"
    echo "[scripts/ai/run-ai-server] You can check logs in ai/serve/$LOG_FILE"
    exit 0
  fi
  echo "[scripts/ai/run-ai-server] 서버 부팅 중... (시도 $i/30)"
  sleep 1
done

# ===============================================================
# Step 5. 실패 처리
# ===============================================================

echo "[scripts/ai/run-ai-server] 서버가 30초 내에 응답하지 않았습니다."
echo "[scripts/ai/run-ai-server] Check ai/serve/$LOG_FILE for details."
echo "[scripts/ai/run-ai-server] If you need detailed debugging, try \"tail -f ai/serve/$LOG_FILE\""
exit 1