# Life Bookshelf AI

인생책장 서비스의 AI 서버 레포지토리

## 프로젝트 구조

- **flows**

  - autobiograpies
    - chat
    - evaluation
    - standard
      - **generate_autobiography**
      - **generate_correction**
  - chapters
    - chat
    - evaluation
    - standard
      - **generate_chapter**
  - interviews
    - chat
      - **interview_chat_v1** (대화 히스토리 기반)
      - **interview_chat_v2** (메트릭 기반)
    - evaluation
    - standard
      - **generate_interview_question**

- **serve**
  - autobiographies
    - **generate_autobiography**
    - **generate_correction**
  - chapters
    - **generate_chapter**
  - interviews
    - **generate_interview_question**
    - **interview_chat** (v1 라우터)
    - **interview_chat_v2** (메트릭 기반)

autobiobraphies, chapters, interviews 총 3개의 도메인이 존재합니다.

- **flows** 에는 각 도메인 별로 chat, evaluation, standard 3가지 종류의 flow가 존재합니다.
- **serve** 에는 각 도메인 별로 flows 경로에 구성했던 flow를 서빙하는 서버 코드가 존재합니다.

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

### v2 API 테스트

Swagger UI에서 `/api/v2/interviews/interview-chat` 엔드포인트를 확인할 수 있습니다.



## Interview Chat V2 (메트릭 기반 질문 생성)

### 개요

v2는 소재별 6W(who/why/when/how/where/what) 축 완성도를 추적하여 체계적으로 질문을 생성합니다.

### 폴더 구조

```
flows/interviews/chat/interview_chat_v2/
  - flow.dag.yaml
  - nodes/
    - 01_parse_answer.py          # 응답 파서 (키워드, 축, 구체성 추출)
    - 02_update_metrics.py        # 메트릭 갱신 (6W 축, 예시, 구체성)
    - 03_generate_candidates.py   # 미완성 축에 대한 질문 생성
    - 04_select_questions.py      # 질문 선택 알고리즘

serve/interviews/interview_chat_v2/
  - router/
    - __init__.py                 # POST /api/v2/interviews/interview-chat
  - dto/
    - __init__.py                 # 요청/응답 스키마 (Pydantic)
  - README.md                     # API 사용 가이드
```

### API 엔드포인트

- **v1**: `POST /api/v1/interviews/interview-chat` (대화 히스토리 기반)
- **v2**: `POST /api/v2/interviews/interview-chat` (메트릭 기반)

### v2 주요 기능

1. **응답 파싱**: 룰 기반 + LLM 기반 키워드 추출
2. **메트릭 추적**: 소재별 6W 축 완성도, 예시/구체성 플래그
3. **질문 생성**: 미완성 축에 대한 LLM 질문 생성 (템플릿 폴백)
4. **질문 선택**: 청크 가중치 기반 우선순위 알고리즘

자세한 사용법은 `serve/interviews/interview_chat_v2/README.md` 참고

## 상세 문서 링크

### Flows
- [Interview Chat V1 (대화 히스토리 기반)](flows/interviews/chat/interview_chat_v1/README.md)
- [Interview Chat V2 (메트릭 기반)](flows/interviews/chat/interview_chat_v2/README.md)
- [Generate Interview Questions V2](flows/interviews/standard/generate_interview_questions_v2/README.md)

### Serve
- [Interviews API 가이드](serve/interviews/readme.md)
- [Interview Chat V2 API](serve/interviews/interview_chat_v2/README.md)
- [API 테스트 가이드](serve/TEST_GUIDE.md)
