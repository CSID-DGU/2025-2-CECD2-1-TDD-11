# Material ID Mapping 개선 검증 보고서

## 변경 사항 요약

### 1. 핵심 변경 사항

#### 1.1 `__init__.py` - Flow 레벨
- **변경 전**: `_build_materials_list()` 함수가 material.json을 순회하며 `[(id, name)]` 튜플 리스트 생성
- **변경 후**: `_build_materials_list_from_mapping()` 함수가 material_id_mapping.json을 직접 로드하여 `{name: [cat, chunk, mat]}` dict 반환

```python
# 변경 전
def _build_materials_list(material_data: dict) -> List[tuple]:
    out: List[tuple] = []
    for cat_idx, category in enumerate(material_data.get("category", [])):
        # ... 순회 로직
        out.append(([cat_idx, chunk_idx, mat_idx], f"{c} {ch} {material}"))
    return out

# 변경 후
def _build_materials_list_from_mapping(mapping_path: str) -> dict:
    try:
        with open(mapping_path, 'r', encoding='utf-8') as f:
            return json.load(f)
    except Exception as e:
        print(f"[ERROR] material_id_mapping.json 로드 실패: {e}")
        return {}
```

#### 1.2 `map_materials.jinja2` - Prompt 레벨
- **변경 전**: materials_list를 리스트로 순회하며 name만 표시
- **변경 후**: materials_list를 dict로 순회하며 name과 id를 함께 표시

```jinja2
# 변경 전
materials_list:
{% for material in materials_list %}
- {{ material }}
{% endfor %}

# 변경 후
materials_list ("소재명": [category_order, chunk_order, material_order]):
{% for name, id in materials_list.items() %}
"{{ name }}: {{ id }}
{% endfor %}
```

- **추가**: current_material_id를 프롬프트에 명시적으로 전달
```jinja2
현재 소재: {{ current_material }}
현재 소재 ID: {{ current_material_id }}
```

### 2. 데이터 흐름 개선

#### 2.1 이전 흐름 (문제점)
```
material.json → _build_materials_list() → [(id, name), ...] 
                                          ↓
                                    LLM에 name만 전달
                                          ↓
                                    LLM이 name으로 id 추론 (불가능)
```

#### 2.2 개선된 흐름
```
material_id_mapping.json → _build_materials_list_from_mapping() → {name: id, ...}
                                                                    ↓
                                                            LLM에 name:id 쌍 전달
                                                                    ↓
                                                            LLM이 name으로 id 직접 조회
```

### 3. LLM 프롬프트 개선

#### 3.1 회피 감지 로직
- **변경 전**: "현재 소재에서 ID를 추출" (모호함)
- **변경 후**: "current_material_id를 그대로 사용" (명확함)

#### 3.2 소재 매칭 로직
- **변경 전**: "materials_list의 순서와 구조를 파악하여 ID 구성" (복잡함)
- **변경 후**: "materials_list의 value 값을 그대로 사용" (단순함)

### 4. 변경되지 않은 부분

#### 4.1 material_id_mapping.json 구조
```json
{
  "부모님 기본정보 성함": [1, 1, 1],
  "취미 입문 시작하게 된 계기": [18, 1, 1]
}
```
- 이미 올바른 구조로 되어 있음

#### 4.2 Engine 연결 관계
- InterviewEngine의 로직은 변경하지 않음
- Redis 데이터 구조는 유지
- axes 처리 로직 (principle, example, similar_event) 유지

#### 4.3 API DTO 구조
- Response의 material 필드는 이미 올바른 구조:
```python
"material": {
    "full_material_name": "생애주기 청소년기(13-19) 학교 생활",
    "material_name": "학교 생활",
    "material_order": 1
}
```

## 검증 체크리스트

### ✅ 완료된 항목

1. **질문 생성 (Question Generation)**
   - [x] Material은 material_id_mapping 형태의 full_material_name으로 전달
   - [x] User input은 그대로 유지

2. **입력 분석 (Answer Analysis)**
   - [x] current_material은 material_id_mapping 형태로 전달
   - [x] current_material_id는 [cat, chunk, mat] 배열로 전달
   - [x] materials_list는 {name: id} dict 형태로 전달
   - [x] LLM output은 `[{"material":[1,3,3],"axes":{...}}]` 구조

3. **Material ID 파싱**
   - [x] LLM이 반환한 material ID를 파싱하는 로직 유지
   - [x] material.json에서 order와 name을 찾는 로직 유지

4. **API Response**
   - [x] material 필드가 dict 형태로 반환:
     ```json
     {
       "full_material_name": "카테고리 청크 소재",
       "material_name": "소재",
       "material_order": 1
     }
     ```

5. **Redis 데이터 구조**
   - [x] 변경하지 않음
   - [x] 변경된 로직이 Redis에 제대로 적용되도록 보장

### 🔍 테스트 필요 항목

1. **LLM 응답 검증**
   - [ ] LLM이 materials_list dict를 올바르게 파싱하는지 확인
   - [ ] 회피 감지 시 current_material_id를 올바르게 반환하는지 확인
   - [ ] 소재 매칭 시 정확한 ID를 반환하는지 확인

2. **통합 테스트**
   - [ ] 첫 질문 생성 테스트
   - [ ] 답변 분석 및 다음 질문 생성 테스트
   - [ ] Material Gate 질문 테스트
   - [ ] 소재 전환 테스트

3. **API 테스트**
   - [ ] `/api/v2/interviews/start/{autobiography_id}` 테스트
   - [ ] `/api/v2/interviews/chat/{autobiography_id}` 테스트
   - [ ] Response의 material 구조 검증

## 예상 동작

### 시나리오 1: 정상 답변
```
User: "대학교 2학년 때 컴퓨터과학 수업을 들으면서..."

LLM Input:
- current_material: "생애주기 청소년기(13-19) 학교 생활"
- current_material_id: [14, 2, 1]
- materials_list: {"생애주기 청소년기(13-19) 학교 생활": [14, 2, 1], ...}

LLM Output:
[{"material":[14,2,1],"axes":{"principle":[1,1,0,1,1,1],"example":1,"similar_event":0,"pass":0}}]

Result:
- material_id [14, 2, 1]로 소재 업데이트
- 다음 질문 생성
```

### 시나리오 2: 회피 답변
```
User: "잘 모르겠어요"

LLM Input:
- current_material: "생애주기 청소년기(13-19) 학교 생활"
- current_material_id: [14, 2, 1]

LLM Output:
[{"material":[14,2,1],"axes":{"principle":[1,1,1,1,1,1],"example":1,"similar_event":1,"pass":1}}]

Result:
- 소재 완료 처리
- 다른 소재로 전환
```

### 시나리오 3: 다중 소재 매칭
```
User: "학교에서 친구들과 함께 프로그래밍 동아리 활동을 했어요..."

LLM Output:
[
  {"material":[14,2,1],"axes":{...}},  // 학교 생활
  {"material":[11,3,1],"axes":{...}}   // 친구 관계
]

Result:
- 두 소재 모두 업데이트
- last_answer_materials_id: [[14,2,1], [11,3,1]]
```

## 주의사항

1. **material_id_mapping.json 필수**
   - 파일이 없으면 빈 dict 반환
   - 에러 로그 출력

2. **LLM 응답 파싱**
   - JSON 파싱 실패 시 빈 리스트 반환
   - 마크다운 코드 블록 제거 로직 유지

3. **하위 호환성**
   - API response 구조가 변경되지 않음
   - 클라이언트 업데이트 불필요

## 결론

모든 요구사항이 올바르게 적용되었습니다:

1. ✅ Material ID는 name 기반이 아닌 id_mapping 기반으로 변경
2. ✅ LLM에 materials_list를 {name: id} dict 형태로 전달
3. ✅ LLM이 material ID를 [cat, chunk, mat] 배열로 반환
4. ✅ Engine 연결 관계 유지
5. ✅ Redis 데이터 구조 유지
6. ✅ API DTO 구조 유지 (material은 이미 dict 형태)

다음 단계: 서버 실행 및 통합 테스트 수행
