from promptflow.core import tool
from typing import Dict, List, Tuple
import json
import re
import sys
import os
import time
from uuid import uuid4

# engine 모듈 import 경로 추가
current_dir = os.path.dirname(os.path.abspath(__file__))
parent_dir = os.path.dirname(current_dir)
sys.path.insert(0, parent_dir)

from engine.core import InterviewEngine
from engine.utils import HINTS, EX_HINTS, CON_HINTS, hit_any, restore_categories_state
from engine.generators import generate_first_question, generate_question_llm


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
                out.append(f"{c} {ch} {material}")
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
    """
    LLM 플로우 호출 → 지정된 단 하나의 포맷만 가정:
    [
      {"material":"카테고리 청크 소재명",
       "axes":{"principle":[0,1,1,0,1,0],"example":1,"similar_event":1}}
    ]
    실패/비정상 시 단일 폴백: 빈 리스트 반환
    """
    if not os.path.exists(flow_path):
        print(f"[WARN] flow 파일 없음: {flow_path}")
        return []

    try:
        from promptflow import load_flow
        flow = load_flow(flow_path)
        res = flow(
            answer_text=answer_text,
            materials_list=materials_list,
            current_material=current_material
        )
        items = res.get("analysis_result", [])
        # 혹시 문자열이라면 한 번만 json.loads 시도
        if isinstance(items, str):
            try:
                items = json.loads(items)
            except Exception:
                return []
        if not isinstance(items, list):
            return []
        return items
    except Exception as e:
        print(f"[ERROR] LLM 플로우 호출 실패: {e}")
        return []  # 폴백: 빈 결과


@tool
def interview_engine(sessionId: str, answer_text: str) -> Dict:
    """인터뷰 엔진 - Redis에서 세션 로드하여 다음 질문 생성"""

    # Redis에서 세션 로드
    import redis
    redis_client = redis.Redis(host='localhost', port=6379, db=0, decode_responses=True)
    session_key = f"session:{sessionId}"
    session_data_raw = redis_client.get(session_key)
    session_data = json.loads(session_data_raw) if session_data_raw and isinstance(session_data_raw, str) else None
    print(f"[DEBUG] Session loaded")

    # 첫 질문 분기
    if not session_data or not session_data.get("last_question"):
        preferred_categories = session_data.get("metrics", {}).get("preferred_categories", []) if session_data else []

        material_json_path = os.path.join(os.path.dirname(__file__), "data", "material.json")
        with open(material_json_path, 'r', encoding='utf-8') as f:
            material_data = json.load(f)

        categories = InterviewEngine.build_categories_from_category_json(material_data)
        engine = InterviewEngine(categories)

        metrics = {"preferred_categories": preferred_categories}
        result = generate_first_question(engine, metrics)
        result["last_answer_materials_id"] = []  # 첫 질문이므로 비움
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
    is_first_question = not answer_text or not current_material

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
        flows_dir = os.path.dirname(os.path.dirname(os.path.dirname(current_dir)))
        map_flow_path = os.path.join(flows_dir, "interviews", "standard", "map_answer_to_materials", "flow.dag.yaml")
        materials_list = _build_materials_list(material_data)

        # 매핑 파일 로드
        mapping_path = os.path.join(os.path.dirname(__file__), "data", "material_id_mapping.json")
        material_mapping, norm_index = _load_mapping(mapping_path)

        llm_items = _call_llm_map_flow(map_flow_path, answer_text, materials_list, current_material)

        # 지정 포맷 그대로 가정: [{"material": "...", "axes": {...}}, ...]
        for item in llm_items:
            if not isinstance(item, dict):
                continue
            name = item.get("material")
            if not name:
                continue

            # 1) 정확 매칭 → 2) 공백 제거 후 매칭
            key = name if name in material_mapping else norm_index.get(_norm(name))
            if not key:
                continue

            matched_materials.append(key)
            axes_analysis_by_material[key] = item.get("axes", {})

        print(f"[INFO] LLM 분석 완료: {len(matched_materials)}개 소재 매칭")

        # 소재 ID 매핑
        for material_name in matched_materials:
            mid = material_mapping.get(material_name)
            if isinstance(mid, list) and len(mid) == 3:
                mapped_ids.append(mid)

        # LLM에 축 정보가 없을 때 휴리스틱 반영
        if mapped_ids:
            for i, material_id in enumerate(mapped_ids):
                cat_num, chunk_num, mat_num = material_id
                material = engine._get_material(cat_num, chunk_num, mat_num)
                if not material:
                    continue

                axes = axes_analysis_by_material.get(matched_materials[i], {})
                axes_data = axes.get("axes", {}) if isinstance(axes, dict) else {}

                # principle (6W)
                if isinstance(axes_data.get("principle"), list) and len(axes_data["principle"]) == 6:
                    for j, detected in enumerate(axes_data["principle"]):
                        if detected == 1:
                            material.principle[j] = 1
                else:
                    # 휴리스틱 보조
                    for j, detected in enumerate(axes_evidence.values()):
                        if detected and j < 6:
                            material.principle[j] = 1

                # example / similar_event
                if axes_data.get("example") == 1 or ex_flag:
                    material.example = 1
                if axes_data.get("similar_event") == 1 or con_flag:
                    material.similar_event = 1

                # 카테고리 가중치, 상태 갱신
                category = engine.categories[cat_num]
                old_weight = category.chunk_weight.get(chunk_num, 0)
                category.chunk_weight[chunk_num] = old_weight + 1
                material.mark_filled_if_ready()

        same_material = (current_material in matched_materials) if current_material else False
        print(f"\n🔍 [소재 매칭] {current_material} → {matched_materials} (동일:{same_material})")
        if axes_analysis_by_material:
            print("📋 [축 분석 결과]")
            for k, v in axes_analysis_by_material.items():
                print(f"  - {k}: {v}")
    # ------------------ 다음 질문 생성 ------------------

<<<<<<< HEAD
        
        # 메트릭 업데이트
        if matched_materials:
            mapped_ids = []
            current_id = None
            
            print(f"\n🔍 [소재 ID 매핑] current_material: '{current_material}'")
            for material_name in matched_materials:
                # 소재명을 띄어쓰기로 분리하여 직접 매칭
                parts = material_name.split()
                if len(parts) >= 3:
                    cat_name, chunk_name, mat_name = parts[0], parts[1], ' '.join(parts[2:])
                    
                    # 카테고리 찾기
                    found_cat = None
                    for cat_num, category in engine.categories.items():
                        if category.category_name == cat_name:
                            found_cat = category
                            break
                    
                    if found_cat:
                        # 청크 찾기
                        found_chunk = None
                        for chunk_num, chunk in found_cat.chunks.items():
                            if chunk.chunk_name == chunk_name:
                                found_chunk = chunk
                                break
                        
                        if found_chunk:
                            # 소재 찾기
                            for mat_num, material in found_chunk.materials.items():
                                if material.material_name == mat_name:
                                    material_id = [cat_num, chunk_num, mat_num]
                                    mapped_ids.append(material_id)
                                    print(f"  '{material_name}' → {material_id}")
                                    break
                            else:
                                print(f"  '{material_name}' → None (소재 미발견: '{mat_name}')")
                        else:
                            print(f"  '{material_name}' → None (청크 미발견: '{chunk_name}')")
                    else:
                        print(f"  '{material_name}' → None (카테고리 미발견: '{cat_name}')")
                else:
                    print(f"  '{material_name}' → None (형식 오류: {len(parts)}개 부분)")
            
            print(f"mapped_ids: {mapped_ids} (total: {len(mapped_ids)})")
            
            if mapped_ids:
                print(f"\n✅ 메트릭 업데이트 시작!")
                print(f"\n📊 [메트릭 업데이트] {len(mapped_ids)}개 소재")
                
                for i, material_id in enumerate(mapped_ids):
                    cat_num, chunk_num, mat_num = material_id
                    material = engine._get_material(cat_num, chunk_num, mat_num)
                    if material:
                        material_name = matched_materials[i] if i < len(matched_materials) else None
                        material_axes = axes_analysis_by_material.get(material_name) if material_name and axes_analysis_by_material else None
                        print(f"  {i+1}. {material_name} → {material_id}")
                        print(f"    처리중: {material_axes}")
                        
                        old_w = material.w.copy()
                        old_ex = material.ex
                        old_con = material.con
                        
                        if material_axes and "w" in material_axes:
                            w_values = material_axes["w"]
                            if isinstance(w_values, list) and len(w_values) == 6:
                                for j, detected in enumerate(w_values):
                                    if detected == 1:
                                        material.w[j] = min(material.w[j] + 1, 6)  # ← 누적 (최대 6)
                                print(f"    6W 반영: {w_values} → {material.w}")
                        else:
                            for j, detected in enumerate(axes_evidence.values()):
                                if detected and j < 6:
                                    material.w[j] = min(material.w[j] + 1, 6)  # ← 누적 (최대 6)
                        
                        if material_axes and material_axes.get("ex") == 1:
                            material.ex = min(material.ex + 1, 3)  # ← 누적 (최대 3)
                        elif ex_flag:
                            material.ex = min(material.ex + 1, 3)  # ← 누적 (최대 3)
                        
                        if material_axes and material_axes.get("con") == 1:
                            material.con = min(material.con + 1, 3)  # ← 누적 (최대 3)
                        elif con_flag:
                            material.con = min(material.con + 1, 3)  # ← 누적 (최대 3)
                        
                        print(f"    변경: w {old_w} → {material.w}, ex {old_ex} → {material.ex}, con {old_con} → {material.con}")
                        
                        category = engine.categories[cat_num]
                        old_weight = category.chunk_weight.get(chunk_num, 0)
                        category.chunk_weight[chunk_num] = old_weight + 1
                        print(f"    chunk_weight: {old_weight} → {category.chunk_weight[chunk_num]}")
                        
                        material.mark_filled_if_ready()
                        print(f"    material_count: {material.material_count}")
            else:
                print(f"\n⚠️ [메트릭 업데이트 실패] mapped_ids가 비어있음")
                print(f"    원인: find_material_id()가 모든 소재에 대해 None 반환")
        else:
            print(f"\n⚠️ [메트릭 업데이트 실패] matched_materials가 비어있음")
                

        

    
    # 다음 질문 생성
=======
>>>>>>> ai/dev
    try:
        material_id = engine.select_material()
        cat_num, chunk_num, mat_num = material_id

        material = engine._get_material(cat_num, chunk_num, mat_num)
        if not material:
            return {"next_question": None, "last_answer_materials_id": []}

        material_id, target = engine.select_question_in_material(material_id)
        if not target:
            return {"next_question": None, "last_answer_materials_id": []}

        # 프롬프트용 타입 변환
        type_mapping = {
            "w1": "when", "w2": "how", "w3": "who",
            "w4": "what", "w5": "where", "w6": "why",
            "ex": "ex", "con": "con"
        }
        prompt_type = type_mapping.get(target, target)

        # 동일 소재면 직전 답변을 컨텍스트로 활용
        context_answer = None
        if not is_first_question:
            same_material = (current_material == material.name)
            if same_material:
                context_answer = answer_text

        category = engine.categories[cat_num]
        chunk = category.chunks[chunk_num]
        full_material_name = f"{category.category_name} {chunk.chunk_name} {material.name}"

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
<<<<<<< HEAD
        
        # Redis에 업데이트된 상태 저장 (배열 구조)
=======

        # Redis에 업데이트된 상태 저장 (배열 구조 직렬화)
>>>>>>> ai/dev
        def serialize_categories(categories):
            result = []
            for k, v in categories.items():
                # 활성 chunk만 포함 (chunk_weight > 0)
                active_chunks = {ck: cv for ck, cv in v.chunks.items() if v.chunk_weight.get(ck, 0) > 0}
                if not active_chunks:
                    continue
<<<<<<< HEAD
                    
                chunks = []
                for ck, cv in active_chunks.items():
                    # 활성 소재만 포함 (w/ex/con 중 하나라도 값이 있음)
                    materials = []
                    for mk, mv in cv.materials.items():
                        if (any(mv.w) or mv.ex or mv.con or mv.material_count > 0):
                            materials.append({
                                "material_num": mv.material_num,
                                "material_name": mv.material_name,
                                "w": mv.w,
                                "ex": mv.ex,
                                "con": mv.con,
                                "material_count": mv.material_count
                            })
                    
=======

                chunks = []
                for ck, cv in active_chunks.items():
                    # 활성 소재만 포함
                    materials = []
                    for mk, mv in cv.materials.items():
                        if any(mv.principle) or mv.example or mv.similar_event or mv.count > 0:
                            materials.append({
                                "order": mv.order,
                                "name": mv.name,
                                "principle": mv.principle,
                                "example": mv.example,
                                "similar_event": mv.similar_event,
                                "count": mv.count
                            })
>>>>>>> ai/dev
                    if materials:
                        chunks.append({
                            "chunk_num": cv.chunk_num,
                            "chunk_name": cv.chunk_name,
                            "materials": materials
                        })
<<<<<<< HEAD
                
                if chunks:
                    # 활성 chunk_weight만 포함
                    active_weights = {str(ck): weight for ck, weight in v.chunk_weight.items() if weight > 0}
=======

                if chunks:
                    active_weights = {str(ck): w for ck, w in v.chunk_weight.items() if w > 0}
>>>>>>> ai/dev
                    result.append({
                        "category_num": v.category_num,
                        "category_name": v.category_name,
                        "chunks": chunks,
                        "chunk_weight": active_weights
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

        print(f"\n🎯 [질문 생성] {category.category_name}-{chunk.chunk_name}-{material.name} ({target})")
        print("=" * 50)

        last_answer_materials_id = mapped_ids if mapped_ids else []
        return {"next_question": next_question, "last_answer_materials_id": last_answer_materials_id}

    except Exception as e:
        print(f"[ERROR] 질문 생성 실패: {e}")
        return {"next_question": None, "last_answer_materials_id": []}
