from promptflow.core import tool
from typing import Dict
import json
import re
import sys
import os
import time
from uuid import uuid4

# engine 모듈 import를 위한 경로 추가
current_dir = os.path.dirname(os.path.abspath(__file__))
parent_dir = os.path.dirname(current_dir)
sys.path.insert(0, parent_dir)

from engine.core import InterviewEngine
from engine.utils import HINTS, EX_HINTS, CON_HINTS, hit_any, find_matching_materials, find_material_id, restore_categories_state
from engine.generators import generate_first_question, generate_question_llm

@tool
def interview_engine(sessionId: str, answer_text: str) -> Dict:
    """인터뷰 엔진 - Redis에서 세션 로드하여 질문 생성"""
    
    # Redis에서 세션 로드
    import redis
    
    redis_client = redis.Redis(host='localhost', port=6379, db=0, decode_responses=True)
    session_key = f"session:{sessionId}"
    session_data_raw = redis_client.get(session_key)
    if session_data_raw and isinstance(session_data_raw, str):
        session_data = json.loads(session_data_raw)
    else:
        session_data = None
    
    print(f"[DEBUG] Session loaded")
    
    if not session_data or not session_data.get("last_question"):
        # 새 세션 또는 첫 질문 - 첫 질문 생성
        preferred_categories = session_data.get("metrics", {}).get("preferred_categories", []) if session_data else []
        
        material_json_path = os.path.join(os.path.dirname(__file__), "data", "material.json")
        with open(material_json_path, 'r', encoding='utf-8') as f:
            material_data = json.load(f)
        
        categories = InterviewEngine.build_categories_from_category_json(material_data)
        engine = InterviewEngine(categories)
        
        metrics = {"preferred_categories": preferred_categories}
        return generate_first_question(engine, metrics)
    
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
        return {"next_question": None}
    
    # 답변 분석
    current_material = question.get("material", "") if question else ""
    is_first_question = not answer_text or not current_material
    
    if not is_first_question:
        # 6W 축 감지
        axes_evidence = {k: hit_any(answer_text, HINTS[k]) for k in HINTS.keys()}
        ex_flag = 1 if hit_any(answer_text, EX_HINTS) else 0
        con_flag = 1 if hit_any(answer_text, CON_HINTS) else 0
        if not con_flag and len(answer_text or "") >= 80:
            con_flag = 1
        
        # LLM 기반 소재 매칭
        try:
            from promptflow import load_flow
            current_dir = os.path.dirname(__file__)
            flows_dir = os.path.dirname(os.path.dirname(os.path.dirname(current_dir)))
            map_flow_path = os.path.join(flows_dir, "interviews", "standard", "map_answer_to_materials", "flow.dag.yaml")
            
            if os.path.exists(map_flow_path):
                # 소재 목록 준비 (카테고리-청크-소재 형식)
                materials_list = []
                for category in material_data.get("category", []):
                    for chunk in category.get("chunk", []):
                        for material in chunk.get("material", []):
                            full_name = f"{category['name']} {chunk['name']} {material}"
                            materials_list.append(full_name)
                
                # LLM 플로우 로드 및 실행
                map_flow = load_flow(map_flow_path)
                result = map_flow(
                    answer_text=answer_text,
                    materials_list=materials_list,
                    current_material=current_material
                )
                print(f"[DEBUG] LLM 플로우 결과: {result}")
                
                analysis_result = result.get("analysis_result", [])
                print(f"[DEBUG] analysis_result 타입: {type(analysis_result)}")
                
                # JSON 파싱 처리
                if isinstance(analysis_result, str):
                    import re
                    # 1. ```json ... ``` 형태에서 JSON 추출 시도
                    json_match = re.search(r'```json\s*([\s\S]*?)\s*```', analysis_result)
                    if json_match:
                        try:
                            analysis_result = json.loads(json_match.group(1))
                            print(f"[DEBUG] JSON 코드 블록 파싱 성공: {analysis_result}")
                        except json.JSONDecodeError as e:
                            print(f"[DEBUG] JSON 코드 블록 파싱 실패: {e}")
                            analysis_result = []
                    else:
                        # 2. 직접 JSON 배열 파싱 시도
                        try:
                            analysis_result = json.loads(analysis_result)
                            print(f"[DEBUG] 직접 JSON 파싱 성공: {analysis_result}")
                        except json.JSONDecodeError as e:
                            print(f"[DEBUG] 직접 JSON 파싱 실패: {e}")
                            # 3. [ ... ] 형태 찾아서 추출
                            array_match = re.search(r'\[([\s\S]*?)\]', analysis_result)
                            if array_match:
                                try:
                                    analysis_result = json.loads('[' + array_match.group(1) + ']')
                                    print(f"[DEBUG] 배열 추출 파싱 성공: {analysis_result}")
                                except json.JSONDecodeError as e2:
                                    print(f"[DEBUG] 배열 추출 파싱 실패: {e2}")
                                    analysis_result = []
                            else:
                                print(f"[DEBUG] JSON 배열 형태를 찾을 수 없음")
                                analysis_result = []
                
                # 배열 형태의 응답에서 소재와 축 정보 추출
                matched_materials = []
                axes_analysis_by_material = {}
                
                if isinstance(analysis_result, list):
                    for item in analysis_result:
                        print(f"[DEBUG] 처리중인 아이템: {item}")
                        if isinstance(item, dict) and "material" in item:
                            material_name = item["material"]
                            matched_materials.append(material_name)
                            axes_analysis_by_material[material_name] = item.get("axes", {})
                
                print(f"[INFO] LLM 분석 완료: {len(matched_materials)}개 소재 매칭")
                for material_name, axes in axes_analysis_by_material.items():
                    print(f"  소재: {material_name}")
                    print(f"  축: {axes}")
                    # 축 데이터 검증
                    if "w" in axes and isinstance(axes["w"], list) and len(axes["w"]) == 6:
                        print(f"    6W: {axes['w']} (valid)")
                    else:
                        print(f"    6W: {axes.get('w', 'missing')} (invalid)")
            else:
                # 폴백: 기존 키워드 매칭
                matched_materials = find_matching_materials(answer_text, current_material, material_data)
                axes_analysis_by_material = {}
                print(f"[WARNING] 키워드 매칭 사용")
        except Exception as e:
            # 에러 시 폴백
            matched_materials = find_matching_materials(answer_text, current_material, material_data)
            axes_analysis_by_material = {}
            print(f"[ERROR] LLM 매칭 실패: {e}, 폴백 사용")
        same_material = current_material in matched_materials if current_material else False
        
        # 소재 매칭 분석
        print(f"\n🔍 [소재 매칭] {current_material} → {matched_materials} (동일:{same_material})")
        if axes_analysis_by_material:
            print(f"📋 [축 분석 결과]")
            for material_name, axes in axes_analysis_by_material.items():
                print(f"  {material_name}: {axes}")
        

        
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
    try:
        material_id = engine.select_material()
        cat_num, chunk_num, mat_num = material_id
        
        material = engine._get_material(cat_num, chunk_num, mat_num)
        if not material:
            return {"next_question": None}
        
        target = engine.select_question_in_material(material)
        if not target:
            return {"next_question": None}
        
        # 전체 컨텍스트로 LLM 질문 생성
        category = engine.categories[cat_num]
        chunk = category.chunks[chunk_num]
        full_material_name = f"{category.category_name} {chunk.chunk_name} {material.material_name}"
        
        # 타입 코드를 프롬프트가 이해할 수 있는 형태로 변환
        type_mapping = {
            "w1": "when", "w2": "how", "w3": "who", 
            "w4": "what", "w5": "where", "w6": "why",
            "ex": "ex", "con": "con"
        }
        prompt_type = type_mapping.get(target, target)
        
        # 동일 소재인 경우 답변 내용 활용
        context_answer = None
        if not is_first_question and same_material:
            context_answer = answer_text
        
        question_text = generate_question_llm(full_material_name, prompt_type, context_answer)
        
        # streak 업데이트
        if engine.state.last_material_id == material_id:
            engine.state.last_material_streak += 1
        else:
            engine.state.last_material_id = material_id
            engine.state.last_material_streak = 1
        

        
        next_question = {
            "id": f"q-{uuid4().hex[:8]}",
            "material": material.material_name,
            "type": target,
            "text": question_text,
            "material_id": material_id
        }
        
        # Redis에 업데이트된 상태 저장 (배열 구조)
        def serialize_categories(categories):
            result = []
            for k, v in categories.items():
                # 활성 청크만 포함 (chunk_weight > 0)
                active_chunks = {ck: cv for ck, cv in v.chunks.items() 
                               if v.chunk_weight.get(ck, 0) > 0}
                
                if not active_chunks:
                    continue
                    
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
                    
                    if materials:
                        chunks.append({
                            "chunk_num": cv.chunk_num,
                            "chunk_name": cv.chunk_name,
                            "materials": materials
                        })
                
                if chunks:
                    # 활성 chunk_weight만 포함
                    active_weights = {str(ck): weight for ck, weight in v.chunk_weight.items() if weight > 0}
                    result.append({
                        "category_num": v.category_num,
                        "category_name": v.category_name,
                        "chunks": chunks,
                        "chunk_weight": active_weights
                    })
            return result
        
        updated_metrics = {
            "sessionId": sessionId,
            "categories": serialize_categories(engine.categories),
            "engine_state": {
                "last_material_id": list(engine.state.last_material_id) if engine.state.last_material_id else [],
                "last_material_streak": engine.state.last_material_streak,
                "epsilon": engine.state.epsilon
            },
            "asked_total": metrics.get("asked_total", 0) + 1,
            "policyVersion": "v2.0.0"
        }
        
        session_update = {
            "metrics": updated_metrics,
            "last_question": next_question,
            "updated_at": time.time()
        }
        redis_client.setex(session_key, 3600, json.dumps(session_update))
        
        print(f"\n🎯 [질문 생성] {category.category_name}-{chunk.chunk_name}-{material.material_name} ({target})")
        print(f"{'='*50}")
        
        return {"next_question": next_question}
        
    except Exception as e:
        print(f"[ERROR] 질문 생성 실패: {e}")
        return {"next_question": None}