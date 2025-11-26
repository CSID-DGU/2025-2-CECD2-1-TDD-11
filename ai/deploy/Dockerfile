FROM python:3.10-slim

RUN mkdir -p /app/serve/
WORKDIR /app/serve/

# Promptflow 구버전 흔적 제거
RUN pip uninstall -y promptflow promptflow-core promptflow-devkit promptflow-azure || true

COPY requirements.txt requirements.txt

# Promptflow 1.7.x는 marshmallow 3.19.0 필요
RUN pip install --no-cache-dir --upgrade --no-deps marshmallow==3.19.0

# keyring 대안 설치 (Docker 환경용)
RUN pip install --no-cache-dir keyrings.alt

# 모든 의존성 설치
RUN pip install --no-cache-dir --upgrade -r requirements.txt

COPY ./serve/ /app/serve/
COPY ./flows/ /app/flows/
COPY ../deploy/ai/promtail-config.yaml /etc/promtail-config.yaml

# Python 모듈 경로 설정
ENV PYTHONPATH=/app/serve

# FastAPI 실행
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "3000"]
