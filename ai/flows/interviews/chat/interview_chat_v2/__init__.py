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

# ------------------------ 캐시 ------------------------
_material_data_cache = None
_material_mapping_cache = None

# ------------------------ 간단 헬퍼 ------------------------

def _norm(s: str) -> str:
    """공백 제거 + trim 후 비교용 문자열로 정규화"""
    return re.sub(r"\s+", "", (s or "").strip())


def _load_material_data() -> dict:
    """material.json 로드 (캐시)"""
    global _material_data_cache
    if _material_data_cache is None:
        material_json_path = os.path.join(os.path.dirname(__file__), "data", "material.json")
        with open(material_json_path, 'r', encoding='utf-8') as f:
            _material_data_cache = json.load(f)
    return _material_data_cache

def _build_materials_list_from_mapping(mapping_path: str) -> dict:
    """material_id_mapping.json에서 {name: [cat, chunk, mat]} dict 반환 (캐시)"""
    global _material_mapping_cache
    if _material_mapping_cache is not None:
        return _material_mapping_cache
    
    try:
        with open(mapping_path, 'r', encoding='utf-8') as f:
            _material_mapping_cache = json.load(f)
            return _material_mapping_cache
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


@tool
def interview_engine(sessionId: str, answer_text: str) -> Dict:
    """인터뷰 엔진 - Redis에서 세션 로드하여 다음 질문 생성"""

    # Redis에서 세션 로드
    import redis
    redis_client = redis.Redis(host='localhost', port=6379, db=0, decode_responses=True)
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

        material_data = _load_material_data()
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
            
            gate_question_text = generate_material_gate_question(full_material_name, previous_answer=None)
            
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
                    # chunk_weight > 0인 것만 저장
                    chunk_weights = {str(ck): w for ck, w in cat.chunk_weight.items() if w > 0}
                    
                    # chunk_weight가 있는 카테고리는 무조건 저장
                    if chunk_weights:
                        result.append({
                            "category_num": cat.category_num,
                            "category_name": cat.category_name,
                            "chunks": [],  # 빈 배열로 저장 (소재 데이터는 나중에 추가)
                            "chunk_weight": chunk_weights
                        })
                        continue
                    
                    # chunk_weight가 없으면 소재 데이터가 있는 경우만 저장
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
                            "chunk_weight": chunk_weights
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
    
    # material.json 로드 및 엔진 초기화 (캐시)
    try:
        material_data = _load_material_data()
        categories = InterviewEngine.build_categories_from_category_json(material_data)

        engine = InterviewEngine(categories)
        
        # 테마 부스팅 먼저 적용 (상태 복원 전)
        preferred_categories = metrics.get("preferred_categories", [])
        if preferred_categories:
            engine.boost_theme(preferred_categories, initial_weight=10)
            print(f"[DEBUG] 테마 부스팅 적용: {preferred_categories}")
        
        # 이전 메트릭이 있으면 상태 복원 (chunk_weight는 유지)
        if metrics.get("categories"):
            restore_categories_state(categories, metrics["categories"])

        # 상태 복원
        engine_state = metrics.get("engine_state", {})
        engine.state.last_material_id = engine_state.get("last_material_id")
        engine.state.last_material_streak = engine_state.get("last_material_streak", 0)
        engine.theme_initialized = True  # 테마 부스팅 완료 표시

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
        materials_list_full = _build_materials_list_from_mapping(mapping_path)
        
        # 답변 기반 유사도 검색으로 관련 소재 필터링 (top 3)
        from engine.retrieval import search_related_materials
        start_time = time.time()
        materials_list = search_related_materials(
            answer_text, 
            materials_list_full, 
            list(current_material_id) if current_material_id else [],
            top_k=3
        )
        retrieval_time = time.time() - start_time
        print(f"[DEBUG] 유사도 검색: {len(materials_list_full)}개 → {len(materials_list)}개 필터링 ({retrieval_time:.2f}s)")

        llm_start = time.time()
        llm_items = _call_llm_map_flow(map_flow_path, answer_text, materials_list, current_material_full, list(current_material_id) if current_material_id else [])
        llm_time = time.time() - llm_start
        print(f"[DEBUG] LLM 분석 시간: {llm_time:.2f}s")

        # 소재 매칭 (모든 축이 0인 소재는 제외)
        print(f"[DEBUG] LLM 분석 결과: {len(llm_items)}개 소재 (입력: {len(materials_list)}개)")
        for item in llm_items:
            if not isinstance(item, dict) or not item.get("material"):
                continue
            
            # 모든 축이 0이면 관련 없는 소재로 판단하여 스킵
            axes_data = item.get("axes", {})
            principle = axes_data.get("principle", [])
            example = axes_data.get("example", 0)
            similar_event = axes_data.get("similar_event", 0)
            is_pass = axes_data.get("pass", 0)
            
            # principle 모두 0 AND example=0 AND similar_event=0 AND pass=0 → 관련 없음
            if (isinstance(principle, list) and all(v == 0 for v in principle) and 
                example == 0 and similar_event == 0 and is_pass == 0):
                print(f"[DEBUG] 모든 축이 0인 소재 제외: {item.get('material')}")
                continue
            
            material_id = item["material"]
            
            if not isinstance(material_id, list) or len(material_id) != 3:
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
    print(f"\n📊 [매칭 완료] 총 {len(matched_materials)}개 소재 업데이트: {matched_materials}")
    
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
            # 같은 카테고리면 이전 답변 전달
            context_answer = None
            if not is_first_question and last_material_id:
                last_cat_num = last_material_id[0] if isinstance(last_material_id, (list, tuple)) and len(last_material_id) >= 1 else None
                current_cat_num = material_id[0]
                if last_cat_num == current_cat_num:
                    context_answer = answer_text
                    print(f"[DEBUG] Gate 질문 - 같은 카테고리({current_cat_num}) 이전 답변 전달")
            
            gate_question_text = generate_material_gate_question(full_material_name, previous_answer=context_answer)
            
            # streak 업데이트 (Gate 질문도 소재 선택으로 간주)
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
                    "epsilon": engine.state.epsilon,
                    "theme_initialized": engine.theme_initialized
                },
                "asked_total": metrics.get("asked_total", 0) + 1,
                "preferred_categories": metrics.get("preferred_categories", []),
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
        
        # 소재가 변경되었는지 확인 (tuple 변환 후 비교)
        original_id = (cat_num, chunk_num, mat_num)
        new_id = tuple(material_id) if isinstance(material_id, list) else material_id
        
        if new_id != original_id:
            print(f"[DEBUG] 소재 변경됨: {original_id} -> {new_id}")
            # 변경된 소재 다시 가져오기
            cat_num, chunk_num, mat_num = new_id
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
                "epsilon": engine.state.epsilon,
                "theme_initialized": engine.theme_initialized
            },
            "asked_total": metrics.get("asked_total", 0) + 1,
            "preferred_categories": metrics.get("preferred_categories", []),
            "policy_version": "v0.5.0"
        }

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
