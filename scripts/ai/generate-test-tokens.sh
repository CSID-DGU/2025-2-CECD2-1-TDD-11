#!/bin/bash

# ==============================
# TalkToBook-AI 서버 자동 재시작 스크립트
# ==============================

echo "[scripts/ai/generate-test-token] Generate Test Token Script"

# 현재 스크립트 위치 기준 절대경로 계산
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(dirname "$(dirname "$SCRIPT_DIR")")"
AI_DIR="$PROJECT_ROOT/ai/serve"

cd "$AI_DIR" || { echo "[scripts/ai/generate-test-token] Failed to change directory to serve"; exit 1; }
echo "[scripts/ai/generate-test-token] Successfully moved to serve directory: $(pwd)"

# ===============================================================
# Step 1. Conda 환경 확인 및 활성화
# ===============================================================

ENV_NAME="talktobook-ai"
PORT=3000
ENV_FILE=".env.development"

if ! command -v conda &> /dev/null; then
    echo "[scripts/ai/generate-test-token] conda 명령어를 찾을 수 없습니다. 이 명령어로 가상 환경을 설치 해주세요. make install-ai"
    exit 1
fi

eval "$(conda shell.bash hook)"
conda activate "$ENV_NAME" || {
    echo "[scripts/ai/generate-test-token] Conda 환경 '$ENV_NAME' 활성화 실패"
    exit 1
}

# ===============================================================
# Step 2. 포트 및 프로세스 점검
# ===============================================================

echo "[scripts/ai/generate-test-token] Checking if port $PORT is already in use..."

if lsof -i :$PORT >/dev/null 2>&1; then
  echo "[scripts/ai/generate-test-token] Port $PORT is already in use. Checking health endpoint..."

  HEALTH=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$PORT/docs)

  if [ "$HEALTH" -eq 200 ]; then
    echo "[scripts/ai/generate-test-token] 200 Success — 서버가 정상 동작 중입니다."
    echo "[scripts/ai/generate-test-token] Test token generating..."
    python generate_test_token.py
    exit 0
  else
    echo "[scripts/ai/generate-test-token] Port $PORT 점유 중이지만 Health Check 실패. 기존 프로세스 종료..."
    PID=$(lsof -ti :$PORT)
    kill -9 $PID
    echo "[scripts/ai/generate-test-token] Killed process on port $PORT (PID: $PID)"
  fi
else
  echo "[scripts/ai/generate-test-token] Port $PORT is free. Proceeding with server start."
fi