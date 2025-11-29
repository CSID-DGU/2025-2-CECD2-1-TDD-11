.PHONY: help run-server restart-server debug-server stop-server clean-server bootstrap deploy deploy-all deploy-server deploy-ai deploy-stream upload-compose clean

# Default AWS profile
AWS_PROFILE ?= talktobook
CDK_DIR = infra


help: ## Show this help message
	@echo "TalkToBook Infrastructure Commands:"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'


# 서버 기본 실행 타겟
run-server: run-localstack run-mariadb copy-config run-springboot
restart-server: stop-springboot run-springboot
debug-server: run-localstack run-mariadb copy-config debug-springboot
stop-server: stop-localstack stop-mariadb stop-springboot
clean-server: rm-localstack rm-mariadb clean-gradle

# AI 서버 기본 실행 타겟
install-ai: copy-config-ai set-virtual-environment
run-ai: run-redis-ai run-ai-server
stop-ai: stop-redis stop-ai-server
clean-ai: rm-redis clean-ai-server

# Aggregator 기본 실행 타겟
run-aggregator: run-redis-aggregator run-aggregator-server
stop-aggregator: stop-aggregator-server stop-redis-aggregator
clean-aggregator: rm-redis-aggregator clean-aggregator-server

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

run-redis-server:
	@bash ./scripts/server/run-redis.sh

stop-redis-server:
	@echo "[Makefile] Stopping Redis container..."
	@docker stop redis-talktobook-server 2>/dev/null || echo "[Makefile] There is no running Redis container."
	@echo "[Makefile] Redis container stopped."

rm-redis-server:
	@echo "[Makefile] Removing Redis container..."
	@docker stop redis-talktobook-server 2>/dev/null || echo "[Makefile] There is no running Redis container."
	@docker rm redis-talktobook-server 2>/dev/null || true
	@echo "[Makefile] Redis container removed."

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
run-redis-ai:
	@bash ./scripts/ai/run-redis.sh

# Redis 서버 종료
stop-redis-ai:
	@echo "[Makefile] Stopping Redis container..."
	@docker stop redis-talktobook-ai 2>/dev/null || echo "[Makefile] There is no running Redis container."
	@echo "[Makefile] Redis container stopped."

# Redis 서버 정리
rm-redis-ai:
	@echo "[Makefile] Removing Redis container..."
	@docker stop redis-talktobook-ai 2>/dev/null || echo "[Makefile] There is no running Redis container."
	@docker rm redis-talktobook-ai 2>/dev/null || true
	@echo "[Makefile] Redis container removed."

### Queue 관련 명령어 ###
# RabbitMQ compose 실행
run-rabbitmq:
	@bash ./scripts/stream/setup-rabbitmq.sh

remove-rabbitmq:
	docker compose -f deploy/docker-compose.stream.prod.yml down

clean-rabbitmq:
	docker compose -f deploy/docker-compose.stream.prod.yml down -v --rmi all --remove-orphans

### Aggregator 관련 명령어 ###
# Aggregator 서버 실행
run-aggregator-server: ## Run aggregator server
	@echo "[Makefile] Starting Aggregator server..."
	@bash ./scripts/aggregator/run-aggregator-server.sh

# Aggregator 서버 종료
stop-aggregator-server: ## Stop aggregator server
	@echo "[Makefile] Stopping Aggregator server..."
	@PID=$$(lsof -ti :8001 2>/dev/null) && \
	  kill $$PID && echo "[Makefile] Aggregator server process stopped (PID: $$PID)" || \
	  echo "[Makefile] There is no running Aggregator server on port 8001."

# Aggregator Redis 실행
run-redis-aggregator: ## Run Redis for aggregator
	@echo "[Makefile] Starting Redis for Aggregator..."
	@bash ./scripts/aggregator/run-redis.sh

# Aggregator Redis 종료
stop-redis-aggregator: ## Stop Redis for aggregator
	@echo "[Makefile] Stopping Redis for Aggregator..."
	@docker stop redis-talktobook-aggregator 2>/dev/null || echo "[Makefile] There is no running Redis container."

# Aggregator Redis 정리
rm-redis-aggregator: ## Remove Redis for aggregator
	@echo "[Makefile] Removing Redis for Aggregator..."
	@docker stop redis-talktobook-aggregator 2>/dev/null || true
	@docker rm redis-talktobook-aggregator 2>/dev/null || true
	@echo "[Makefile] Redis container removed."

# Aggregator 정리
clean-aggregator-server: ## Clean aggregator server
	@echo "[Makefile] Cleaning Aggregator server..."
	@cd aggregator && rm -rf __pycache__ *.pyc .env
	@echo "[Makefile] Successfully Aggregator server cleaned."

### Infrastructure 관련 명령어 ###
# CDK 배포 스크립트 실행
bootstrap: ## Bootstrap CDK environment (run once)
	@echo "Bootstrapping CDK environment..."
	cd $(CDK_DIR) && AWS_PROFILE=$(AWS_PROFILE) npx cdk bootstrap --qualifier talkbook

bootstrap-force: ## Force bootstrap CDK environment
	@echo "Force bootstrapping CDK environment..."
	cd $(CDK_DIR) && AWS_PROFILE=$(AWS_PROFILE) npx cdk bootstrap --qualifier talkbook --force

# Deployment Commands
deploy-all: ## Deploy all stacks (Base + Server + AI + Stream)
	@echo "Deploying all stacks..."
	cd $(CDK_DIR) && AWS_PROFILE=$(AWS_PROFILE) npx cdk deploy --all --require-approval never --qualifier talkbook

deploy: deploy-all ## Alias for deploy-all

deploy-base: ## Deploy Base stack only
	@echo "🏗️ Deploying Base stack..."
	cd $(CDK_DIR) && AWS_PROFILE=$(AWS_PROFILE) npx cdk deploy TalkToBook-Base --require-approval never --qualifier talkbook

deploy-server: ## Deploy Server stack
	@echo "🖥️ Deploying Server stack..."
	cd $(CDK_DIR) && AWS_PROFILE=$(AWS_PROFILE) npx cdk deploy TalkToBook-Base TalkToBook-Server --require-approval never --qualifier talkbook

deploy-ai: ## Deploy AI stack
	@echo "🤖 Deploying AI stack..."
	cd $(CDK_DIR) && AWS_PROFILE=$(AWS_PROFILE) npx cdk deploy TalkToBook-Base TalkToBook-AI --require-approval never --qualifier talkbook

deploy-stream: ## Deploy Stream stack
	@echo "🌊 Deploying Stream stack..."
	cd $(CDK_DIR) && AWS_PROFILE=$(AWS_PROFILE) npx cdk deploy TalkToBook-Base TalkToBook-Stream --require-approval never --qualifier talkbook

diff: ## Show CDK diff
	@echo "📋 Showing CDK diff..."
	cd $(CDK_DIR) && AWS_PROFILE=$(AWS_PROFILE) npx cdk diff

synth: ## Synthesize CDK templates
	@echo "🔍 Synthesizing CDK templates..."
	cd $(CDK_DIR) && AWS_PROFILE=$(AWS_PROFILE) npx cdk synth

list: ## List all CDK stacks
	@echo "📝 Listing CDK stacks..."
	cd $(CDK_DIR) && AWS_PROFILE=$(AWS_PROFILE) npx cdk list

# Cleanup Commands
destroy-all: ## Destroy all stacks (DANGEROUS)
	@echo "⚠️ Destroying all stacks..."
	@read -p "Are you sure? This will delete all resources! (y/N): " confirm && [ "$$confirm" = "y" ]
	cd $(CDK_DIR) && AWS_PROFILE=$(AWS_PROFILE) npx cdk destroy --all

clean: ## Clean CDK build artifacts
	@echo "🧹 Cleaning CDK artifacts..."
	cd $(CDK_DIR) && rm -rf cdk.out node_modules/.cache

# Development Commands
install: ## Install CDK dependencies
	@echo "📦 Installing CDK dependencies..."
	cd $(CDK_DIR) && npm install

build: ## Build CDK project
	@echo "🔨 Building CDK project..."
	cd $(CDK_DIR) && npm run build

watch: ## Watch CDK project for changes
	@echo "👀 Watching CDK project..."
	cd $(CDK_DIR) && npm run watch
