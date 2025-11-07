"""
AI Autobiography Interview Engine (자서전 인터뷰 엔진)
---------------------------------------------------
- 데이터 구조: category(분류) → chunk(덩어리) → material(소재)
- 카운트 행렬: 각 소재별 w1~w6(육하원칙), ex(예시), con(유사사례), material_count(소재 달성 여부)
- chunk_weight: 덩어리 우선순위 가중치(테마 선택 시 초기 10으로 우선순위 먹음)
- last_material_id / last_material_streak: 같은 소재를 최대 3회까지 연속 질문
- ε-greedy 탐색: 10%는 완전 랜덤 탐색, 90%는 우선순위 규칙 적용
- 종료 조건: (충분히 채워진 소재 ≥ 30) AND (카테고리 커버리지 ≥ 70% OR 원고 길이 ≥ 5만자×0.75)

※ 실제 NLP 연동 지점
  - map_answer_to_materials(): 답변 → 여러 소재 매핑 (현재는 현재 소재만 반환)
  - generate_question(): 선택된 소재·타겟에 맞춰 런타임 질문 문장을 생성
"""
from __future__ import annotations
from dataclasses import dataclass, field
from typing import Dict, Tuple, List, Optional, Iterable
import random

MaterialId = Tuple[int, int, int]  # (category_num, chunk_num, material_num)

# ----------------------------- Data Models ----------------------------- #
# NOTE: This engine supports two external JSONs
#  1) theme.json: { "theme": [{"name": str, "category": [category_name, ...]}] }
#  2) category.json : { "category": [ {"name": str, "chunk": [{"name": str, "material": [str, ...]}]} ] }
# You can build in-memory structures from (2) with `build_categories_from_category_json()` below.

@dataclass
class Material:
    material_num: int # 소재(material)의 고유 번호 (한 덩어리 안에서 1부터 부여)
    material_name: str # 소재 이름(내용)
    w: List[int] = field(default_factory=lambda: [0, 0, 0, 0, 0, 0])  # w1..w6 육하원칙별 플래그 (0/1, 상한)
    ex: int = 0  # 예시 (0/1, 상한)
    con: int = 0  # 유사사례 (0/1, 상한)
    material_count: int = 0  # 이 소재가 '충분히 채워졌는지'를 나타내는 플래그(0/1)

    # w1~w6의 합. '해당 소재에서 어느 정도 질문이 진행되었는지'의 간단한 지표로 사용
    def sum_w(self) -> int:
        return sum(self.w)
    
    # 소재 선택 우선순위용 진행도 스코어: sum(w1..w6) + ex + con
    def progress_score(self) -> int:
        return self.sum_w() + self.ex + self.con

    # material count가 되는 조건
    def mark_filled_if_ready(self) -> None:
        if self.sum_w() >= 3 and self.ex ==1 and self.con == 1:
            self.material_count = 1
    
    # 소내 내 모든 항목이 모두 채워졌는지 판정 -> 모두 채워지면 더 이상 해당 소재 질문X
    def is_fully_completed(self) -> bool:
        return all(v == 1 for v in self.w) and self.ex == 1 and self.con == 1 # w1~w6 모두 1 AND ex==1 AND con==1


@dataclass
class Chunk:
    chunk_num: int # 덩어리(Chunk)의 고유 번호 (한 카테고리 안에서 1부터 부여)
    chunk_name: str # 덩어리 이름 (예: "프로필", "성격", "만남" 등)

    # 이 덩어리에 속하는 소재(Material)들 - [키: material_num, 값: Material 객체]
    materials: Dict[int, "Material"] = field(default_factory=dict)  


@dataclass
class Category:    
    category_num: int # 카테고리의 고유 번호 (전체 카테고리에서 1부터 부여)
    category_name: str # 카테고리 이름 (예: "부모", "연인", "취미" 등)

    # 이 카테고리에 속하는 덩어리(Chunk)들 - [키: chunk_num, 값: Chunk 객체] 
    chunks: Dict[int, Chunk] = field(default_factory=dict)

    # 덩어리별 우선순위 가중치(Chunk Weight)
    """ - 초기값은 0
        - 테마(theme)를 선택하면 해당 테마에 속한 카테고리의 모든 하위 덩어리는 10으로 부스트
        - 답변 업데이트 시 같은 덩어리의 chunk_weight가 +1 증가 (동일 덩어리 보상) """   
    chunk_weight: Dict[int, int] = field(default_factory=dict)

    # 현재 가지고 있는 모든 덩어리에 대해 chunk_weight 키를 보장(없으면 0으로 세팅)
    def ensure_weights(self) -> None:
        for ch_num in self.chunks.keys():
            self.chunk_weight.setdefault(ch_num, 0) # setdefault: 키가 없으면 0으로 넣고, 있으면 기존 값 유지


@dataclass
class EngineState:
    # 직전에 질문한 소재의 식별자 (category_num, chunk_num, material_num)
    last_material_id: Optional[MaterialId] = None

    # 같은 소재를 연속으로 질문한 횟수: 3이 되면 다음 질문에서는 소재를 전환
    last_material_streak: int = 0

    # ε-greedy 탐색 비율 (기본 0.10 = 10%): 질문 후보 선택 시, 이 확률로 우선순위를 무시하고 랜덤으로 선택하여 탐색 성향 유지
    epsilon: float = 0.10


# ----------------------------- Engine ----------------------------- #
class InterviewEngine:

    # -------- 엔진 초기화 -------- #
    def __init__(self, categories: Dict[int, Category], m_ratio: float = 0.70, n_chars_target: int = 50_000):
        self.categories = categories # 카테고리 전체 트리 구조
        self.m_ratio = m_ratio # 카테고리별 커버리지 목표(기본 70%)
        self.n_chars_target = n_chars_target # 최종 원고 목표 길이(기본 50,000자)
        self.state = EngineState()      
        self.theme_initialized: bool = False # 테마 초기 부스트가 이미 수행되었는지 여부(boost를 초기에 한 번만 적용하기 위함)

        for cat in self.categories.values():
            cat.ensure_weights()  # 각 chunk의 weight 키를 0으로 보장

    # -------- 테마 선택 -------- #
    # 선택된 테마에 속한 카테고리들의 하위 chunk를 일괄 부스트 (initial_weight = 10)
    def boost_theme(self, category_nums: Iterable[int], initial_weight: int = 10, *, force: bool = False) -> None:
        if self.theme_initialized and not force:
            return # 이미 초기화됨 → 아무 작업 안 함
        for cnum in category_nums:
            cat = self.categories.get(cnum)
            if not cat:
                continue
            for ch_num in cat.chunks.keys(): 
                if cat.chunk_weight.get(ch_num, 0) == 0:  # 이미 초기화된 경우 건드리지 않음. 단, force=True이면 재적용 가능
                    cat.chunk_weight[ch_num] = initial_weight  # 테마에 포함된 chunk가 0(미부스트)인 경우에만 10으로 세팅하고, 이미 값이 있으면 건드리지 않음
        self.theme_initialized = True       
            

    # -------- 다음 질문 : 소재 선택 -------- #  
        """
        <우선순위 규칙>
        1) 직전 소재 유지(단, streak < 3 AND 아직 material_count < 1)
            - 사용자가 같은 흐름을 이어서 말할 때 맥락 유지를 위해 최대 3번까지 같은 소재를 유지
            - 소재가 이미 '충분히 채워진(material_count == 1)' 경우엔 전환
        2) chunk_weight 내림차순
            - 테마 부스트(초기 10) 및 동일 덩어리 보상(+1)으로 가중치가 큰 덩어리부터 우선
        3) 같은 chunk 내에서는 sumwc(w1~6, ex, con) 오름차순
            - 덜 채워진 소재(질문을 덜 받은 소재)를 먼저 채우기
        4) 같은 덩어리 내 소재의 sumwc가 같다(동률)
            - 같은 우선순위의 후보들 중 무작위 선택
        이때 ε-greedy로 10% 확률은 우선순위를 무시하고 랜덤 탐색
        """   
    def select_material(self) -> MaterialId:

        # 1) 직전 소재 유지 (streak < 3 AND 아직 material_count < 1)
        if self.state.last_material_id: 
            cat, ch, m = self.state.last_material_id
            mat = self._get_material(cat, ch, m)
            if mat: # 직전 소재가 존재하고
                # 같은 소재를 연속으로 물은 횟수가 3 미만이며 그 소재가 아직 충분히 채워지지 않았다면
                if self.state.last_material_streak < 3 and mat.material_count < 1: 
                    return self.state.last_material_id #그대로 이어서 질문한다(맥락 유지)

        # 2) ε-greedy 탐색
        if random.random() < self.state.epsilon: # 난수(0~1) < epsilon(기본 0.10)이면, 우선순위를 무시하고 완전 랜덤 후보를 선택
            return self._random_material_id()

        # 3) 후보 목록 구축(Build candidates)
        candidates = [] 
        # 모든 카테고리 → 모든 덩어리 → 모든 소재를 순회하여 선택에 필요한 메타(덩어리 가중치, 진행도 지표 등)를 한 곳에 모은다
        for cat in self.categories.values():
            for ch_num, ch in cat.chunks.items():
                cw = cat.chunk_weight.get(ch_num, 0)  # 덩어리 우선순위 가중치(chunk weight)
                for m_num, mat in ch.materials.items():
                    candidates.append({
                        "id": (cat.category_num, ch_num, m_num),  # 최종 반환용 식별자
                        "chunk": ch_num,                          # 같은 덩어리 내에서 비교할 경우 사용
                        "cw": cw,                                 # 2순위: 덩어리 가중치(내림차순)
                        "sumwc": mat.progress_score(),            # 3순위: 진행 정도(오름차순) - sum(w1..w6) + ex + con
                    }) 
        # 후보가 하나도 없으면 설계 이상 상황 → 예외 발생
        if not candidates:
            raise RuntimeError("No materials present.")


        # 4) 정렬: chunk_weight DESC, sumw ASC
        # 덩어리 가중치가 큰 순서, 같은 덩어리 내에서는 sum(w)가 작은 순서(덜 채워진 소재 우선)
        candidates.sort(key=lambda x: (-x["cw"], x["sumwc"]))


        # 5) 같은 덩어리 내 소재의 sumwc가 같다(동률) -> 랜덤 선택
        best_group = [candidates[0]]
        for c in candidates[1:]:
            same_weight = (c["cw"] == best_group[0]["cw"])
            same_chunk  = (c["chunk"] == best_group[0]["chunk"])
            same_sumwc   = (c["sumwc"] == best_group[0]["sumwc"])
            if same_weight and same_chunk and same_sumwc:
                best_group.append(c)
            else:
                break
        return random.choice(best_group)["id"]


    # -------- 다음 질문 : 소재 내 질문 선택 -------- #        
    """
        - 우선순위: w2("어떻게") → ex(예시) → con(유사사례) → 나머지 w (w1,w3,w4,w5,w6)
        - 모두 채워졌다면 심화 질문(deepening)으로 전환
        - 실제 문장 생성은 generate_question()에서 수행
    """      
    def select_question_in_material(self, material: Material) -> Optional[str]:
        # 0) 모든 항목이 채워진 경우: 더 이상 질문하지 않음 → None 반환 (다른 소재로 전환)
        if material.is_fully_completed():
            return None

        # 1) 채워야 할 타겟 수집
        w1, w2, w3, w4, w5, w6 = material.w
        needs: List[str] = []
        if w2 == 0: needs.append("w2")              # 1순위: '어떻게'
        if material.ex == 0: needs.append("ex")     # 2순위: 예시
        if material.con == 0: needs.append("con")   # 3순위: 유사사례
        for label, val in [("w1", w1), ("w3", w3), ("w4", w4), ("w5", w5), ("w6", w6)]:
            if val == 0:
                needs.append(label)

        # 2) 남은 것이 없으면(사실상 fully에 가까움) 안전하게 전환
        if not needs:
            return None

        # 3) 타겟 결정 및 질문 생성
        target = needs[0]
        return self.generate_question(material, target)


    # -------- 답변 후 카운트/가중치 업데이트 -------- #
    """
    1) 카운트/가중치 업데이트 규칙
    - w1~w6: 각각 +1 (단, 0/1 상한 → 이미 1이면 그대로 1 유지)
    - ex / con: 이번 답변에서 예시/유사사례가 확보되었다고 보고 1로 설정(0→1)
    - material_count(소재의 충분한 답변 정도): sum(w) ≥ 3 AND ex==1 AND con==1 이면 1로 설정
    - 동일 덩어리 보상: 소속 chunk의 chunk_weight += 1

    2) streak(연속 질문) 관리
    - 같은 소재(current_id)로 이어서 질문했다면 last_material_streak += 1
    - 다른 소재로 전환했다면 last_material_id 갱신 + streak = 1로 리셋
    """
    def update_after_answer(self, mapped_ids: Iterable[MaterialId], current_id: MaterialId) -> None:
        # mapped_ids: 이번 답변에 관련된 모든 소재 목록 (NLP 매핑 결과)
        
        # 1) 카운트/가중치 업데이트
        for (cnum, chnum, mnum) in mapped_ids:
            # 존재 확인
            mat = self._get_material(cnum, chnum, mnum)
            if not mat:
                continue

            # w1~w6 카운트: 각 항목을 +1 하되, 0/1 상한을 적용
            mat.w = [min(v + 1, 1) for v in mat.w]

            # 예시/유사사례 플래그: 답변에 포함되었다고 간주하면 1로 올림(0→1)
            mat.ex, mat.con = 1, 1

            # material_count 판정 : [sum(w) ≥ 3 AND ex==1 AND con==1] -> material_count=1
            mat.mark_filled_if_ready()

            # 어느 덩어리 내 소재에 대해 답변하면 덩어리 가중치 +1 : 이후 chunk_weight가 큰 덩어리 우선순위
            cat = self.categories[cnum]
            cat.chunk_weight[chnum] = cat.chunk_weight.get(chnum, 0) + 1

        # 2) streak(연속 질문) 관리
        if self.state.last_material_id == current_id: # 현재 질문한 소재(current_id)가 직전 소재와 같다면 연속 횟수 +1
            self.state.last_material_streak += 1 
        else:                                         # 다르면 last_material_id를 현재로 바꾸고 streak=1로 재설정
            self.state.last_material_id = current_id
            self.state.last_material_streak = 1


    # -------- Stop(종료) 조건 -------- #
    def should_stop(self, transcript_len: int) -> bool:
        cond_a = self._count_filled_materials() >= 30            # 충분히 채워진 소재 수 ≥ 30
        cond_b = self._categories_meet_ratio(self.m_ratio)       # 카테고리별로 (충분히 채워진 소재 / 전체 소재) ≥ m_ratio(기본 0.70)
        cond_c = (transcript_len * 0.75) >= self.n_chars_target  # 계산법: (누적 답변 글자수 × 0.75) ≥ n_chars_target(기본 50,000)
        return cond_a and (cond_b or cond_c)  # 최종 종료: A AND (B OR C)


    # ------------------------- Helper methods ------------------------- #
    # -------- 소재 가져오기 -------- #
    def _get_material(self, cnum: int, chnum: int, mnum: int) -> Optional[Material]:
        cat = self.categories.get(cnum)      # 카테고리 조회
        if not cat:
            return None
        ch = cat.chunks.get(chnum)           # 카테고리 내 덩어리 조회
        if not ch:
            return None
        return ch.materials.get(mnum)        # 덩어리 내 소재 반환(없으면 None)


    # -------- 랜덤 소재 뽑기(ε-greedy 탐색 시) -------- #
    def _random_material_id(self) -> MaterialId:
        cat = random.choice(list(self.categories.values()))   # 무작위 카테고리
        ch = random.choice(list(cat.chunks.values()))         # 그 안에서 무작위 덩어리
        mat = random.choice(list(ch.materials.values()))      # 그 안에서 무작위 소재
        return (cat.category_num, ch.chunk_num, mat.material_num)


    # -------- 충분히 답변된 소개 개수 세기(종료조건 a에 사용) -------- #
    def _count_filled_materials(self) -> int:
        return sum(
            1    # 맨 아랫줄 조건에 의해 내보내진 1들을 전부 더함 → 개수가 됨
            for cat in self.categories.values()        # 모든 카테고리 순회
            for ch in cat.chunks.values()              # → 하위 덩어리 순회
            for mat in ch.materials.values()           # → 하위 소재 순회
            if mat.material_count == 1                 # 조건 만족하는 매번에 숫자 1을 내보냄                                            
        )

    # -------- 카테고리별 커버리지 비율이 목표치(ratio) 이상인지 확인 -------- #
    def _categories_meet_ratio(self, ratio: float) -> bool:
        for cat in self.categories.values():
            
            # 카테고리 내 모든 소재를 flatten (카테고리->덩어리->소재에서 덩어리 out)
            mats = []
            for ch in cat.chunks.values():           # 카테고리 내 모든 덩어리
                for m in ch.materials.values():      # 덩어리 내 모든 소재
                    mats.append(m)                   # 하나의 평평한 리스트로 모음

            if not mats: # 소재가 하나도 없는 카테고리는 스킵(비어있는 카테고리로 실패 처리하지 않음)     
                continue
            
            # mats(하나의 카테고리)에서 material_count == 1(충분히 채워진) 소재 개수 세기
            filled = sum(1 for m in mats if m.material_count == 1) 

            coverage = filled / len(mats) # 커버리지 정의: (해당 카테고리에서 충분히 채워진 소재 수) / (전체 소재 수)
            
            if coverage < ratio: # 카테고리마다 커버리지 값이 ratio(예: 0.70) 이상이어야 True
                return False
        return True

    def map_answer_to_materials(self, answer: str, current_id: MaterialId) -> List[MaterialId]:
        """

        답변을 소재에 mapping
        AI 일시키키 부분

        """
        return [current_id]

    def generate_question(self, material: Material, target: str) -> str:
        """

        질문 생성
        AI 일시키키 부분

        """

        # 아래는 just 예시 -> AI한테 시키면 됨
        six = {
            "w1": "언제",
            "w2": "어떻게",
            "w3": "누가",
            "w4": "무엇을",
            "w5": "어떻게(절차/수단2)",
            "w6": "왜",
        }
        if target in six:
            return f"{material.material_name}에 대해 '{six[target]}' 측면에서 더 구체적으로 들려주세요."
        if target == "ex":
            return f"{material.material_name}와 관련된 구체적인 '예시 한 가지'를 자세히 이야기해 주세요."
        if target == "con":
            return f"{material.material_name}와 비슷한 '유사 사례'가 있었다면 비교해서 설명해 주세요."
        return f"{material.material_name}에 대해 아직 다루지 못한 세부사항이나 감정, 맥락을 더 이야기해 주세요."


   # -------- JSON mapping -------- #
    """
        한국어 구조의 category JSON을 받아 Category/Chunk/Material 트리로 변환한다.
        번호는 입력 순서대로 1부터 부여한다.
        초기 상태:
        - 모든 w/ex/con/material_count = 0 (정수 0/1 체계)
        - 모든 chunk_weight = 0 (테마 부스트 이전 기본값)
        기대 JSON 스키마(예시):
        {
            "category": [
            { "name": "부모",
                "chunk": [
                { "name": "프로필", "소재": ["name","출생지(고향)", ...] },
                ...
                ]
            },
            ...
            ]
        }
        반환:
        - {category_num: Category} 딕셔너리
    """
    def build_categories_from_category_json(kor_json: dict) -> Dict[int, Category]:

        categories: Dict[int, Category] = {}                # 최종 반환 딕셔너리: 카테고리 번호 → Category 객체
        cat_num = 1                                         # category_num : 1부터 증가

        # 1) 최상위: category 리스트를 순회
        for cat_entry in kor_json.get("category", []):
            cat_name = cat_entry.get("name")                # 카테고리 이름 
            chunks: Dict[int, Chunk] = {}                   # 이 카테고리에 소속된 Chunk들을 담을 딕셔너리: chunk_num → Chunk 객체
            ch_num = 1                                      # chunk_num : 1부터 증가

            # 2) 카테고리 하위의 chunk 리스트 순회
            for ch_entry in cat_entry.get("chunk", []):               
                ch_name = ch_entry.get("name")              # 덩어리(Chunk) 이름
                materials: Dict[int, Material] = {}         # 이 덩어리에 소속된 Material들을 담을 딕셔너리: material_num → Material  
                m_num = 1                                   # material_num : 1부터 증가

                # 3) 한 덩어리 내 소재 리스트를 순회 -> Material 객체 생성
                for mat_name in ch_entry.get("material", []):
                    # Material은 초기 상태에서 w=[0..], ex=0, con=0, material_count=0 로 생성됨
                    materials[m_num] = Material(
                        material_num=m_num,
                        material_name=str(mat_name)
                    )
                    m_num += 1

                # 4) 한 덩어리의 Material 딕셔너리를 채운 뒤, Chunk 객체로 감싸 chunks에 등록
                chunks[ch_num] = Chunk(
                    chunk_num=ch_num,
                    chunk_name=str(ch_name),
                    materials=materials
                )
                ch_num += 1

            # 5) 카테고리 이름과 chunks를 이용해 Category 객체 생성
            category = Category(
                category_num=cat_num,
                category_name=str(cat_name),
                chunks=chunks
            )

            # 6) chunk_weight 초기화 보장
            category.ensure_weights() # 일단 0으로 채움

            # 7) categories 딕셔너리에 등록 후, 다음 카테고리를 위해 cat_num 증가
            categories[cat_num] = category
            cat_num += 1

        return categories



# ----------------------------- Theme integration ----------------------------- #
""" theme.json을 읽어 테마명 → 카테고리 번호 목록으로 매핑하고,
    선택된 테마의 카테고리 하위 chunk_weight를 '초기 1회' 10으로 세팅(부스트)한다.
"""
class ThemeManager:
    def __init__(self, engine: InterviewEngine, theme_json: dict):
        self.engine = engine
        # 엔진 내부 카테고리에서 이름→번호 매핑 생성
        self.category_name_to_num: Dict[str, int] = {
            cat.category_name: cat.category_num for cat in engine.categories.values()
        }
        # {테마명: [카테고리번호,...]} 형태로 정규화
        themes = {}
        for t in theme_json.get("theme", []):
            theme_name = t.get("name")
            cat_names = t.get("category", [])
            nums = [self.category_name_to_num[n] for n in cat_names if n in self.category_name_to_num]
            if nums:
                themes[theme_name] = nums
        self.themes = themes

    def select_theme(self, theme_name: str, initial_weight: int = 10, *, force: bool = False) -> List[int]:
        """테마를 선택하여 해당 카테고리들의 chunk_weight를 초기 10으로 세팅한다.
        - 엔진의 boost_theme()는 '초기 1회만' 적용되도록 가드되어 있음(theme_initialized).
        - force=True로 주면 초기화 가드를 무시하고 재적용 가능.
        - 반환값: 부스트된 카테고리 번호 목록
        """
        nums = self.themes.get(theme_name, [])
        if nums:
            self.engine.boost_theme(nums, initial_weight=initial_weight, force=force)
        return nums

