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
import redis

# 실제 함수 구현
def publish_delta_change(user_id, autobiography_id, theme_id, category_id, chunk_deltas=None, material_deltas=None):
    """실제 변화량을 CategoriesPayload로 전송"""
    try:
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
        
        publish_categories_message(payload)
        print(f"[DEBUG] Published delta change: category={category_id}, {len(chunks)} chunks, {len(materials)} materials")
        
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


# AI cat_num을 DB의 theme_id, category_order로 변환하는 함수
def convert_cat_num_to_db_mapping(cat_num):
    """AI의 cat_num(0-based)을 DB의 (theme_id, category_order)로 변환"""
    # material.json의 category 순서와 DB의 theme-category 매핑
    # AI material.json: 0=부모, 1=조부모, 2=형제, 3=자녀/육아, 4=친척, 5=가족사건, 6=연인, 7=결혼, 8=배우자, 9=자녀/육아, 10=친구, 11=직장, 12=진로, 13=문제해결, 14=생애주기, 15=성격, 16=취미, 17=반려동물, 18=철학, 19=주거지, 20=생활, 21=금전
    
    # DB 매핑 (theme_id, category_order)
    mapping = [
        (1, 1),  # 0: 부모
        (1, 2),  # 1: 조부모  
        (1, 3),  # 2: 형제
        (1, 4),  # 3: 자녀/육아
        (1, 5),  # 4: 친척
        (1, 6),  # 5: 가족 사건
        (2, 1),  # 6: 연인
        (2, 2),  # 7: 결혼
        (2, 3),  # 8: 배우자
        (3, 1),  # 9: 자녀/육아 (다른 theme)
        (6, 1),  # 10: 친구
        (7, 1),  # 11: 직장
        (7, 2),  # 12: 진로
        (7, 3),  # 13: 문제해결(과정)
        (8, 1),  # 14: 생애주기
        (5, 1),  # 15: 성격
        (11, 1), # 16: 취미
        (12, 1), # 17: 반려동물
        (9, 2),  # 18: 철학
        (4, 1),  # 19: 주거지
        (3, 3),  # 20: 생활
        (10, 1), # 21: 금전
    ]
    
    if 0 <= cat_num < len(mapping):
        return mapping[cat_num]
    else:
        return (1, 1)  # 기본값

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
        # 상대 경로로 flows 디렉토리 찾기
        flows_dir = os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(current_dir))))
        map_flow_path = os.path.join(flows_dir, "flows", "interviews", "standard", "map_answer_to_materials", "flow.dag.yaml")
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
                old_weight = category.chunk_weight.get(chunk_num, 0)
                category.chunk_weight[chunk_num] = old_weight + 1  # ★ CHUNK WEIGHT 증가 지점 (+1씩 누적)
                
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

        # Redis에 업데이트된 상태 저장 (배열 구조 직렬화)
        def serialize_categories(categories):
            result = []
            for k, v in categories.items():
                # 활성 chunk만 포함 (chunk_weight > 0)
                active_chunks = {ck: cv for ck, cv in v.chunks.items() if v.chunk_weight.get(ck, 0) > 0}
                if not active_chunks:
                    continue

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
                    if materials:
                        chunks.append({
                            "chunk_num": cv.chunk_num,
                            "chunk_name": cv.chunk_name,
                            "materials": materials
                        })

                if chunks:
                    active_weights = {str(ck): w for ck, w in v.chunk_weight.items() if w > 0}
                    result.append({
                        "category_num": v.category_num,
                        "category_name": v.category_name,
                        "chunks": chunks,
                        "chunk_weight": active_weights
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
            "policy_version": "v2.0.0"
        }
        
        # Delta 계산 및 발행
        try:
            from datetime import datetime, timezone
            serve_dir = os.path.join(current_dir, '..', '..', '..', '..', 'serve')
            sys.path.insert(0, serve_dir)
            from stream import publish_persistence_message
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
                    publish_persistence_message(CategoriesPayload(
                        autobiographyId=str(session_data.get("metrics", {}).get("autobiography_id")),
                        userId=str(session_data.get("metrics", {}).get("user_id")),
                        categoryId=cat_num, chunks=chunks_deltas, materials=materials_deltas
                    ))
        except Exception as e:
            print(f"[WARN] Delta 발행 실패: {e}")

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
