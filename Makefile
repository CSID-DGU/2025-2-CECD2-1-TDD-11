# 서버 기본 실행 타겟
run-server: run-localstack run-mariadb copy-config run-springboot
restart-server: stop-springboot run-springboot
debug-server: run-localstack run-mariadb copy-config debug-springboot
stop-server: stop-localstack stop-mariadb stop-springboot
clean-server: rm-localstack rm-mariadb clean-gradle

# AI 서버 기본 실행 타겟
install-ai: copy-config-ai set-virtual-environment
run-ai: run-redis run-ai-server
stop-ai: stop-redis stop-ai-server
clean-ai: rm-redis clean-ai-server

### 서버 관련 명령어 ###
# 1. LocalStack 실행
run-localstack:
	@bash ./scripts/server/setup-localstack.sh

# 2. MariaDB 실행
run-mariadb:
	@bash ./scripts/server/setup-mariadb.sh

# 2-1. 설정 파일 복사
copy-config:
	@cp server/src/main/resources/application-local.yml.example server/src/main/resources/application-local.yml || echo "[Makefile] 설정 파일 복사 실패: 이미 존재하거나 경로가 잘못되었습니다."
	@echo "[Makefile] Successfully copied application-local.yml"

# 3. Spring Boot 서버 실행
run-springboot:
	@bash ./scripts/server/run-springboot.sh

# 3-1. Spring Boot 서버 디버그 모드 실행
debug-springboot:
	@bash ./scripts/server/debug-springboot-trace.sh

# localstack, mariadb, springboot 종료
stop-localstack:
	@echo "[Makefile] Stopping LocalStack container..."
	@docker stop localstack 2>/dev/null || echo "[Makefile] There is no running LocalStack container."
	@echo "[Makefile] LocalStack container stopped."

stop-mariadb:
	@echo "[Makefile] Stopping MariaDB container..."
	@docker stop mariadb-talktobook 2>/dev/null || echo "[Makefile] There is no running MariaDB container."
	@echo "[Makefile] MariaDB container stopped."

stop-springboot:
	@echo "[Makefile] Stopping Spring Boot server..."
	@PID=$$(lsof -ti :8080) && \
	  kill $$PID && echo "[Makefile] Spring Boot process stopped (PID: $$PID)" || \
	  echo "[Makefile] There is no running Spring Boot server on port 8080." || true

rm-localstack:
	@echo "[Makefile] Removing LocalStack container..."
	@docker stop localstack 2>/dev/null || echo "[Makefile] There is no running LocalStack container."
	@docker rm localstack 2>/dev/null || true
	@echo "[Makefile] LocalStack container removed."

rm-mariadb:
	@echo "[Makefile] Removing MariaDB container..."
	@docker stop mariadb-talktobook 2>/dev/null || echo "[Makefile] There is no running MariaDB container."
	@docker rm mariadb-talktobook 2>/dev/null || true
	@echo "[Makefile] MariaDB container removed."

clean-gradle: rm-localstack rm-mariadb
	@echo "[Makefile] Cleaning gradle build..."
	@cd server && ./gradlew clean && rm -rf .gradle bin
	@echo "[Makefile] Successfully gradle build cleaned."


### AI 서버 관련 명령어 ###
# 1-1. 설정 파일 복사
copy-config-ai:
	@cp ai/serve/.env.example ai/serve/.env.development || echo "[Makefile] 설정 파일 복사 실패: 이미 존재하거나 경로가 잘못되었습니다."
	@echo "[Makefile] AZURE_OPENAI_API_KEY 를 유효한 openai key 값으로 설정해주세요 !!!"
	@echo "[Makefile] Successfully copied .env.development"

# AI 서버 실행
run-ai-server:
	@bash ./scripts/ai/run-ai-server.sh

# AI 서버 종료
stop-ai-server:
	@echo "[Makefile] Stopping AI server..."
	@PID=$$(lsof -ti :5000) && \
	  kill $$PID && echo "[Makefile] AI server process stopped (PID: $$PID)" || \
	  echo "[Makefile] There is no running AI server on port 3000." || true

# AI 서버 정리
clean-ai-server:
	@echo "[Makefile] Cleaning AI server..."
	@cd ai && rm -rf __pycache__ *.pyc venv
	@echo "[Makefile] Successfully AI server cleaned."	

# 테스트용 가상 token 생성
generate-test-tokens:
	@bash ./scripts/ai/generate-test-tokens.sh

# 가상 환경 설정
set-virtual-environment:
	@bash ./scripts/ai/set-virtual-environment.sh

# Redis 서버 실행
run-redis:
	@bash ./scripts/ai/run-redis.sh

# Redis 서버 종료
stop-redis:
	@echo "[Makefile] Stopping Redis container..."
	@docker stop redis-talktobook 2>/dev/null || echo "[Makefile] There is no running Redis container."
	@echo "[Makefile] Redis container stopped."

# Redis 서버 정리
rm-redis:
	@echo "[Makefile] Removing Redis container..."
	@docker stop redis-talktobook 2>/dev/null || echo "[Makefile] There is no running Redis container."
	@docker rm redis-talktobook 2>/dev/null || true
	@echo "[Makefile] Redis container removed."