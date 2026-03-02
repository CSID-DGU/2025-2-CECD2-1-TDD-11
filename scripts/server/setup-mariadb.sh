#!/bin/bash

# ===============================================================
# MariaDB Container 실행 스크립트
# ===============================================================

echo "[scripts/server/mariadb] Run MariaDB Container Script"

# MariaDB 컨테이너 이름
CONTAINER_NAME=mariadb-talktobook
DB_PORT=3311
DB_USER=talktobook
DB_PASSWORD=password
DB_NAME=talktobook

# 1. Docker 설치 확인
if ! command -v docker &> /dev/null; then
  echo "[scripts/server/mariadb] There is no Docker installed. Please install Docker first. [https://docs.docker.com/engine/install/]"
  exit 1
fi

# 2. MariaDB 컨테이너가 이미 존재하는지 확인
EXISTING_CONTAINER=$(docker ps -a --filter "name=$CONTAINER_NAME" --format "{{.Names}}")

if [ "$EXISTING_CONTAINER" == "$CONTAINER_NAME" ]; then
  # 이미 실행 중인 경우
  if [ "$(docker inspect -f '{{.State.Running}}' $CONTAINER_NAME)" == "true" ]; then
    echo "[scripts/server/mariadb] MariaDB container ($CONTAINER_NAME) is already running."
  else
    echo "[scripts/server/mariadb] MariaDB container ($CONTAINER_NAME) is stopped. Starting it again..."
    docker start $CONTAINER_NAME
  fi
else
  # 새로 컨테이너 생성
  echo "[scripts/server/mariadb] Creating a new MariaDB container..."
  docker run -d \
    --name $CONTAINER_NAME \
    -e MARIADB_ROOT_PASSWORD=rootpassword \
    -e MARIADB_DATABASE=$DB_NAME \
    -e MARIADB_USER=$DB_USER \
    -e MARIADB_PASSWORD=$DB_PASSWORD \
    -p $DB_PORT:3306 \
    --restart unless-stopped \
    mariadb:11.4.2 \
    --character-set-server=utf8mb4 \
    --collation-server=utf8mb4_unicode_ci
fi

echo "[scripts/server/mariadb] Waiting for MariaDB to be ready..."
for i in {1..10}; do
  if docker exec -i $CONTAINER_NAME mariadb -u$DB_USER -p$DB_PASSWORD -e "SELECT 1;" > /dev/null 2>&1; then
    echo "[scripts/server/mariadb] MariaDB is ready!"
    break
  fi
  echo "[scripts/server/mariadb] MariaDB is still starting... ($i/10)"
  sleep 3
done

# 3. 상태 확인
if docker exec -i $CONTAINER_NAME mariadb -u$DB_USER -p$DB_PASSWORD -e "SHOW DATABASES;" > /dev/null 2>&1; then
  echo "[scripts/server/mariadb] MariaDB is running successfully: localhost:$DB_PORT"
else
  echo "[scripts/server/mariadb] Failed to connect to MariaDB. Check the logs:"
  docker logs $CONTAINER_NAME 2>&1 | grep -E "ERROR|Fail|Crash" | tail -n 10 || echo "(no error logs found)"
fi
