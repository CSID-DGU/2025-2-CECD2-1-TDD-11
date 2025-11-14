# 자서전 인터뷰 엔진 - 질문 알고리즘

> category → chunk → material 3단 구조 위에서, ε-greedy 탐색·가중치 보상·커버리지/분량 종료 조건을 이용해 인터뷰 질문을 자동 생성·진행하는 파이썬 엔진

---
<img width="1713" height="595" alt="image" src="https://github.com/user-attachments/assets/9addb266-b613-4601-84cc-88f8a9ac505c" />

## 주요 특징 (Features)

* 데이터 트리: `Category` → `Chunk` → `Material` (각 노드는 번호 부여)
* 카운트 행렬: 소재별 `w1~w6`(육하원칙), `ex`(예시), `con`(유사사례), `material_count`
* 우선순위 가중치: `chunk_weight` — 테마 부스트(초기 10) + 동일 덩어리 보상(+1)
* 맥락 유지: `last_material_id` / `last_material_streak`로 같은 소재 최대 3연속 질문
* ε-greedy 탐색: 10% 랜덤 탐색, 90% 규칙 기반(가중치·진행도)
* 종료 조건 (충분히 채워진 소재 ≥ 30) AND (카테고리 커버리지 ≥ 70% OR 원고 길이×0.75 ≥ 50,000자)
* 확장 포인트: `map_answer_to_materials()` / `generate_question()`에 실제 NLP 모델 연동

---

## 저장 포맷 (JSON Schemas)

### `material.json` 
* `Category` → `Chunk` → `Material`  데이터를 알 수 있습니다.

```json
{
  "category": [
    {
      "name": "부모",
      "chunk": [
        { "name": "프로필", "material": ["이름", "출생지(고향)", "학력/직업"] },
        { "name": "성격",   "material": ["특징", "대표 에피소드"] }
      ]
    },
    {
      "name": "취미",
      "chunk": [
        { "name": "시작", "material": ["입문 계기", "시작 연도"] }
      ]
    }
  ]
}
```

### `theme.json`
* 특정 테마에 어떤 카테고리가 속하는지 알 수 있습니다.

```json
{
  "theme": [
    { "name": "가족", "category": ["부모"] },
    { "name": "자기소개", "category": ["취미"] }
  ]
}
```

> `ThemeManager`가 카테고리 "이름 → 번호"로 매핑한 뒤, 선택된 테마의 모든 하위 chunk_weight를 초기 10으로 부스트합니다.

---

## 아키텍처 개요

* Data Models: `Material`, `Chunk`, `Category`, `EngineState`
* Core Engine: `InterviewEngine`

  * `select_material()` → 다음 질문의 소재 선택
  * `select_question_in_material(material)` → 소재 내부의 타겟(w/ex/con) 결정 및 문장 생성
  * `update_after_answer(mapped_ids, current_id)` → 카운트/가중치 업데이트 + streak 관리
  * `should_stop(transcript_len)` → 종료 판정
* Theme Integration: `ThemeManager`

---

## 설치 & 실행 (Quick Start)

```bash
python>=3.9
pip install -r requirements.txt  # (필요 시)
```

### 1) 카테고리 트리 로딩

```python
from engine import InterviewEngine
from pathlib import Path
import json

# material.json 로딩
category_json = json.loads(Path("material.json").read_text(encoding="utf-8"))

# 트리 구성
categories = InterviewEngine.build_categories_from_category_json(category_json)
engine = InterviewEngine(categories, m_ratio=0.70, n_chars_target=50_000)
```

### 2) 테마 부스트

```python
from engine import ThemeManager

theme_json = json.loads(Path("theme.json").read_text(encoding="utf-8"))
manager = ThemeManager(engine, theme_json)
manager.select_theme("가족")   # 포함된 카테고리의 chunk_weight가 10으로 설정
```

### 3) 질문 루프 

```python
# 1) 소재 선택
cat_num, ch_num, m_num = engine.select_material()
material = engine._get_material(cat_num, ch_num, m_num)

# 2) 소재 내 질문 선택
q = engine.select_question_in_material(material)
if q is None:
    # 모든 항목이 채워졌다면 다른 소재로 전환
    pass
else:
    print("Q:", q)
    # 사용자의 답변을 받았다고 가정
    answer = "...사용자 답변..."

    # 3) 답변 → 소재 매핑 (현재는 current_id만 매핑, NLP 연동 시 수정)
    current_id = (cat_num, ch_num, m_num)
    mapped_ids = engine.map_answer_to_materials(answer, current_id)

    # 4) 업데이트
    engine.update_after_answer(mapped_ids, current_id)

# 4) 종료 판정
if engine.should_stop(transcript_len=12345):
    print("종료 조건 충족")
```

---

## 선택/진행 로직 상세

### 1. 소재 선택 규칙 `select_material()`

1. 직전 소재 유지: `streak < 3` **그리고** `material_count < 1`이면 같은 소재로 연결 질문
2. ε-greedy 탐색 : 10% 확률로 전역 랜덤 소재 선택
3. 후보 정렬 :
   * (1순위) `chunk_weight` 내림차순 (테마 부스트 + 동일 덩어리 보상)
   * (2순위) 같은 chunk 내에서는 `sumwc = sum(w1..w6)+ex+con` 오름차순(덜 채워진 소재 우선)
   * (tie-break) 동률이면 무작위 선택

### 2. 소재 내 질문 선택 `select_question_in_material()`

* 우선순위: `w2(어떻게)` → `ex` → `con` → 나머지 `w1, w3, w4, w5, w6`
* 모두 채워졌다면 `None` 반환(다른 소재로 전환)

### 3. 답변 반영 `update_after_answer()`

* `w1~w6`는 각각 0/1 상한으로 +1 (이미 1이면 그대로)
* `ex`, `con`은 답변에서 충족되었다고 보면 1로 세팅
* `material_count = 1` 조건: `sum(w) ≥ 3` AND `ex==1` AND `con==1`
* 동일 덩어리 보상: 해당 `chunk_weight += 1`
* streak: 같은 소재면 `+1`, 아니면 현재 소재로 갱신하고 `1`로 리셋

### 4. 종료 조건 `should_stop()`

* A: 충분히 채워진 소재 수 ≥ 30
* B: 카테고리 커버리지 ≥ m_ratio (기본 0.70)
* C: (누적 글자수 × 0.75) ≥ n_chars_target (기본 50,000)
* 최종: `A AND (B OR C)`

---

## NLP 연동 포인트 
### 1) `map_answer_to_materials(answer, current_id) -> List[MaterialId]`
* 답변을 소재에 mapping 

### 2) `generate_question(material, target) -> str`
* 질문 생성

---

## 파라미터/튜닝 가이드

* `epsilon`(탐색률): 0.05~0.15 사이에서 조정 권장
* `initial_weight`(테마 부스트): 기본 10, 테마 집중도를 더 높이고 싶으면 15~20
* `m_ratio`(커버리지): 0.6~0.8 사이에서 도메인에 맞게 조정
* `n_chars_target`: 최종 원고 목표 길이(자서전 제품 스펙에 맞게 설정)

---

## 라이선스
* 

---

## FAQ

**Q1. 같은 소재를 몇 번까지 연속으로 물어보나요?**
A. 기본 **최대 3회**입니다. 이후에는 다른 소재로 전환됩니다.

**Q2. 예시/유사사례(ex/con) 카운트는 어떻게 올라가나요?**
A. 현재는 답변마다 1로 세팅하는 보수적 구현입니다. 실제 제품에서는 **답변 분석 결과**로만 올리도록 바꾸는 것을 권장합니다.

**Q3. 커버리지 70%는 무엇을 의미하나요?**
A. 각 카테고리에서 `material_count==1`인 소재의 비율이 70% 이상임을 의미합니다.

