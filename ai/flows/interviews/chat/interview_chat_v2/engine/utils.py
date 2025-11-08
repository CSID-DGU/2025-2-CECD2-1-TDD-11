from typing import Dict, List, Optional
from .core import InterviewEngine, MaterialId, Category

#기존 알고리즘 - 축 감지 힌트
HINTS = {
    "who": ["누가", "어떤 사람", "본인", "부모", "친구", "선생님", "상사", "동료"],
    "why": ["왜", "이유", "계기", "동기", "목적", "결심", "결정"],
    "when": ["언제", "시기", "연도", "해", "달", "날", "때", "초등학교", "대학", "입사"],
    "how": ["어떻게", "방법", "방식", "과정", "절차", "수단", "전략", "구체적으로"],
    "where": ["어디", "장소", "지역", "도시", "고향", "학교", "회사", "부서", "지점"],
    "what": ["무엇", "무슨", "어떤 일", "내용", "사건", "경험", "에피소드"],
}
EX_HINTS = ["예시", "사례", "예를", "예로", "구체적 사례"]
CON_HINTS = ["구체적", "구체적으로", "디테일", "상세히", "세부적으로"]

#V2 추가 함수 - 힌트 매칭
def hit_any(text: str, words: list) -> bool:
    if not text or not words:
        return False
    return any(w in text for w in words)

#기존 알고리즘 - 소재 매칭
def find_matching_materials(answer: str, current_material: str, material_data: dict) -> List[str]:
    """답변에서 매칭되는 소재들 찾기"""
    all_materials = []
    for category in material_data.get("category", []):
        for chunk in category.get("chunk", []):
            for material in chunk.get("material", []):
                all_materials.append(material)
    
    matched = []
    for material in all_materials:
        if material in answer:
            matched.append(material)
    
    if current_material and current_material in answer and current_material not in matched:
        matched.append(current_material)
    
    return matched

#기존 알고리즘 - 소재 ID 찾기
def find_material_id(engine: InterviewEngine, material_name: str) -> Optional[MaterialId]:
    """소재 이름으로 MaterialId 찾기"""
    for cat_num, category in engine.categories.items():
        for chunk_num, chunk in category.chunks.items():
            for mat_num, material in chunk.materials.items():
                if material.material_name == material_name:
                    return (cat_num, chunk_num, mat_num)
    return None

#V2 추가 함수 - 세션 상태 복원
def restore_categories_state(categories: Dict[int, Category], metrics_categories) -> None:
    """이전 메트릭에서 카테고리 상태 복원 (배열 구조)"""
    # 배열 구조 처리
    if isinstance(metrics_categories, list):
        for cat_data in metrics_categories:
            cat_num = cat_data.get("category_num")
            if cat_num not in categories:
                continue
                
            category = categories[cat_num]
            
            # chunk_weight 복원
            if "chunk_weight" in cat_data:
                category.chunk_weight.update(cat_data["chunk_weight"])
            
            # chunks 상태 복원 (배열)
            for chunk_data in cat_data.get("chunks", []):
                chunk_num = chunk_data.get("chunk_num")
                if chunk_num not in category.chunks:
                    continue
                    
                chunk = category.chunks[chunk_num]
                
                # materials 상태 복원 (배열)
                for mat_data in chunk_data.get("materials", []):
                    mat_num = mat_data.get("material_num")
                    if mat_num not in chunk.materials:
                        continue
                        
                    material = chunk.materials[mat_num]
                    material.w = mat_data.get("w", [0, 0, 0, 0, 0, 0])
                    material.ex = mat_data.get("ex", 0)
                    material.con = mat_data.get("con", 0)
                    material.material_count = mat_data.get("material_count", 0)
    
    # 기존 객체 구조 호환성 유지
    elif isinstance(metrics_categories, dict):
        for cat_key, cat_data in metrics_categories.items():
            cat_num = cat_data.get("category_num")
            if cat_num not in categories:
                continue
                
            category = categories[cat_num]
            
            # chunk_weight 복원
            if "chunk_weight" in cat_data:
                category.chunk_weight.update(cat_data["chunk_weight"])
            
            # materials 상태 복원
            for chunk_key, chunk_data in cat_data.get("chunks", {}).items():
                chunk_num = chunk_data.get("chunk_num")
                if chunk_num not in category.chunks:
                    continue
                    
                chunk = category.chunks[chunk_num]
                
                for mat_key, mat_data in chunk_data.get("materials", {}).items():
                    mat_num = mat_data.get("material_num")
                    if mat_num not in chunk.materials:
                        continue
                        
                    material = chunk.materials[mat_num]
                    material.w = mat_data.get("w", [0, 0, 0, 0, 0, 0])
                    material.ex = mat_data.get("ex", 0)
                    material.con = mat_data.get("con", 0)
                    material.material_count = mat_data.get("material_count", 0)