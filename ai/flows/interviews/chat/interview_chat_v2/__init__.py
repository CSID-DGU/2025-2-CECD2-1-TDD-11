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

# 실제 함수 구현
def publish_delta_change(user_id, autobiography_id, theme_id, category_id, chunk_deltas=None, material_deltas=None):
    """실제 변화량을 CategoriesPayload로 전송"""
    try:
        # print(f"[DEBUG] publish_delta_change called with theme_id={theme_id}, category_id={category_id}")
        
        # serve 디렉토리 경로 추가
        serve_dir = os.path.join(current_dir, '..', '..', '..', '..', 'serve')
        sys.path.insert(0, serve_dir)
        from stream import publish_categories_message
        from stream.dto import ChunksPayload, MaterialsPayload, CategoriesPayload
        
        # None 값 체크
        if user_id is None or autobiography_id is None:
            print("[DEBUG] Skipping publish_delta_change due to None values")
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


def _build_materials_list_from_mapping(mapping_path: str) -> dict:
    """material_id_mapping.json에서 {name: [cat, chunk, mat]} dict 반환"""
    try:
        with open(mapping_path, 'r', encoding='utf-8') as f:
            return json.load(f)
    except Exception as e:
        print(f"[ERROR] material_id_mapping.json 로드 실패: {e}")
        return {}


def _call_llm_map_flow(flow_path: str, answer_text: str, materials_list: dict, current_material: str, current_material_id: List[int]) -> List[dict]:
    """LLM 플로우 호출"""
    if not os.path.exists(flow_path):
        return []

    try:
        from promptflow import load_flow
        flow = load_flow(flow_path)
        res = flow(answer_text=answer_text, materials_list=materials_list, current_material=current_material, current_material_id=current_material_id)
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
        return []
    
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
    session_data = json.loads(session_data_raw) if session_data_raw and isinstance(session_data_raw, str) else None
    print(f"[DEBUG] Session loaded: session_data={session_data is not None}")
    if session_data:
        print(f"[DEBUG] last_question exists: {session_data.get('last_question') is not None}")
        if session_data.get('last_question'):
            print(f"[DEBUG] last_question: {session_data.get('last_question')}")

    # 첫 질문 분기
    if not session_data or not session_data.get("last_question"):
        preferred_categories = session_data.get("metrics", {}).get("preferred_categories", []) if session_data else []

        material_json_path = os.path.join(os.path.dirname(__file__), "data", "material.json")
        with open(material_json_path, 'r', encoding='utf-8') as f:
            material_data = json.load(f)

        categories = InterviewEngine.build_categories_from_category_json(material_data)
        engine = InterviewEngine(categories)
        
        # 테마 부스팅 적용
        if preferred_categories:
            engine.boost_theme(preferred_categories, initial_weight=10)
            print(f"[DEBUG] 테마 부스팅 적용: {preferred_categories}")
            
            # preferred_categories가 있으면 material gate 질문 생성
            material_id = engine.select_material()
            cat_num, chunk_num, mat_num = material_id
            material = engine._get_material(cat_num, chunk_num, mat_num)
            category = engine.categories[cat_num]
            chunk = category.chunks[chunk_num]
            full_material_name = f"{category.category_name} {chunk.chunk_name} {material.name}"
            
            gate_question_text = generate_material_gate_question(full_material_name)
            
            next_question = {
                "id": f"q-{uuid4().hex[:8]}",
                "material": {
                    "full_material_name": full_material_name,
                    "full_material_id": list(material_id),
                    "material_name": material.name,
                    "material_order": material.order
                },
                "type": "material_gate",
                "text": gate_question_text
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
            
            updated_metrics = {
                "session_id": sessionId,
                "categories": serialize_categories(engine.categories),
                "engine_state": {
                    "last_material_id": list(engine.state.last_material_id) if engine.state.last_material_id else [],
                    "last_material_streak": engine.state.last_material_streak,
                    "epsilon": engine.state.epsilon
                },
                "asked_total": 1,
                "preferred_categories": preferred_categories,
                "policy_version": "v0.5.0"
            }
            
            session_update = {
                "metrics": updated_metrics,
                "last_question": next_question,
                "updated_at": time.time()
            }
            redis_client.setex(session_key, 3600, json.dumps(session_update))
            
            print(f"\n🚧 [첫 질문 - Material Gate] {full_material_name}")
            return {"next_question": next_question, "last_answer_materials_id": []}
        else:
            # preferred_categories가 없으면 자유 질문
            metrics = {"preferred_categories": preferred_categories}
            result = generate_first_question(engine, metrics)
            if result.get("next_question") and "material_id" in result["next_question"]:
                material_id = result["next_question"].pop("material_id")
                result["next_question"]["material"]["full_material_id"] = material_id
            result["last_answer_materials_id"] = []
            return result

    # 이후 질문 생성 준비
    question = session_data.get("last_question", {})
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
        engine.theme_initialized = engine_state.get("theme_initialized", False)

    except Exception as e:
        print(f"[ERROR] 엔진 초기화 실패: {e}")
        return {"next_question": None, "last_answer_materials_id": []}

    # 답변 분석
    current_material = question.get("material", "") if question else ""
    # material_id는 material.full_material_id 또는 최상위 material_id에서 가져오기 (하위 호환)
    current_material_id = None
    if isinstance(current_material, dict):
        current_material_id = current_material.get("full_material_id")
    if not current_material_id:
        current_material_id = question.get("material_id") if question else None
    is_first_question = not answer_text or not current_material
    
    # 현재 질문 소재의 full_material_name 찾기 (LLM에 전달용)
    current_material_full = ""
    if isinstance(current_material, dict):
        current_material_full = current_material.get("full_material_name", "")
    elif isinstance(current_material, str):
        current_material_full = current_material
    
    # full_material_name이 없으면 material_id로 역검색
    if not current_material_full and current_material_id and isinstance(current_material_id, list) and len(current_material_id) == 3:
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

        # 상대경로로 map flow 찾기
        map_flow_path = os.path.normpath(os.path.join(current_dir, "..", "..", "standard", "map_answer_to_materials", "flow.dag.yaml"))
        mapping_path = os.path.join(os.path.dirname(__file__), "data", "material_id_mapping.json")
        materials_list = _build_materials_list_from_mapping(mapping_path)

        llm_items = _call_llm_map_flow(map_flow_path, answer_text, materials_list, current_material_full, list(current_material_id) if current_material_id else [])

        # 소재 매칭
        print(f"[DEBUG] LLM items count: {len(llm_items)}")
        for item in llm_items:
            if not isinstance(item, dict) or not item.get("material"):
                print(f"[DEBUG] Skipping invalid item: {item}")
                continue
            
            material_id = item["material"]
            print(f"[DEBUG] material_id type: {type(material_id)}, value: {material_id}")
            
            # material_id는 [cat, chunk, mat] 형태여야 함
            if not isinstance(material_id, list) or len(material_id) != 3:
                print(f"[DEBUG] Invalid material_id format: {material_id}")
                continue
            
            # 소재 이름 찾기
            cat_num, chunk_num, mat_num = material_id
            temp_cat = engine.categories.get(cat_num)
            if not temp_cat:
                print(f"[DEBUG] Category {cat_num} not found")
                continue
            
            temp_chunk = temp_cat.chunks.get(chunk_num)
            if not temp_chunk:
                print(f"[DEBUG] Chunk {chunk_num} not found in category {cat_num}")
                continue
            
            temp_mat = temp_chunk.materials.get(mat_num)
            if not temp_mat:
                print(f"[DEBUG] Material {mat_num} not found in chunk {chunk_num}")
                continue
            
            material_name = f"{temp_cat.category_name} {temp_chunk.chunk_name} {temp_mat.name}"
            matched_materials.append(material_name)
            axes_analysis_by_material[material_name] = item.get("axes", {})
            mapped_ids.append(material_id)
            print(f"[DEBUG] Mapped to ID: {material_id}, name: {material_name}")

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
                # 정상 응답 - principle (6W)
                principle = axes_data.get("principle", [])
                if isinstance(principle, list) and len(principle) == 6:
                    for j, val in enumerate(principle):
                        if val == 1: material.principle[j] = 1
                else:
                    # 휴리스틱 보조
                    for j, val in enumerate(axes_evidence.values()):
                        if val and j < 6: material.principle[j] = 1

                # example / similar_event
                if axes_data.get("example") == 1 or ex_flag: material.example = 1
                if axes_data.get("similar_event") == 1 or con_flag: material.similar_event = 1

            # 카테고리 가중치 갱신
            category = engine.categories[cat_num]
            category.chunk_weight[chunk_num] = category.chunk_weight.get(chunk_num, 0) + 1
            material.mark_filled_if_ready()

        print(f"\n🔍 [소재 매칭] {current_material} → {matched_materials}")
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
        # material_id는 material.full_material_id 또는 최상위 material_id에서 가져오기
        last_material_id = None
        if isinstance(current_material, dict):
            last_material_id = tuple(current_material.get("full_material_id", [])) if current_material.get("full_material_id") else None
        if not last_material_id:
            last_material_id = tuple(question.get("material_id")) if question and question.get("material_id") else None
        is_material_empty = (material.progress_score() == 0 and material.count == 0)
        is_different_material = (last_material_id != material_id)
        
        print(f"[DEBUG] Gate 체크: material_id={material_id}, progress_score={material.progress_score()}, count={material.count}, last_type={last_question_type}")
        print(f"[DEBUG] is_material_empty={is_material_empty}, principle={material.principle}, ex={material.example}, con={material.similar_event}")
        print(f"[DEBUG] last_material_id={last_material_id}, is_different_material={is_different_material}")
        
        if is_material_empty and (last_question_type != "material_gate" or is_different_material):
            gate_question_text = generate_material_gate_question(full_material_name)
            
            # material.name 직접 사용
            material_name = material.name
            
            next_question = {
                "id": f"q-{uuid4().hex[:8]}",
                "material": {
                    "full_material_name": full_material_name,
                    "full_material_id": list(material_id),
                    "material_name": material_name,
                    "material_order": material.order
                },
                "type": "material_gate",
                "text": gate_question_text
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
            redis_client.setex(session_key, 3600, json.dumps(session_update))
            
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

        # 카테고리가 같으면 이전 답변을 컨텍스트로 전달
        context_answer = None
        if not is_first_question and last_material_id:
            last_cat_num = last_material_id[0] if isinstance(last_material_id, (list, tuple)) and len(last_material_id) >= 1 else None
            current_cat_num = material_id[0]
            if last_cat_num == current_cat_num:
                context_answer = answer_text
                print(f"[DEBUG] 같은 카테고리({current_cat_num}) - 이전 답변 전달")
            else:
                print(f"[DEBUG] 다른 카테고리({last_cat_num} → {current_cat_num}) - 이전 답변 미전달")

        question_text = generate_question_llm(full_material_name, prompt_type, context_answer)

        # streak 업데이트
        if engine.state.last_material_id == material_id:
            engine.state.last_material_streak += 1
        else:
            engine.state.last_material_id = material_id
            engine.state.last_material_streak = 1

        # material.name 직접 사용
        material_name = material.name
        
        next_question = {
            "id": f"q-{uuid4().hex[:8]}",
            "material": {
                "full_material_name": full_material_name,
                "full_material_id": list(material_id),
                "material_name": material_name,
                "material_order": material.order
            },
            "type": target,
            "text": question_text
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
        
        # 이전 상태 저장
        previous_categories = metrics.get("categories", [])
        
        # Delta 계산 및 발행
        try:
            from datetime import datetime, timezone
            serve_dir = os.path.join(current_dir, '..', '..', '..', '..', 'serve')
            sys.path.insert(0, serve_dir)
            from stream import publish_categories_message
            from stream.dto import ChunksPayload, MaterialsPayload, CategoriesPayload
            
            now = datetime.now(timezone.utc)
            prev_cats = {c["category_num"]: c for c in previous_categories}
            
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
                    
                    # print(f"[AI_SEND_FINAL] CategoriesPayload: autobiographyId={final_payload.autobiographyId}, userId={final_payload.userId}, themeId={final_payload.themeId}, categoryId={final_payload.categoryId}, chunks={len(final_payload.chunks)}, materials={len(final_payload.materials)}")
                    
                    publish_categories_message(final_payload)
        except Exception as e:
            print(f"[WARN] Delta 발행 실패: {e}")

        session_update = {
            "metrics": updated_metrics,
            "last_question": next_question,
            "updated_at": time.time()
        }
        redis_client.setex(session_key, 3600, json.dumps(session_update))

        print(f"\n🎯 [질문 생성] {category.category_name}-{chunk.chunk_name}-{material.name} ({target})")
        print(f"[DEBUG] 선택된 소재 ID: {material_id}, chunk_weight: {category.chunk_weight.get(chunk_num, 0)}, progress_score: {material.progress_score()}")
        print("=" * 50)

        last_answer_materials_id = mapped_ids if mapped_ids else []
        return {"next_question": next_question, "last_answer_materials_id": last_answer_materials_id}

    except Exception as e:
        print(f"[ERROR] 질문 생성 실패: {e}")
        return {"next_question": None, "last_answer_materials_id": []}