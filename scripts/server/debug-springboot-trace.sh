#!/bin/bash

# ===============================================================
# Spring Boot TRACE 모드 실행 스크립트
# ===============================================================

echo "[scripts/server/debug-springboot] Debug Trace Springboot Script"

# 현재 스크립트 위치 기준 절대경로 계산
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(dirname "$(dirname "$SCRIPT_DIR")")"
SERVER_DIR="$PROJECT_ROOT/server"

cd "$SERVER_DIR" || { echo "Failed to change directory to server"; exit 1; }
echo "[scripts/server/debug-springboot] Successfully moved to server directory."
pwd

echo ">>> Running Spring Boot with TRACE logs..."
./gradlew bootRun --args='--debug --trace --spring.main.banner-mode=console --spring.output.ansi.enabled=ALWAYS'
