FROM python:3.10-slim

RUN mkdir -p /app/serve/

WORKDIR /app/serve/

# 반드시 true 로 써야 함
RUN pip uninstall -y promptflow promptflow-core promptflow-devkit promptflow-azure || true

COPY serve/requirements.txt requirements.txt

# dependency resolution 깨지지 않게 --no-deps 옵션 추가하는 것이 핵심
RUN pip install --no-cache-dir --upgrade --no-deps marshmallow==3.26.1
RUN pip install --no-cache-dir --upgrade -r requirements.txt keyrings.alt

COPY ./serve/ /app/serve/
COPY ./flows/ /app/flows/

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "3000"]
