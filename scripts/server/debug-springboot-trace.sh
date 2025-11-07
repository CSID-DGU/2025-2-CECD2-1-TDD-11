#!/b.  n/bash

# ===============================================================
# Spring Boot TRACE 모드 실행 스크립트
# ===============================================================

echo "[scripts/server/debug-springboot] Debug Trace Springboot Script"

# 현재 스크립트 위치 기준 절대경로 계산
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(dirname "$(dirname "$SCRIPT_DIR")")"
SERVER_DIR="$PROJECT_ROOT/server"

cd "$SERVER_DIR" || { echo "Failed to change directory to server"; exit 1; }
echo "[scripts/server/debug-springboot] Successfully moved to server directory. $(pwd)"

# ===============================================================
# Step 1. 8080 포트 점검
# ===============================================================

PORT=8080
echo "[scripts/server/debug-springboot] Checking if port $PORT is already in use..."

if lsof -i :$PORT >/dev/null 2>&1; then
  echo "[scripts/server/debug-springboot] Port $PORT is already in use. Checking Spring Boot health..."

  HEALTH=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$PORT/actuator/health)

  if [ "$HEALTH" -eq 200 ]; then
    echo "[scripts/server/debug-springboot] curl -v http://localhost:$PORT/actuator/health"
    echo "[scripts/server/debug-springboot] 200 Success"
    echo "[scripts/server/debug-springboot] You can check logs in server/boot.log"
    exit 0
  else
    echo "[scripts/server/debug-springboot] Port $PORT is occupied, but health check failed. Killing existing process..."
    PID=$(lsof -ti :$PORT)
    kill -9 $PID
    echo "[scripts/server/debug-springboot] Killed process on port $PORT (PID: $PID)"
  fi
else
  echo "[scripts/server/debug-springboot] Port $PORT is free. Proceeding with build and run."
fi

# ===============================================================
# Step 2. Spring Boot 빌드
# ===============================================================

echo "[scripts/server/debug-springboot] Building Spring Boot application..."
./gradlew build -x test -Pprofile=local

if [ $? -ne 0 ]; then
  echo "[scripts/server/debug-springboot] Failed to build the Spring Boot application."
  exit 1
fi

echo "[scripts/server/debug-springboot] Successfully built the Spring Boot application."

# ===============================================================
# Step 3. 서버 실행 (백그라운드)
# ===============================================================

echo "[scripts/server/debug-springboot] Starting Spring Boot application on port $PORT..."
nohup ./gradlew bootRun \
  --args='--debug --trace --spring.main.banner-mode=console --spring.output.ansi.enabled=ALWAYS' \
  > boot.log 2>&1 &

# 방금 백그라운드로 실행된 프로세스의 PID(Process ID)”를 변수에 저장하는 구문
BOOT_PID=$!
echo "[scripts/server/debug-springboot] BootRun process started (PID: $BOOT_PID)"

# ===============================================================
# Step 4. Health Check (최대 30초 대기)
# ===============================================================
echo "[scripts/server/debug-springboot] Waiting for server to start (checking /actuator/health)..."
for i in {1..30}; do
  HEALTH=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:$PORT/actuator/health")
  if [ "$HEALTH" -eq 200 ]; then
    echo "[scripts/server/debug-springboot] Server is UP (HTTP 200)"
    echo "[scripts/server/debug-springboot] You can check detailed logs in $LOG_FILE"
    exit 0
  fi
  sleep 1
done

# ===============================================================
# Step 5. 실패 처리
# ===============================================================
echo "[scripts/server/debug-springboot] Server did not respond within 30 seconds."
echo "[scripts/server/debug-springboot] Check server/boot.log for details."
exit 1
