# Interview Chat V2 API 테스트 가이드

Redis 세션 기반 Legacy 알고리즘 통합 시스템 테스트
혹시 swagger 예시와 다른 게 있으면 swagger가 맞음

## 사전 준비

### 1. Redis 서버 실행
```bash
# Windows (Chocolatey)
choco install redis-64
redis-server

# 또는 Docker
docker run -d -p 6379:6379 redis:alpine
```

### 2. AI 서버 실행
```bash
cd serve
python -m uvicorn main:app --env-file .env.development --port 3000
```

### 3. JWT 토큰 생성
```bash
python generate_test_token.py
```

출력된 토큰을 복사하세요.

### 4. Swagger UI 접속
http://localhost:3000/docs

---

## 테스트 시나리오

### Step 1: 인증 설정

1. Swagger UI 우측 상단 **Authorize** 버튼 클릭
2. Value 입력란에 토큰 입력 (Bearer 제외하고 토큰만)
3. **Authorize** 클릭
4. **Close** 클릭

---

### Step 2A: 세션 시작 (완전 처음)

**엔드포인트**: `POST /api/v2/interviews/start/{autobiography_id}`

**Request Body**:
```json
{
  "preferred_categories": []
}
```

**예상 응답**:
```json
{
  "first_question": {
    "id": "q-2afa5754",
    "material": "일반_소개",
    "type": "category_intro",
    "text": "어떤 이야기를 하고 싶으신가요? 기억에 남는 에피소드나 경험이 있다면 들려주세요.",
    "material_id": [0, 0, 0]
  }
}
```

**확인 사항**:
- ✅ 200 OK 응답
- ✅ 카테고리 소개 질문 생성됨
- ✅ type이 "category_intro"임
- ✅ material_id가 [카테고리번호, 0, 0] 형태

---

### Step 2B: 세션 시작 (선호 카테고리 지정)

**엔드포인트**: `POST /api/v2/interviews/start/{autobiography_id}`

**Request Body**:
```json
{
  "preferred_categories": [3, 5]
}
```

**예상 응답**:
```json
{
  "first_question": {
    "id": "q-d4234930",
    "material": "형제, 친척_소개",
    "type": "category_intro",
    "text": "형제, 친척에 대해서 어떤 이야기를 하고 싶으신가요? 기억에 남는 에피소드나 경험이 있다면 들려주세요.",
    "material_id": [3, 0, 0]
  }
}
```

**확인 사항**:
- ✅ 200 OK 응답
- ✅ 선호 카테고리(3번)에서 질문 생성됨
- ✅ 테마 부스팅 적용됨

---

### Step 2C: 세션 재개 (이전 메트릭 포함)

**엔드포인트**: `POST /api/v2/interviews/start/{autobiography_id}`

**Request Body**:
```json
{
  "preferred_categories": [],
  "previous_metrics": {
    "session_id": "session-12345",
    "categories": [
      {
        "category_num": 1,
        "category_name": "부모",
        "chunks": [
          {
            "chunk_num": 1,
            "chunk_name": "프로필",
            "chunk_weight": 2,
            "materials": [
              {
                "order": 1,
                "name": "성격",
                "principle": [2, 1, 0, 1, 0, 0],
                "example": 1,
                "similar_event": 0,
                "count": 1
              }
            ]
          }
        ]
      }
    ],
    "engine_state": {
      "last_material_id": [1, 1, 1],
      "last_material_streak": 2,
      "epsilon": 0.1
    },
    "asked_total": 5,
    "policy_version": "v2.0.0"
  }
}
```

**예상 응답**:
```json
{
  "first_question": {
    "id": "q-def456",
    "material": "근무 환경(조직, 분위기)",
    "type": "w1",
    "text": "근무 환경(조직, 분위기)에 대해 '언제' 측면에서 더 구체적으로 들려주세요.",
    "material_id": [1, 1, 2]
  }
}
```

**확인 사항**:
- ✅ 200 OK 응답
- ✅ 이전 상태가 복원됨
- ✅ 다음 적절한 소재로 질문 생성됨
- ✅ chunk_weight 가산점 반영됨

---

### Step 3: 대화 진행 (동일 소재 답변)

**엔드포인트**: `POST /api/v2/interviews/chat/{autobiography_id}`

**Request Body**:
```json
{
  "answer_text": "부모님을 떠올리면, 일요일 아침마다 식탁에 올라오던 따뜻한 국물과 조용히 흘러나오던 라디오 소리가 함께 생각납니다. 아버지는 언제나 국그릇을 제 앞으로 밀어주며 한 숟가락 크게 떠 보라고 했고, 어머니는 그 틈에 제 학교 이야기를 천천히 끌어냈습니다. 그날그날의 사소한 실패도 식탁 위에서 웃음으로 덜어냈고, 그렇게 하루가 단단하게 시작됐습니다. 가장 기억에 남는 순간은 첫 발표에 떨던 저를 위해 부모님이 밤늦게까지 연습 상대가 되어준 날입니다. 대본을 외우다 지쳐 고개를 떨구자, 아버지는 한 문장씩 끊어 읽는 법을 알려주고, 어머니는 말의 속도를 잡아주며 끝까지 함께해 주었습니다.\n\n조부모님과의 기억은 마당의 흙냄새와 얽혀 있습니다. 여름이면 할아버지는 큼직한 모자를 씌워 주고 토마토 줄기를 묶는 법을 가르쳐 주셨는데, 힘을 너무 주면 줄기가 상한다며 손끝의 힘을 조절하는 법을 몸으로 알려 주셨습니다. 해가 기울면 할머니가 삭힌 장아찌를 내오고, 우리는 대청마루에서 느릿하게 저녁을 먹었습니다. 한 번은 비바람이 심해 밭이 반쯤 쓰러졌을 때, 할아버지는 다음 날 새벽에 저를 깨워 무너진 지주대를 하나하나 바로 세우며 "살아가는 일은 넘어진 것들을 다시 일으키는 일"이라고 말했습니다. 그 말은 시간이 지나도 제 마음속에서 계속 자라, 어려움 앞에서 숨을 고르게 하는 문장이 되었습니다."
}
```

**예상 응답**:
```json
{
  "next_question": {
    "id": "q-def456",
    "material": "설명",
    "type": "ex",
    "text": "설명과 관련된 구체적인 '예시 한 가지'를 자세히 이야기해 주세요.",
    "material_id": [8, 1, 1]
  },
  "last_answer_materials_id": [[1, 4, 4], [2, 2, 3]]
}
```

**확인 사항**:
- ✅ 200 OK 응답
- ✅ 동일 소재 ("성격") 감지됨
- ✅ 답변 내용을 반영한 질문 생성
- ✅ material_id 유지됨
- ✅ last_answer_materials_id에 매칭된 소재 ID 목록 포함

---

### Step 4: 다른 소재 대화 테스트

**엔드포인트**: `POST /api/v2/interviews/chat/{autobiography_id}`

**Request Body**:
```json
{
  "answer_text": "아버지부터 말씀드릴게요. 아버지는 정말 온화하신 분이셨어요. 어릴 때부터 저에게 항상 따뜻한 말을 건네주셨고, 제가 실수를 해도 화를 내지 않고 차근차근 설명해주셨습니다."
}
```

**예상 응답**:
```json
{
  "next_question": {
    "id": "q-abc123",
    "material": "name",
    "type": "when",
    "text": "name에 대해 '언제' 측면에서 더 구체적으로 들려주세요.",
    "material_id": [1, 1, 1]
  },
  "last_answer_materials_id": [[1, 1, 1]]
}
```

**확인 사항**:
- ✅ 200 OK 응답
- ✅ 다른 소재 감지됨
- ✅ 새로운 material_id 선택됨
- ✅ last_answer_materials_id 포함

---

### Step 5: 세션 종료

**엔드포인트**: `POST /api/v2/interviews/end/{autobiography_id}`

**Request Body**:
```json
{}
```

**예상 응답** (최적화된 JSON):
```json
{
  "session_id": "session-12345",
  "final_metrics": {
    "session_id": "session-12345",
    "categories": [
      {
        "category_num": 1,
        "category_name": "부모",
        "chunks": [
          {
            "chunk_num": 1,
            "chunk_name": "프로파일",
            "chunk_weight": 15,
            "materials": [
              {
                "order": 1,
                "name": "성격",
                "principle": [1, 1, 1, 0, 1, 1],
                "example": 1,
                "similar_event": 1,
                "count": 1
              }
            ]
          }
        ]
      }
    ],
    "engine_state": {
      "last_material_id": [1, 1, 1],
      "last_material_streak": 3,
      "epsilon": 0.1
    },
    "asked_total": 10,
    "policy_version": "v2.0.0"
  },
  "pool_to_save": []
}
```

**확인 사항**:
- ✅ 200 OK 응답
- ✅ Redis에서 최종 메트릭 조회 성공
- ✅ 세션 삭제 성공
- ✅ final_metrics에 활성 데이터만 포함 (JSON 최적화)
- ✅ LLM 분석 결과가 메트릭에 정확히 반영됨

---

## Stream API 테스트 (Queue/Cycle 관리)

### Step 7: Cycle 초기화

**엔드포인트**: `POST /api/v2/cycle/init`

**Request Body**:
```json
{
  "cycleId": "cycle-001",
  "expectedCount": 3,
  "autobiographyId": 1,
  "userId": 1
}
```

**예상 응답**:
```json
{
  "status": "success",
  "cycleId": "cycle-001"
}
```

**확인 사항**:
- ✅ 200 OK 응답
- ✅ Redis에 cycle 정보 저장됨

---

### Step 8: 인터뷰 요약 생성 (함수 호출)

**엔드포인트**: `POST /api/v2/summary/generate`

**Request Body**:
```json
{
  "interviewId": 1,
  "userId": 1,
  "conversations": [
    {
      "question": "어린 시절 가장 기억에 남는 일은 무엇인가요?",
      "conversation": "초등학교 때 친구들과 함께 놀던 기억이 가장 생생합니다."
    }
  ]
}
```

**예상 응답**:
```json
{
  "interviewId": 1,
  "userId": 1,
  "summary": "사용자는 어린 시절 초등학교 때 친구들과 함께 놀던 기억을 가장 소중하게 여기고 있습니다..."
}
```

---

### Step 9: 자서전 생성 (Cycle 기반)

**엔드포인트**: `POST /api/v2/autobiography/generate`

**Request Body**:
```json
{
  "cycleId": "cycle-001",
  "step": 1,
  "autobiographyId": 1,
  "userId": 1,
  "userInfo": {
    "gender": "남성",
    "occupation": "학생",
    "ageGroup": "20대"
  },
  "autobiographyInfo": {
    "theme": "성장",
    "reason": "나의 성장 과정을 기록하고 싶어서",
    "category": "학창시절"
  },
  "answers": [
    {
      "content": "초등학교 때 친구들과 함께 놀던 기억이 가장 생생합니다.",
      "conversationType": "HUMAN"
    }
  ]
}
```

**예상 응답** (Step 1/3):
```json
{
  "cycleId": "cycle-001",
  "step": 1,
  "autobiographyId": 1,
  "userId": 1,
  "title": "성장 - 학창시절에 대한 나의 이야기",
  "content": "초등학교 시절의 소중한 추억...",
  "isLast": false
}
```

**예상 응답** (Step 3/3 - 마지막):
```json
{
  "cycleId": "cycle-001",
  "step": 3,
  "autobiographyId": 1,
  "userId": 1,
  "title": "성장 - 대학시절에 대한 나의 이야기",
  "content": "대학 생활의 의미 있는 순간들...",
  "isLast": true
}
```

**확인 사항**:
- ✅ 200 OK 응답
- ✅ Step 1~2: isLast = false
- ✅ Step 3 (마지막): isLast = true
- ✅ Redis에서 cycle 자동 삭제됨

---

## 수정된 사항 (v2.0.0)

✅ **API 경로 간소화**: `/api/v2/interviews/*` → `/interviews/*` 불필요한 prefix 제거

✅ **DTO 표준화**: `sessionId` → `session_id` (snake_case 적용)

✅ **버전 업데이트**: `policyVersion` v1.2.0 → v2.0.0

### 기존 v1.2.0 기능 유지

✅ **LLM 기반 소재 매칭**: 답변 내용을 분석하여 관련 소재를 자동으로 매칭

✅ **6W 축 자동 분석**: LLM이 답변에서 6W(누가/언제/어디서/무엇을/왜/어떻게) + ex/con 축을 자동 분석하여 메트릭에 정확히 반영

✅ **배열 구조로 변경**: 객체 구조에서 배열 구조로 변경하여 값 누적 문제 해결
- categories: {} → []
- chunks: {} → []
- materials: {} → []

✅ **JSON 최적화**: 활성 데이터만 직렬화하여 JSON 크기 대폭 감소
- 활성 카테고리만 포함 (chunk_weight > 0)
- 활성 청크만 포함 (chunk_weight > 0)
- 활성 소재만 포함 (w/ex/con/material_count 중 하나라도 값 존재)
- 활성 chunk_weight만 포함 (weight > 0)

✅ **직접 파싱**: 소재명을 띄어쓰기 기준으로 파싱하여 정확한 매칭 보장

✅ **압축된 JSON 응답**: LLM이 불필요한 띄어쓰기와 줄바꿈 없이 압축된 JSON을 반환하도록 개선

---

## 터미널 로그 설명

### 성공적인 대화 흐름
```
[DEBUG] LLM 플로우 결과: {...}
[DEBUG] 직접 JSON 파싱 성공: [...]
[INFO] LLM 분석 완료: 3개 소재 매칭
  소재: 부모 나와의_관계 기억나는_대화
  축: {'w': [1, 1, 0, 1, 1, 1], 'ex': 1, 'con': 1}
    6W: [1, 1, 0, 1, 1, 1] (valid)
🔍 [소재 매칭] 부모, 조부모_소개 → [...] (동일:False)
🔍 [소재 ID 매핑] current_material: '부모, 조부모_소개'
  '부모 나와의_관계 기억나는_대화' → [1, 4, 4]
📊 [메트릭 업데이트] 3개 소재
  1. 부모 나와의_관계 기억나는_대화 → [1, 4, 4]
    6W 반영: [1, 1, 0, 1, 1, 1] → [1, 1, 0, 1, 1, 1]
    변경: w [0, 0, 0, 0, 0, 0] → [1, 1, 0, 1, 1, 1], ex 0 → 1, con 0 → 1
    chunk_weight: 0 → 1
    material_count: 1
🎯 [질문 생성] 부모-프로필-학력_직업 (w2)
```

### 주요 로그 설명
- **LLM 분석 완료**: 답변에서 추출된 소재 개수
- **6W 축 분석**: 각 소재별 6W 축 값 (1=감지됨, 0=미감지)
- **소재 ID 매핑**: 소재명 → [카테고리, 청크, 소재] ID 변환
- **메트릭 업데이트**: 각 소재의 w/ex/con 값 변화 추적
- **chunk_weight**: 청크별 사용 빈도 증가
- **material_count**: 소재별 완성도 업데이트

## 트러블슈팅

### 401 Unauthorized
- JWT 토큰이 올바르게 설정되었는지 확인
- `generate_test_token.py`로 새 토큰 생성

### 500 Internal Server Error
- 서버 콘솔 로그 확인
- OpenAI API 키가 올바른지 확인
- material.json 파일 존재 확인

### LLM 분석 실패
- `[ERROR] LLM 매칭 실패` 로그 확인
- map_answer_to_materials 플로우 상태 확인
- OpenAI API 호출 제한 확인

### 소재 매칭 실패
- `소재 미발견/청크 미발견/카테고리 미발견` 로그 확인
- material.json에 해당 소재가 정확히 존재하는지 확인
- 소재명의 띄어쓰기/밑줄 형식 일치 확인

---

### Step 6: 자서전 생성

**엔드포인트**: `POST /generate/{autobiography_id}`

**Request Body**:
```json
{
  "user_info": {
    "gender": "FEMALE",
    "occupation": "프로그래머",
    "age_group": "대학교 재학"
  },
  "autobiography_info": {
    "theme": "가족",
    "category": "부모",
    "reason": "부모님과의 소중한 추억을 기록하고 싶습니다."
  },
  "interviews": [
    {
      "content": "부모님에 대해 어떤 기억이 가장 인상 깊나요?",
      "conversation_type": "AI"
    },
    {
      "content": "부모님을 떠올리면, 일요일 아침마다 식탁에 올라오던 따뜻한 국물과 조용히 흘러나오던 라디오 소리가 함께 생각됩니다. 아버지는 언제나 국그릇을 제 앞으로 밀어주며 한 숙가락 크게 떠 보라고 했고, 어머니는 그 틈에 제 학교 이야기를 천천히 끌어냈습니다.",
      "conversation_type": "HUMAN"
    }
  ]
}
```

**예상 응답**:
```json
{
  "title": "가족 - 부모에 대한 나의 이야기",
  "autobiographical_text": "일요일 아침의 따뜻한 기억\n\n부모님을 떠올리면, 일요일 아침마다 식탁에 올라오던 따뜻한 국물과 조용히 흘러나오던 라디오 소리가 함께 생각됩니다. 그 소중한 순간들이 지금의 나를 만든 초석이 되었습니다...\n\n(전체 자서전 내용)"
}
```

**확인 사항**:
- ✅ 200 OK 응답
- ✅ 인터뷰 내용을 바탕으로 자서전 생성
- ✅ 제목과 본문이 적절히 구성됨
- ✅ 사용자 정보와 주제가 반영됨

---

## 성공 기준

✅ Step 1: JWT 인증 성공  
✅ Step 2: 엔진 초기화 및 첫 질문 생성 성공  
✅ Step 3: 동일 소재 감지 및 컨텍스트 기반 질문 생성 성공  
✅ Step 4: 다른 소재 감지 및 material+type 기반 질문 생성 성공  
✅ Step 5: 세션 종료 및 최종 메트릭 수집 성공  
✅ Step 6: 인터뷰 내용 기반 자서전 생성 성공  

**최종 확인**: 모든 단계가 200 OK를 반환하고 Redis 세션 관리 + Legacy 알고리즘 + 명시적 세션 종료가 정상 작동하면 API가 완전히 기능하는 것입니다!
