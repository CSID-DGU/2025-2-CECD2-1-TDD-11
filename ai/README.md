# Life Bookshelf AI

인생책장 서비스의 AI 서버 레포지토리

## 프로젝트 구조

- **flows** (AI 로직)
  - autobiographies
    - standard
      - **generate_autobiography** (유지)
  - interviews
    - chat
      - **interview_chat_v2** (Redis 세션 + 신규 알고리즘)
    - standard
      - **generate_interview_questions_v2** (질문 생성 유틸)

- **serve** (API 서버)
  - **session_manager.py** (Redis 세션 관리)
  - autobiographies
    - **generate_autobiography** (유지)
  - interviews
    - **interview_chat_v2** (Redis 기반 세션 관리)

### 폐기된 API (v2.0.0)
- **generate_correction** (교정/교열 기능 불필요)
- **generate_chapter** (덩어리 묶음방식 채택)
- **generate_interview_question** (메인 질문 미리 생성 방식 미채택)
- **interview_chat_v1** (현재 사용 알고리즘 아님)

### 아키텍처 특징 (v2.0.0)
- **새로운 API 명세**: 폐기된 API 제거 및 경로 정리
- **Redis 세션 관리**: 대화 상태를 Redis에 저장하여 세션 지속성 보장
- **Legacy 알고리즘 통합**: 기존 질문 생성 알고리즘을 엔진으로 통합
- **3단계 계층**: Category → Chunk → Material 구조
- **통합 엔진**: 7개 파일을 3개로 통합 (core.py, utils.py, generators.py)
- **자서전 생성 혁신**: 테마/카테고리 기반 생성 및 병렬 처리

## 로컬 개발 환경 설정 (flows)

### Python 설치

[Python](https://www.python.org/downloads/) (3.9 이상)을 설치합니다.

### Prompt Flow Extension 설치

[Prompt Flow Extension](https://marketplace.visualstudio.com/items?itemName=prompt-flow.prompt-flow)을 설치합니다.

### OpenAI API Key 발급

[OpenAI API Key 발급](https://platform.openai.com/)에서 API Key를 발급받습니다.

[Azure OpenAI API Key 발급](https://ai.azure.com/)에서 API Key를 발급받습니다.

> AZure OpenAI를 활용하는 경우, https://learn.microsoft.com/ko-kr/legal/cognitive-services/openai/limited-access 에서 Azure OpenAI 서비스에 대한 엑세스를 신청해야 할 수 있습니다.

### Basic Setup

```bash
git clone git@github.com:life-librarians/life-bookshelf-ai.git
```

```bash
cd life-bookshelf-ai
```

```bash
code flows/[개발할 플로우 디렉토리]
```

개발할 플로우의 디렉토리를 열기

```bash
python3 -m venv .venv
```

required python3 ≥ `Python 3.9.x` (3.9.x 권장)

```bash
source .venv/bin/activate
```

```bash
pip install -r requirements.txt
```

```bash
pip install --upgrade pip
```

![VSCode 인터프리터 설정](image/image1.png)

VSCode에서 인터프리터도 변경해주어야 합니다.

![Prompt Flow Connection 설정](image/image2.png)

Prompt Flow Extension에서 Connection을 설정해줍니다.

![Connection 이름 설정](image/image3.png)
connection 이름을 설정하고 Create Connection 클릭합니다. (다른건 수정 X)

![API 키 입력](image/image4.png)
터미널에서 api_key를 입력하면 connection 등록이 완료됩니다.

> name의 값을 **open_ai_connection** 으로 설정해야 합니다.

### Flow Test Run 실행

![Visul Editor 열기](image/image5.png)

**[실행을 원하는 플로우 경로]/flow.dag.yaml** 파일을 열어 Visual Editor를 실행해줍니다.

![Test Run 실행](image/image6.png)

Test Run을 실행해줍니다.

![실행 결과](image/image7.png)

터미널에서 결과를 확인할 수 있습니다.

![웹 실행 결과](image/image8.png)
또한 제공되는 링크를 열어 웹에서도 결과를 확인할 수 있습니다.

### Flow Batch Run 실행

![Batch Run 실행](image/image9.png)

Batch Run을 실행해줍니다.
Batch Run은 미리 준비한 데이터를 이용하여 여러번의 Test Run을 실행하는 것입니다.

![Local File 선택](image/image10.png)

Local File을 선택합니다.

![data.json 선택](image/image12.png)

**data.json** 파일을 선택합니다.

![Batch Run 실행](image/image13.png)

Batch Run을 실행합니다.

![웹 결과 확인](image/image14.png)

웹에서 결과를 확인할 수 있습니다.

## 로컬 개발 환경 설정 (serve)

### Python 설치

[Python](https://www.python.org/downloads/) (3.9 이상)을 설치합니다.

```bash
git clone git@github.com:life-librarians/life-bookshelf-ai.git
```

```bash
cd life-bookshelf-ai
```

```bash
code serve
```

개발할 serve 디렉토리를 열기

```bash
python3 -m venv .venv
```

required python3 ≥ `Python 3.9.x` (3.9.x 권장)

```bash
source .venv/bin/activate
```

```bash
pip install -r requirements.txt
```

```bash
cp .env.example .env.development
```

`.env.development` 파일을 생성합니다.

```.env.example
DEBUG=True
LOG_LEVEL=DEBUG

# Azure OpenAI의 API Key를 사용하는 경우
# AZURE_OPENAI_API_KEY=your-api-key
# AZURE_OPENAI_API_BASE=https://your-api-base.openai.azure.com
# AZURE_OPENAI_API_TYPE=azure

# OpenAI의 API Key를 사용하는 경우
AZURE_OPENAI_API_KEY=sk-your-api-key
LIFE_BOOKSHELF_AI_JWT_SECRET_KEY=0190ab45-7e42-7a3f-9dec-726ddf778076
```

자신의 API Key를 입력합니다.

```bash
python -m uvicorn main:app --env-file .env.development --port 3000
```

3000 포트에서 fastapi 서버를 실행합니다.

![서버 실행 예시](image/image15.png)

서버가 정상적으로 실행되었을 경우 위와 같은 로그가 출력됩니다. (실제 로그는 3000포트로 출력됩니다.)

http://localhost:3000/docs 에서 API 문서를 확인할 수 있습니다. (사용 방법 참고: [Life Bookshelf Server](https://github.com/life-librarians/life-bookshelf-server?tab=readme-ov-file#swagger-%EC%9D%B4%EC%9A%A9-%EB%B0%A9%EB%B2%95))

### Redis 설치 및 실행

```bash
# Windows (Chocolatey)
choco install redis-64
redis-server

# 또는 Docker
docker run -d -p 6379:6379 redis:alpine
```

### v2 API 테스트

Swagger UI에서 `/api/v2/interviews/interview-chat` 엔드포인트를 확인할 수 있습니다.

## Interview Chat V2 (Redis 세션 + Legacy 알고리즘)

### 개요

v2는 Redis 세션 관리와 Legacy 알고리즘을 통합하여 체계적 질문을 생성합니다.

### 통합된 구조

```
flows/interviews/chat/interview_chat_v2/
  - flow.dag.yaml               # 단일 노드 플로우
  - __init__.py                 # 통합 엔진 (Redis 연동)
  - engine/                     # Legacy 알고리즘 (통합)
    - core.py                   # 기존 알고리즘 + 모델
    - utils.py                  # 기존 유틸 + V2 추가
    - generators.py             # V2 질문 생성
  - data/
    - material.json, theme.json

serve/
  - session_manager.py          # Redis 세션 관리
  - interviews/interview_chat_v2/
    - router/__init__.py        # API 엔드포인트
    - dto/__init__.py           # 요청/응답 스키마
```

### API 엔드포인트 (v2.0.0)

#### 인터뷰 API
- `POST /interviews/start` - 세션 시작
- `POST /interviews/chat` - 인터뷰 대화
- `POST /interviews/end` - 세션 종료

#### 자서전 API  
- `POST /autobiographies/generate/{autobiography_id}` - 자서전 생성

#### 폐기된 API
- ~~`POST /chapters/generate_chapters`~~ (챕터 생성)
- ~~`POST /autobiographies/proofreading`~~ (교정/교열)
- ~~`POST /interviews/interview-questions`~~ (인터뷰 질문 생성)
- ~~`POST /interviews/interview-chat`~~ (v1 대화)

### v2.0.0 주요 기능

#### 새로운 API 구조
1. **API 경로 정리**: 불필요한 prefix 제거 및 직관적 경로 체계
2. **자서전 생성 혁신**: 테마/카테고리 기반 생성 및 제목 자동 생성
3. **병렬 처리**: async/await 구조로 성능 향상
4. **DTO 표준화**: snake_case 적용 및 의미 있는 필드명

#### 기존 v1.2.0 기능 유지
1. **Redis 세션 관리**: 대화 상태를 Redis에 저장하여 세션 지속성 보장
2. **Legacy 알고리즘 통합**: 기존 질문 생성 알고리즘을 엔진으로 완전 통합
3. **통합 엔진**: 7개 파일을 3개로 통합하여 코드 구조 개선
4. **LLM 기반 소재 매칭**: 답변 내용을 분석하여 관련 소재 자동 매칭
5. **6W 축 분석**: LLM이 답변에서 6W(누가/언제/어디서/무엇을/왜/어떻게) 축을 자동 분석
6. **메트릭 최적화**: 활성 데이터만 저장하여 JSON 크기 대폭 감소
7. **직접 파싱**: 소재명을 띄어쓰기 기준으로 파싱하여 정확한 매칭 보장
8. **컨텍스트 기반 질문**: 동일 소재 시 답변 내용 반영

자세한 사용법은 `serve/interviews/interview_chat_v2/README.md` 참고

## 상세 문서 링크

### Flows (v2.0.0)
- [Interview Chat V2 (메트릭 기반)](flows/interviews/chat/interview_chat_v2/README.md)
- [Generate Interview Questions V2](flows/interviews/standard/generate_interview_questions_v2/README.md)
- [Generate Autobiography](flows/autobiographies/standard/generate_autobiography/)

### Serve (v2.0.0)
- [Interview Chat V2 API](serve/interviews/interview_chat_v2/README.md)
- [API 테스트 가이드](serve/TEST_GUIDE.md)
- [폐기된 API 목록](serve/DEPRECATED_APIS.md)

### 마이그레이션 가이드
- [v2.0.0 변경사항](CHANGELOG.md#v200---2025-01-17)
- [Breaking Changes 상세](CHANGELOG.md#-breaking-changes)
## AI
