# 기본 실행 타겟
run-server: run-localstack run-mariadb copy-config run-springboot
debug-server: run-localstack run-mariadb copy-config debug-springboot
stop-server: stop-localstack stop-mariadb stop-springboot
clean-server: rm-localstack rm-mariadb clean-gradle

# 1. LocalStack 실행
run-localstack:
	@bash ./scripts/server/setup-localstack.sh

# 2. MariaDB 실행
run-mariadb:
	@bash ./scripts/server/setup-mariadb.sh

# 2-1. 설정 파일 복사
copy-config:
	@cp server/src/main/resources/application-local.yml.example server/src/main/resources/application-local.yml || echo "⚠️ 설정 파일 복사 실패: 이미 존재하거나 경로가 잘못되었습니다."
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
	@if command -v lsof >/dev/null 2>&1; then \
		PID=$$(lsof -ti :8080 || true); \
		if [ -n "$$PID" ]; then \
			kill $$PID && echo "[Makefile] Spring Boot process stopped (PID: $$PID)"; \
		else \
			echo "[Makefile] There is no running Spring Boot server on port 8080."; \
		fi; \
	elif command -v powershell >/dev/null 2>&1; then \
		powershell -Command '$$p = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty OwningProcess; if ($$p) { Stop-Process -Id $$p -Force; Write-Output "[Makefile] Spring Boot process stopped (PID: $$p)"; } else { Write-Output "[Makefile] There is no running Spring Boot server on port 8080." }'; \
	else \
		echo "[Makefile] Could not find lsof or powershell. Please stop Spring Boot manually (Ctrl+C)."; \
	fi

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
