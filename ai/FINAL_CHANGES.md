# Material ID Mapping 구조 개선 완료

## 변경 사항

### 1. material_id_mapping.json 구조 변경
**이전**: `"카테고리 청크 {'order': X, 'name': '소재명'}": [cat, chunk, mat]`
**현재**: `"카테고리 청크 소재명": [cat, chunk, mat]`

**예시**:
```json
{
  "부모님 기본정보 성함": [1, 1, 1],
  "취미 입문 시작하게 된 계기": [18, 1, 1],
  "돈과 경제 가치관 돈에 대한 첫 기억": [19, 1, 1]
}
```

### 2. 생성 스크립트 작성
**파일**: `ai/flows/interviews/chat/interview_chat_v2/generate_material_mapping.py`

material.json에서 각 레이어의 name만 추출하여 간단한 구조로 매핑 생성:
```python
key = f"{cat_name} {chunk_name} {mat_name}"
mapping[key] = [cat_order, chunk_order, mat_order]
```

**실행 결과**: 594개 소재 매핑 생성 완료

### 3. API Response 구조
```json
{
  "material": {
    "full_material_name": "부모님 기본정보 성함",
    "material_name": "성함",
    "material_order": 1
  },
  "material_id": [1, 1, 1]
}
```

- `full_material_name`: "카테고리 청크 소재" 형태의 전체 경로
- `material_name`: 소재명만 (마지막 부분)
- `material_order`: material.json의 order 값

### 4. 코드 변경사항

#### 4.1 `__init__.py`
- material_name 추출 로직 단순화:
  ```python
  parts = full_material_name.split()
  material_name = ' '.join(parts[2:]) if len(parts) > 2 else material.name
  ```

#### 4.2 `map_materials.jinja2`
- 프롬프트 업데이트: 새로운 구조 설명 추가
- materials_list 형식: "카테고리명 청크명 소재명"

#### 4.3 `dto/__init__.py`
- 예제 업데이트: 새로운 구조 반영

## 테스트 방법

### 1. material_id_mapping.json 재생성
```bash
cd ai/flows/interviews/chat/interview_chat_v2
python generate_material_mapping.py
```

### 2. 서버 실행
```bash
cd ai/serve
python -m uvicorn main:app --env-file .env.development --port 3000
```

### 3. 통합 테스트
```bash
cd ai/serve
python test_advanced_scenarios.py
```

## 장점

### 1. 간결성
- 불필요한 dict 문자열 제거
- 읽기 쉬운 구조

### 2. 명확성
- full_material_name이 실제 경로를 명확히 표현
- material_name이 소재명만 깔끔하게 표시

### 3. 유지보수성
- 생성 스크립트로 자동화
- material.json 변경 시 쉽게 재생성 가능

## 주의사항

1. **material.json 변경 시**: generate_material_mapping.py 재실행 필요
2. **LLM 프롬프트**: materials_list 형식이 변경되었으므로 LLM이 올바른 형식으로 반환하는지 확인
3. **클라이언트 업데이트**: material 필드 구조가 변경되었으므로 클라이언트 코드 업데이트 필요

## 완료 체크리스트

- ✅ material_id_mapping.json 구조 변경
- ✅ 생성 스크립트 작성 및 실행
- ✅ __init__.py 로직 수정
- ✅ map_materials.jinja2 프롬프트 수정
- ✅ DTO 예제 업데이트
- ⏳ 통합 테스트 (서버 실행 후 확인 필요)
