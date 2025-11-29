#!/bin/bash

# ==============================
# Conda 가상환경 생성 및 활성화 스크립트
# ==============================

# 1. 환경 이름과 파이썬 버전 설정
ENV_NAME="talktobook-ai"
PYTHON_VERSION="3.10"
REQUIREMENTS_FILE="requirements.txt"

# 경로 의존성 제거
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$(dirname "$SCRIPT_DIR")")"
REQUIREMENTS_FILE="$PROJECT_ROOT/ai/requirements.txt"

# 2. conda 초기화
if ! command -v conda &> /dev/null
then
    echo "[scripts/ai/set-virtual-environment] conda 명령어를 찾을 수 없습니다. Miniconda 또는 Anaconda가 설치되어 있는지 확인하세요."
    exit 1
fi

# 3. conda 환경 목록 확인 후 동일 이름의 환경이 없을 경우 생성
if conda info --envs | grep -q "$ENV_NAME"
then
    echo "[scripts/ai/set-virtual-environment] 이미 존재하는 conda 환경입니다: $ENV_NAME"
else
    echo "[scripts/ai/set-virtual-environment] 새 conda 환경을 생성합니다: $ENV_NAME"
    conda create -y -n "$ENV_NAME" python="$PYTHON_VERSION"
fi

# 4. 현재 쉘에서 conda를 사용할 수 있도록 초기화
eval "$(conda shell.bash hook)"

# 5. 환경 활성화
echo "[scripts/ai/set-virtual-environment] 가상환경을 활성화합니다: $ENV_NAME"
conda activate "$ENV_NAME"


# 6. requirements.txt 설치
if [ -f "$REQUIREMENTS_FILE" ]; then
    echo "[scripts/ai/set-virtual-environment] requirements.txt 파일을 감지했습니다. 패키지를 설치합니다..."
    pip install --upgrade pip
    pip install -r "$REQUIREMENTS_FILE"
else
    echo "[scripts/ai/set-virtual-environment] requirements.txt 파일이 존재하지 않습니다. 패키지 설치를 건너뜁니다."
fi

# 7. 완료 메시지
echo "[scripts/ai/set-virtual-environment] 현재 활성 환경: $(conda info --envs | grep '*' | awk '{print $1}')"
echo "[scripts/ai/set-virtual-environment] 가상 환경 준비가 완료되었습니다!"