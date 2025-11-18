FROM python:3.10-slim

RUN mkdir -p /app/serve/
WORKDIR /app/serve/

# Promptflow 구버전 완전 제거
RUN pip uninstall -y promptflow promptflow-core promptflow-devkit promptflow-azure || true

COPY serve/requirements.txt requirements.txt

# marshmallow 강제 설치 (Promptflow가 나중에 올려도 override 됨)
RUN pip install --no-cache-dir --upgrade --no-deps marshmallow==3.26.1

# dependencies 설치 (Promptflow 포함)
RUN pip install --no-cache-dir --upgrade -r requirements.txt keyrings.alt

COPY ./serve/ /app/serve/
COPY ./flows/ /app/flows/

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "3000"]
