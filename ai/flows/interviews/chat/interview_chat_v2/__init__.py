from promptflow.core import tool
from typing import Dict, List, Tuple
import json
import re
import sys
import os
import time
from uuid import uuid4

# engine 모듈 import 경로 추가
current_dir = os.path.dirname(__file__)
parent_dir = os.path.dirname(current_dir)
sys.path.insert(0, parent_dir)

from engine.core import InterviewEngine
from engine.utils import HINTS, EX_HINTS, CON_HINTS, hit_any, restore_categories_state
from engine.generators import generate_first_question, generate_question_llm, generate_material_gate_question
import redis

from logs import get_logger

logger = get_logger()

# 실제 함수 구현
def publish_delta_change(user_id, autobiography_id, theme_id, category_id, chunk_deltas=None, material_deltas=None):
    """실제 변화량을 CategoriesPayload로 전송"""
    try:
        from logs import get_logger
        logger = get_logger()
        logger.info(f"[PUBLISH_DELTA] Called with user_id={user_id}, autobiography_id={autobiography_id}, theme_id={theme_id}, category_id={category_id}")
        
        # serve 디렉토리 경로 추가
        serve_dir = os.path.join(current_dir, '..', '..', '..', '..', 'serve')
        sys.path.insert(0, serve_dir)
        from stream import publish_categories_message
        from stream.dto import ChunksPayload, MaterialsPayload, CategoriesPayload
        
        # None 값 체크
        if user_id is None or autobiography_id is None:
            logger.info("[PUBLISH_DELTA] Skipping due to None values")
            return
            
        # 현재 시간
        from datetime import datetime, timezone
        timestamp = datetime.now(timezone.utc)
        
        # ChunksPayload 생성 (chunk_id를 chunkOrder로 매핑)
        chunks = []
        if chunk_deltas:
            for chunk_data in chunk_deltas:
                chunks.append(ChunksPayload(
                    categoryId=category_id,
                    chunkOrder=chunk_data.get('chunk_id', 0),  # chunk_id → chunkOrder
                    weight=chunk_data.get('weight_delta', 0),   # 변화량
                    timestamp=timestamp
                ))
        
        # MaterialsPayload 생성 (material_id를 materialOrder로 매핑)
        materials = []
        if material_deltas:
            for material_data in material_deltas:
                materials.append(MaterialsPayload(
                    chunkId=material_data.get('chunk_id', 0),     # 어느 chunk에 속하는지
                    materialOrder=material_data.get('material_id', 0), # material_id → materialOrder
                    example=material_data.get('example_delta', 0),     # 변화량
                    similarEvent=material_data.get('similar_event_delta', 0), # 변화량
                    count=material_data.get('count_delta', 0),         # 변화량
                    principle=material_data.get('principle_delta', [0,0,0,0,0,0]), # 변화량 배열
                    timestamp=timestamp
                ))
        
        # CategoriesPayload 생성 및 전송
        payload = CategoriesPayload(
            autobiographyId=int(autobiography_id),
            userId=int(user_id),
            themeId=theme_id,
            categoryId=category_id,
            chunks=chunks,
            materials=materials
        )
        
        # print(f"[AI_SEND] CategoriesPayload: autobiographyId={payload.autobiographyId}, userId={payload.userId}, themeId={payload.themeId}, categoryId={payload.categoryId}, chunks={len(payload.chunks)}, materials={len(payload.materials)}")
        
        publish_categories_message(payload)
        # print(f"[DEBUG] Published delta change: category={category_id}, {len(chunks)} chunks, {len(materials)} materials")
        
    except Exception as e:
        print(f"[WARN] Delta 발행 실패: {e}")
        pass


# ------------------------ 간단 헬퍼 ------------------------

def _norm(s: str) -> str:
    """공백 제거 + trim 후 비교용 문자열로 정규화"""
    return re.sub(r"\s+", "", (s or "").strip())


def _build_materials_list(material_data: dict) -> List[str]:
    """카테고리-청크-소재 풀네임 리스트 (예: '카테고리 청크 소재')"""
    out: List[str] = []
    for category in material_data.get("category", []):
        c = category.get("name", "")
        for chunk in category.get("chunk", []):
            ch = chunk.get("name", "")
            for material in chunk.get("material", []):
                # material이 이제 {"order": 1, "name": "소재명"} 형태
                material_name = material.get("name", "") if isinstance(material, dict) else material
                out.append(f"{c} {ch} {material_name}")
    return out


def _load_mapping(mapping_path: str) -> Tuple[dict, dict]:
    """
    material_id_mapping.json 로드
    - 반환1: 원본 매핑 { "카테고리 청크 소재": [cat,chunk,mat], ... }
    - 반환2: 공백 제거 인덱스 { "카테고리청크소재": "카테고리 청크 소재", ... }
    """
    try:
        with open(mapping_path, "r", encoding="utf-8") as f:
            mapping = json.load(f)
        norm_index = {_norm(k): k for k in mapping.keys()}
        return mapping, norm_index
    except Exception as e:
        print(f"[ERROR] material_id_mapping.json 로드 실패: {e}")
        return {}, {}


def _call_llm_map_flow(flow_path: str, answer_text: str, materials_list: List[str], current_material: str) -> List[dict]:
    """LLM 플로우 호출"""
    if not os.path.exists(flow_path):
        return []

    try:
        from promptflow import load_flow
        flow = load_flow(flow_path)
        res = flow(answer_text=answer_text, materials_list=materials_list, current_material=current_material)
        items = res.get("analysis_result", [])
        
        print(f"[DEBUG] LLM raw response: {items}")
        
        # 문자열이면 JSON 파싱
        if isinstance(items, str):
            items = items.strip()
            # 마크다운 코드 블록 제거
            if items.startswith('```'):
                lines = items.split('\n')
                if lines[0].startswith('```'): lines = lines[1:]
                if lines and lines[-1].strip() == '```': lines = lines[:-1]
                items = '\n'.join(lines)
            items = json.loads(items)
        
        return items if isinstance(items, list) else []
    except Exception as e:
        print(f"[ERROR] LLM 플로우 호출 실패: {e}")
        return []  # 폴백: 빈 결과


# AI cat_num을 DB의 theme_id, category_order로 변환하는 함수
def convert_cat_num_to_db_mapping(cat_num):
    """AI의 cat_num(0-based)을 DB의 (theme_id, category_order)로 변환"""
    # material.json의 category order와 DB의 theme-category 매핑
    # material.json: order=1(부모), order=2(조부모), order=3(형제), order=4(자녀/육아), order=5(친척), order=6(가족사건), order=7(주거지), order=8(성격), order=9(결혼), order=10(배우자), order=11(친구), order=12(연인), order=13(반려동물), order=14(생애주기), order=15(직장), order=16(진로), order=17(문제해결), order=18(취미), order=19(금전), order=20(철학), order=21(생활)
    
    # DB 매핑 (theme_id, category_order) - material.json의 order 기준
    mapping = {
        1: (1, 1),   # 부모
        2: (1, 2),   # 조부모  
        3: (1, 3),   # 형제
        4: (1, 4),   # 자녀/육아
        5: (1, 5),   # 친척
        6: (1, 6),   # 가족 사건
        7: (4, 1),   # 주거지
        8: (5, 1),   # 성격
        9: (2, 2),   # 결혼
        10: (2, 3),  # 배우자
        11: (6, 1),  # 친구
        12: (2, 1),  # 연인
        13: (12, 1), # 반려동물
        14: (8, 1),  # 생애주기
        15: (7, 1),  # 직장
        16: (7, 2),  # 진로
        17: (7, 3),  # 문제해결(과정)
        18: (11, 1), # 취미
        19: (10, 1), # 금전
        20: (13, 1), # 철학
        21: (14, 1), # 생활
    }
    
    return mapping.get(cat_num, (1, 1))  # 기본값

@tool
def interview_engine(sessionId: str, answer_text: str, user_id: int, autobiography_id: int) -> Dict:
    """인터뷰 엔진 - Redis에서 세션 로드하여 다음 질문 생성"""

    # Redis에서 세션 로드
    import redis
    import os
    
    # 환경변수에서 Redis 설정 읽기
    redis_host = os.getenv('REDIS_HOST')
    redis_port = int(os.getenv('REDIS_PORT'))
    redis_client = redis.Redis(host=redis_host, port=redis_port, db=0, decode_responses=True)
    session_key = f"session:{sessionId}"
    session_data_raw = redis_client.get(session_key)
    
    # 세션 데이터 파싱
    session_data = None
    if session_data_raw:
        try:
            session_data = json.loads(session_data_raw) if isinstance(session_data_raw, str) else session_data_raw
            print(f"[DEBUG] Session loaded: last_question exists = {bool(session_data.get('last_question'))}")
        except Exception as e:
            print(f"[ERROR] Session parse failed: {e}")
            session_data = None

    # 첫 질문 분기
    is_first_question = not session_data or not session_data.get("last_question")
    print(f"[DEBUG] is_first_question: {is_first_question}")
    
    if is_first_question:
        preferred_categories = session_data.get("metrics", {}).get("preferred_categories", []) if session_data else []

        material_json_path = os.path.join(os.path.dirname(__file__), "data", "material.json")
        with open(material_json_path, 'r', encoding='utf-8') as f:
            material_data = json.load(f)

        categories = InterviewEngine.build_categories_from_category_json(material_data)
        engine = InterviewEngine(categories)
        
        # 테마 부스팅 적용
        if preferred_categories:
            engine.boost_theme(preferred_categories, initial_weight=10, force=True)
            print(f"[DEBUG] 테마 부스팅 적용: {preferred_categories}")

        metrics = {"preferred_categories": preferred_categories}
        result = generate_first_question(engine, metrics)
        result["last_answer_materials_id"] = []
        
        # 첫 질문도 세션에 저장
        session_update = {
            "metrics": metrics,
            "last_question": result["next_question"],
            "updated_at": time.time()
        }
        redis_client.set(session_key, json.dumps(session_update))
        print(f"[DEBUG] Session saved (first): {session_key}, question: {result['next_question']['text'][:30]}...")
        
        return result

    # 이후 질문 생성 준비
    question = session_data.get("last_question", {})
    # question이 문자열이면 JSON 파싱 시도
    if isinstance(question, str):
        try:
            question = json.loads(question)
        except:
            question = {}
    metrics = session_data.get("metrics", {})

    # material.json 로드 및 엔진 초기화
    material_json_path = os.path.join(os.path.dirname(__file__), "data", "material.json")
    try:
        with open(material_json_path, 'r', encoding='utf-8') as f:
            material_data = json.load(f)

        categories = InterviewEngine.build_categories_from_category_json(material_data)

        # 이전 메트릭이 있으면 상태 복원
        if metrics.get("categories"):
            restore_categories_state(categories, metrics["categories"])

        engine = InterviewEngine(categories)

        # 상태 복원
        engine_state = metrics.get("engine_state", {})
        engine.state.last_material_id = engine_state.get("last_material_id")
        engine.state.last_material_streak = engine_state.get("last_material_streak", 0)
        
        # preferred_categories 부스팅 (매번 적용)
        preferred_categories = metrics.get("preferred_categories", [])
        if preferred_categories:
            engine.boost_theme(preferred_categories, initial_weight=10, force=True)
            print(f"[DEBUG] 테마 부스팅 적용: {preferred_categories}")

    except Exception as e:
        print(f"[ERROR] 엔진 초기화 실패: {e}")
        return {"next_question": None, "last_answer_materials_id": []}

    # 답변 분석
    current_material = question.get("material", "") if isinstance(question, dict) else ""
    current_material_id = question.get("material_id") if isinstance(question, dict) else None
    is_first_question = not answer_text or not current_material
    
    # 현재 질문 소재의 전체 경로 찾기 (LLM에 전달용)
    current_material_full = current_material
    if current_material_id and isinstance(current_material_id, list) and len(current_material_id) == 3:
        cat_num, chunk_num, mat_num = current_material_id
        temp_cat = engine.categories.get(cat_num)
        if temp_cat:
            temp_chunk = temp_cat.chunks.get(chunk_num)
            if temp_chunk:
                temp_mat = temp_chunk.materials.get(mat_num)
                if temp_mat:
                    current_material_full = f"{temp_cat.category_name} {temp_chunk.chunk_name} {temp_mat.name}"

    matched_materials: List[str] = []
    axes_analysis_by_material: Dict[str, dict] = {}
    mapped_ids: List[List[int]] = []

    if not is_first_question:
        # 6W 축 감지(휴리스틱, LLM 반환에 값 없을 때 보조로 사용)
        axes_evidence = {k: hit_any(answer_text, HINTS[k]) for k in HINTS.keys()}
        ex_flag = 1 if hit_any(answer_text, EX_HINTS) else 0
        con_flag = 1 if hit_any(answer_text, CON_HINTS) else 0
        if not con_flag and len(answer_text or "") >= 80:
            con_flag = 1

        # ---------- 간결해진 LLM 기반 소재 매핑 ----------
        # 상대 경로로 flows 디렉토리 찾기
        flows_dir = os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(current_dir))))
        map_flow_path = os.path.join(flows_dir, "flows", "interviews", "standard", "map_answer_to_materials", "flow.dag.yaml")
        materials_list = _build_materials_list(material_data)

        # 매핑 파일 로드
        mapping_path = os.path.join(os.path.dirname(__file__), "data", "material_id_mapping.json")
        material_mapping, norm_index = _load_mapping(mapping_path)

        llm_items = _call_llm_map_flow(map_flow_path, answer_text, materials_list, current_material_full)

        # 소재 매칭
        print(f"[DEBUG] LLM items count: {len(llm_items)}")
        for item in llm_items:
            if not isinstance(item, dict) or not item.get("material"):
                continue
            
            llm_material = item["material"]
            
            # 매핑 파일에서 찾기
            key = None
            for k in material_mapping.keys():
                # 1. 정확히 포함되는지 확인
                if llm_material in k:
                    key = k
                    break
                # 2. 공백 제거 후 포함되는지 확인
                if _norm(llm_material) in _norm(k):
                    key = k
                    break
                # 3. 소재명만 추출해서 비교 (예: "배움의 길(학교·직업훈련·삶에서 배운 것)")
                # LLM: "부모님 기본정보 배움의 길(...)" -> 마지막 부분만 추출
                parts = llm_material.split()
                if len(parts) >= 3:  # "카테고리 청크 소재명" 형태
                    material_name = " ".join(parts[2:])  # 소재명 부분
                    if material_name in k:
                        key = k
                        break
            
            if not key:
                print(f"[DEBUG] No match for: {llm_material}")
                continue

            matched_materials.append(key)
            axes_analysis_by_material[key] = item.get("axes", {})

        # 소재 ID 매핑
        for material_name in matched_materials:
            mid = material_mapping.get(material_name)
            if isinstance(mid, list) and len(mid) == 3:
                mapped_ids.append(mid)

        # LLM 분석 결과 반영
        for i, material_id in enumerate(mapped_ids):
            cat_num, chunk_num, mat_num = material_id
            material = engine._get_material(cat_num, chunk_num, mat_num)
            if not material:
                continue

            axes_data = axes_analysis_by_material.get(matched_materials[i], {})
            is_pass = axes_data.get("pass", 0) == 1

            if is_pass:
                # 회피/반감 응답: 소재 완료 처리
                material.principle = [1, 1, 1, 1, 1, 1]
                material.example, material.similar_event = 1, 1
                material.count = 1
                print(f"[INFO] 회피/반감 감지: {matched_materials[i]} - 소재 완료 처리")
            else:
                # ========== PRINCIPLE 증가 부분 (6W 축) ==========
                # principle (6W) - 각 축별로 0에서 1로 변경될 때 증가
                if isinstance(axes_data.get("principle"), list) and len(axes_data["principle"]) == 6:
                    principle_delta = [0,0,0,0,0,0]
                    for j, detected in enumerate(axes_data["principle"]):
                        if detected == 1 and material.principle[j] == 0:  # 0→1 변화만 감지
                            material.principle[j] = 1  # ★ PRINCIPLE 증가 지점
                            principle_delta[j] = 1
                else:
                    # 휴리스틱 보조 - LLM이 축 정보를 제공하지 않을 때
                    principle_delta = [0,0,0,0,0,0]
                    for j, detected in enumerate(axes_evidence.values()):
                        if detected and j < 6 and material.principle[j] == 0:  # 0→1 변화만 감지
                            material.principle[j] = 1  # ★ PRINCIPLE 증가 지점 (휴리스틱)
                            principle_delta[j] = 1

                # ========== EXAMPLE 증가 부분 ==========
                example_delta = 0
                if (axes_data.get("example") == 1 or ex_flag) and material.example == 0:  # 0→1 변화만 감지
                    material.example = 1  # ★ EXAMPLE 증가 지점
                    example_delta = 1
                    
                # ========== SIMILAR_EVENT 증가 부분 ==========
                similar_event_delta = 0
                if (axes_data.get("similar_event") == 1 or con_flag) and material.similar_event == 0:  # 0→1 변화만 감지
                    material.similar_event = 1  # ★ SIMILAR_EVENT 증가 지점
                    similar_event_delta = 1

                # material 변경사항 발행
                if any(principle_delta) or example_delta or similar_event_delta:
                    print(f"[DEBUG] Material changes detected: principle_delta={principle_delta}, example_delta={example_delta}, similar_event_delta={similar_event_delta}")
                    
                    # material 변화량 데이터 구성
                    material_deltas = [{
                        'chunk_id': chunk_num,
                        'material_id': material.order,
                        'example_delta': example_delta,
                        'similar_event_delta': similar_event_delta,
                        'count_delta': 0,
                        'principle_delta': principle_delta
                    }]
                    
                    # AI cat_num을 DB 매핑으로 변환
                    theme_id, category_order = convert_cat_num_to_db_mapping(cat_num)
                    
                    print(f"[DEBUG] publish_delta_change params: user_id={user_id}, autobiography_id={autobiography_id}, theme_id={theme_id}, category_order={category_order}")
                    
                    publish_delta_change(
                        user_id=user_id,
                        autobiography_id=autobiography_id, 
                        theme_id=theme_id,
                        category_id=category_order,  # DB의 category order 사용
                        material_deltas=material_deltas
                    )

                # ========== CHUNK WEIGHT 증가 부분 ==========
                category = engine.categories[cat_num]
                category.chunk_weight[chunk_num] = category.chunk_weight.get(chunk_num, 0) + 1  # ★ CHUNK WEIGHT 증가 지점 (+1씩 누적)
                
                # chunk weight 증가 발행
                # chunk weight 변화량 데이터 구성
                chunk_deltas = [{
                    'chunk_id': chunk_num,
                    'weight_delta': 1  # weight 증가
                }]
                
                # AI cat_num을 DB 매핑으로 변환
                theme_id, category_order = convert_cat_num_to_db_mapping(cat_num)
                
                publish_delta_change(
                    user_id=user_id,
                    autobiography_id=autobiography_id,
                    theme_id=theme_id,
                    category_id=category_order,  # DB의 category order 사용
                    chunk_deltas=chunk_deltas
                )
                
                # ========== MATERIAL COUNT 증가 부분 ==========
                material.mark_filled_if_ready()  # ★ 이 함수 내부에서 MATERIAL COUNT가 0→1로 변경됨

        same_material = (current_material in matched_materials) if current_material else False
        print(f"\n🔍 [소재 매칭] {current_material} → {matched_materials} (동일:{same_material})")
        if axes_analysis_by_material:
            print("📋 [축 분석 결과]")
            for k, v in axes_analysis_by_material.items():
                print(f"  - {k}: {v}")
    # ------------------ 다음 질문 생성 ------------------

    try:
        material_id = engine.select_material()
        cat_num, chunk_num, mat_num = material_id

        material = engine._get_material(cat_num, chunk_num, mat_num)
        if not material:
            return {"next_question": None, "last_answer_materials_id": []}

        # Material Gate 체크: 소재에 기존 데이터가 없으면 gate 질문 먼저
        category = engine.categories[cat_num]
        chunk = category.chunks[chunk_num]
        full_material_name = f"{category.category_name} {chunk.chunk_name} {material.name}"
        
        # 직전 질문이 gate가 아니고, 현재 소재가 완전히 비어있으면 gate 질문 생성
        # 단, 직전 질문이 gate였어도 다른 소재로 바뀌었으면 gate 질문 생성
        last_question_type = question.get("type") if question else None
        last_material_id = tuple(question.get("material_id")) if question and question.get("material_id") else None
        is_material_empty = (material.progress_score() == 0 and material.count == 0)
        is_different_material = (last_material_id != material_id)
        
        print(f"[DEBUG] Gate 체크: material_id={material_id}, progress_score={material.progress_score()}, count={material.count}, last_type={last_question_type}")
        print(f"[DEBUG] is_material_empty={is_material_empty}, principle={material.principle}, ex={material.example}, con={material.similar_event}")
        print(f"[DEBUG] last_material_id={last_material_id}, is_different_material={is_different_material}")
        
        if is_material_empty and (last_question_type != "material_gate" or is_different_material):
            gate_question_text = generate_material_gate_question(full_material_name)
            
            next_question = {
                "id": f"q-{uuid4().hex[:8]}",
                "material": material.name,
                "type": "material_gate",
                "text": gate_question_text,
                "material_id": material_id
            }
            
            # 메트릭 업데이트 (상태는 변경하지 않음)
            def serialize_categories(categories):
                result = []
                for cat in categories.values():
                    active_chunks = {ck: cv for ck, cv in cat.chunks.items() if cat.chunk_weight.get(ck, 0) > 0}
                    if not active_chunks:
                        continue

                    chunks = []
                    for chunk in active_chunks.values():
                        materials = [
                            {"order": m.order, "name": m.name, "principle": m.principle,
                             "example": m.example, "similar_event": m.similar_event, "count": m.count}
                            for m in chunk.materials.values()
                            if any(m.principle) or m.example or m.similar_event or m.count > 0
                        ]
                        if materials:
                            chunks.append({"chunk_num": chunk.chunk_num, "chunk_name": chunk.chunk_name, "materials": materials})

                    if chunks:
                        result.append({
                            "category_num": cat.category_num,
                            "category_name": cat.category_name,
                            "chunks": chunks,
                            "chunk_weight": {str(ck): w for ck, w in cat.chunk_weight.items() if w > 0}
                        })
                return result
            
            updated_metrics = {
                "session_id": sessionId,
                "categories": serialize_categories(engine.categories),
                "engine_state": {
                    "last_material_id": list(engine.state.last_material_id) if engine.state.last_material_id else [],
                    "last_material_streak": engine.state.last_material_streak,
                    "epsilon": engine.state.epsilon
                },
                "asked_total": metrics.get("asked_total", 0) + 1,
                "policy_version": "v0.5.0"
            }
            
            session_update = {
                "metrics": updated_metrics,
                "last_question": next_question,
                "updated_at": time.time()
            }
            redis_client.set(session_key, json.dumps(session_update))
            print(f"[DEBUG] Session saved (gate): {session_key}, has last_question: {bool(session_update.get('last_question'))}")
            
            print(f"\n🚧 [Material Gate] {full_material_name} - 진입 확인 질문 생성")
            if last_question_type == "material_gate" and is_different_material:
                print(f"   (직전도 gate였지만 소재 변경: {last_material_id} → {material_id})")
            print("=" * 50)
            
            return {"next_question": next_question, "last_answer_materials_id": mapped_ids if mapped_ids else []}

        material_id, target = engine.select_question_in_material(material_id)
        if not target:
            return {"next_question": None, "last_answer_materials_id": []}
        
        print(f"[DEBUG] select_question_in_material 후: material_id={material_id}, target={target}")
        
        # 소재가 변경되었는지 확인
        if material_id != (cat_num, chunk_num, mat_num):
            print(f"[DEBUG] 소재 변경됨: {(cat_num, chunk_num, mat_num)} -> {material_id}")
            # 변경된 소재 다시 가져오기
            cat_num, chunk_num, mat_num = material_id
            material = engine._get_material(cat_num, chunk_num, mat_num)
            category = engine.categories[cat_num]
            chunk = category.chunks[chunk_num]
            full_material_name = f"{category.category_name} {chunk.chunk_name} {material.name}"

        # 타입 매핑: 엔진 타입 → 프롬프트 타입
        type_mapping = {
            "w1": "when_where",
            "w2": "how1",
            "w3": "who",
            "w4": "what",
            "w5": "how2",
            "w6": "why",
            "ex": "ex",
            "con": "con"
        }
        prompt_type = type_mapping.get(target, target)

        # 직전 답변을 항상 컨텍스트로 활용 (소재 전환 여부 무관)
        context_answer = answer_text if not is_first_question else None

        question_text = generate_question_llm(full_material_name, prompt_type, context_answer)

        # streak 업데이트
        if engine.state.last_material_id == material_id:
            engine.state.last_material_streak += 1
        else:
            engine.state.last_material_id = material_id
            engine.state.last_material_streak = 1

        next_question = {
            "id": f"q-{uuid4().hex[:8]}",
            "material": material.name,
            "type": target,
            "text": question_text,
            "material_id": material_id
        }

        def serialize_categories(categories):
            result = []
            for cat in categories.values():
                active_chunks = {ck: cv for ck, cv in cat.chunks.items() if cat.chunk_weight.get(ck, 0) > 0}
                if not active_chunks:
                    continue

                chunks = []
                for chunk in active_chunks.values():
                    materials = [
                        {"order": m.order, "name": m.name, "principle": m.principle,
                         "example": m.example, "similar_event": m.similar_event, "count": m.count}
                        for m in chunk.materials.values()
                        if any(m.principle) or m.example or m.similar_event or m.count > 0
                    ]
                    if materials:
                        chunks.append({"chunk_num": chunk.chunk_num, "chunk_name": chunk.chunk_name, "materials": materials})

                if chunks:
                    result.append({
                        "category_num": cat.category_num,
                        "category_name": cat.category_name,
                        "chunks": chunks,
                        "chunk_weight": {str(ck): w for ck, w in cat.chunk_weight.items() if w > 0}
                    })
            return result

        # 이전 상태 저장
        previous_categories = metrics.get("categories", [])
        
        updated_metrics = {
            "session_id": sessionId,
            "categories": serialize_categories(engine.categories),
            "engine_state": {
                "last_material_id": list(engine.state.last_material_id) if engine.state.last_material_id else [],
                "last_material_streak": engine.state.last_material_streak,
                "epsilon": engine.state.epsilon
            },
            "asked_total": metrics.get("asked_total", 0) + 1,
            "policy_version": "v0.5.0"
        }
        
        # Delta 계산 및 발행
        try:
            from datetime import datetime, timezone
            serve_dir = os.path.join(current_dir, '..', '..', '..', '..', 'serve')
            sys.path.insert(0, serve_dir)
            from stream import publish_categories_message
            from stream.dto import ChunksPayload, MaterialsPayload, CategoriesPayload
            
            now = datetime.now(timezone.utc)
            prev_cats = {c["category_num"]: c for c in previous_categories}
            
            logger.info(f"[DELTA_CHECK] previous_categories count: {len(previous_categories)}, updated_categories count: {len(updated_metrics['categories'])}")
            
            for curr_cat in updated_metrics["categories"]:
                cat_num = curr_cat["category_num"]
                prev_cat = prev_cats.get(cat_num, {})
                prev_chunks = {c["chunk_num"]: c for c in prev_cat.get("chunks", [])}
                chunks_deltas = []
                materials_deltas = []
                
                for curr_chunk in curr_cat["chunks"]:
                    chunk_num = curr_chunk["chunk_num"]
                    prev_chunk = prev_chunks.get(chunk_num, {})
                    
                    # chunk weight 변화
                    prev_weight = prev_chunk.get("chunk_weight", {}).get(str(chunk_num), 0) if prev_chunk else 0
                    curr_weight = curr_cat["chunk_weight"].get(str(chunk_num), 0)
                    
                    if curr_weight > prev_weight:
                        chunks_deltas.append(ChunksPayload(
                            categoryId=cat_num, chunkOrder=chunk_num,
                            weight=curr_weight - prev_weight, timestamp=now
                        ))
                    
                    # material 변화
                    prev_materials = {m["order"]: m for m in prev_chunk.get("materials", [])}
                    for curr_mat in curr_chunk["materials"]:
                        mat_order = curr_mat["order"]
                        prev_mat = prev_materials.get(mat_order, {})
                        
                        principle_delta = [curr_mat["principle"][i] - prev_mat.get("principle", [0,0,0,0,0,0])[i] for i in range(6)]
                        example_delta = curr_mat["example"] - prev_mat.get("example", 0)
                        similar_event_delta = curr_mat["similar_event"] - prev_mat.get("similar_event", 0)
                        count_delta = curr_mat["count"] - prev_mat.get("count", 0)
                        
                        if any(principle_delta) or example_delta or similar_event_delta or count_delta:
                            materials_deltas.append(MaterialsPayload(
                                chunkId=chunk_num, materialOrder=mat_order,
                                example=example_delta, similarEvent=similar_event_delta,
                                count=count_delta, principle=principle_delta, timestamp=now
                            ))
                
                logger.info(f"[DELTA_CHECK] cat_num={cat_num}, chunks_deltas={len(chunks_deltas)}, materials_deltas={len(materials_deltas)}")
                
                if chunks_deltas or materials_deltas:
                    # AI cat_num을 DB 매핑으로 변환
                    theme_id, category_order = convert_cat_num_to_db_mapping(cat_num)
                    
                    final_payload = CategoriesPayload(
                        autobiographyId=int(autobiography_id),  # str() 제거하고 int() 사용
                        userId=int(user_id),  # str() 제거하고 int() 사용
                        themeId=theme_id,  # 올바른 theme_id 사용
                        categoryId=category_order,  # DB의 category_order 사용
                        chunks=chunks_deltas, materials=materials_deltas
                    )
                    
                    logger.info(f"[AI_SEND_FINAL] CategoriesPayload: autobiographyId={final_payload.autobiographyId}, userId={final_payload.userId}, themeId={final_payload.themeId}, categoryId={final_payload.categoryId}, chunks={len(final_payload.chunks)}, materials={len(final_payload.materials)}")
                    
                    publish_categories_message(final_payload)
        except Exception as e:
            print(f"[WARN] Delta 발행 실패: {e}")

        session_update = {
            "metrics": updated_metrics,
            "last_question": next_question,
            "updated_at": time.time()
        }
        redis_client.set(session_key, json.dumps(session_update))
        print(f"[DEBUG] Session saved (normal): {session_key}, has last_question: {bool(session_update.get('last_question'))}")

        print(f"\n🎯 [질문 생성] {category.category_name}-{chunk.chunk_name}-{material.name} ({target})")
        print(f"[DEBUG] 선택된 소재 ID: {material_id}, chunk_weight: {category.chunk_weight.get(chunk_num, 0)}, progress_score: {material.progress_score()}")
        print("=" * 50)

        last_answer_materials_id = mapped_ids if mapped_ids else []
        return {"next_question": next_question, "last_answer_materials_id": last_answer_materials_id}

    except Exception as e:
        import traceback
        print(f"[ERROR] 질문 생성 실패: {e}")
        print(f"[ERROR] Traceback: {traceback.format_exc()}")
        return {"next_question": None, "last_answer_materials_id": []}
