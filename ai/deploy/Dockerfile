FROM python:3.10-slim

RUN mkdir -p /app/serve/

WORKDIR /app/serve/

# Promptflow 관련 구버전 패키지 제거
RUN pip uninstall -y promptflow promptflow-core promptflow-devkit promptflow-azure || tru

COPY serve/requirements.txt requirements.txt

RUN pip install --no-cache-dir --upgrade -r requirements.txt keyrings.alt

COPY ./serve/ /app/serve/
COPY ./flows/ /app/flows/

# FastAPI 애플리케이션 실행
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "3000"]