FROM python:3.10-slim

RUN mkdir -p /app/serve/
WORKDIR /app/serve/

COPY serve/requirements.txt requirements.txt

# 모든 의존성 설치
RUN pip install --no-cache-dir --upgrade -r requirements.txt keyrings.alt

COPY ./serve/ /app/serve/
COPY ./flows/ /app/flows/

# FastAPI 실행
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "3000"]
