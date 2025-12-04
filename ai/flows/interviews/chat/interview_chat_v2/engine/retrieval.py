"""소재 검색 모듈 - 답변 기반 유사도 검색"""
from typing import List, Dict, Tuple
import re
import numpy as np

try:
    from sentence_transformers import SentenceTransformer  # type: ignore
    import os
    _model = None
    _material_embeddings = None  # 사전 계산된 임베딩
    _material_names_list = None  # 소재명 리스트
    
    def get_embedding_model():
        global _model
        if _model is None:
            _model = SentenceTransformer('jhgan/ko-sroberta-multitask')
        return _model
    
    def load_precomputed_embeddings():
        """사전 계산된 임베딩 로드"""
        global _material_embeddings, _material_names_list
        if _material_embeddings is not None:
            return _material_embeddings, _material_names_list
        
        current_dir = os.path.dirname(__file__)
        embeddings_path = os.path.join(current_dir, "..", "data", "material_embeddings.npz")
        
        if os.path.exists(embeddings_path):
            data = np.load(embeddings_path)
            _material_embeddings = data['embeddings']
            _material_names_list = data['material_names'].tolist()
            print(f"[INFO] 사전 계산된 임베딩 로드: {len(_material_names_list)}개")
            return _material_embeddings, _material_names_list
        else:
            print(f"[WARN] 임베딩 파일 없음: {embeddings_path}")
            return None, None
    
    USE_EMBEDDINGS: bool = True
except ImportError:
    USE_EMBEDDINGS: bool = False

def extract_keywords(text: str) -> List[str]:
    """답변에서 키워드 추출"""
    # 불용어 제거
    stopwords = {'은', '는', '이', '가', '을', '를', '의', '에', '에서', '와', '과', '도', '만', '까지', '부터', '로', '으로'}
    
    # 공백/특수문자 기준 분리
    words = re.findall(r'[가-힣]+', text)
    
    # 2글자 이상, 불용어 제외
    keywords = [w for w in words if len(w) >= 2 and w not in stopwords]
    
    return list(set(keywords))  # 중복 제거

def calculate_similarity(keywords: List[str], material_name: str, answer_text: str = "") -> float:
    """키워드와 소재명 유사도 계산"""
    
    # 임베딩 모델 사용 (의미 기반)
    if USE_EMBEDDINGS and answer_text:
        try:
            model = get_embedding_model()
            answer_emb = model.encode(answer_text, convert_to_numpy=True)
            material_emb = model.encode(material_name, convert_to_numpy=True)
            
            # 코사인 유사도
            similarity = np.dot(answer_emb, material_emb) / (np.linalg.norm(answer_emb) * np.linalg.norm(material_emb))
            return float(similarity)
        except Exception as e:
            print(f"[WARN] 임베딩 실패, 문자열 매칭으로 폴백: {e}")
    
    # 폴백: 단순 문자열 매칭
    material_words = set(re.findall(r'[가-힣]+', material_name))
    
    if not keywords or not material_words:
        return 0.0
    
    # 교집합 개수 / 키워드 개수
    matches = sum(1 for kw in keywords if any(kw in mw or mw in kw for mw in material_words))
    return matches / len(keywords)

def search_related_materials(
    answer_text: str,
    materials_list: Dict[str, List[int]],
    current_material_id: List[int],
    top_k: int = 10
) -> Dict[str, List[int]]:
    """답변 기반 관련 소재 검색 (유사도 상위 K개)
    
    현재 소재 + 유사도 상위 (K-1)개 반환
    """
    
    # 현재 소재 찾기
    current_name = next((name for name, id in materials_list.items() if id == current_material_id), None)
    
    keywords = extract_keywords(answer_text)
    
    # 답변이 너무 짧거나 키워드 없으면 현재 소재만 반환
    if not keywords or len(answer_text.strip()) < 10:
        return {current_name: current_material_id} if current_name else {}
    
    # 임베딩 모델 사용 시 배치 처리
    scored_materials = []
    use_fallback = False
    
    if USE_EMBEDDINGS:
        try:
            model = get_embedding_model()
            
            # 답변 임베딩
            answer_emb = model.encode(answer_text, convert_to_numpy=True)
            
            # 사전 계산된 임베딩 로드
            material_embs, precomputed_names = load_precomputed_embeddings()
            
            if material_embs is not None:
                # 사전 계산된 임베딩 사용
                similarities = np.dot(material_embs, answer_emb) / (np.linalg.norm(material_embs, axis=1) * np.linalg.norm(answer_emb))
                
                # top_k*2 개만 고려
                top_indices = np.argsort(similarities)[::-1][:top_k*2]
                scored_materials = [(precomputed_names[i], materials_list.get(precomputed_names[i]), float(similarities[i])) 
                                    for i in top_indices 
                                    if similarities[i] > 0.3 and precomputed_names[i] in materials_list]
            else:
                # 폴백: 실시간 임베딩
                print(f"[WARN] 실시간 임베딩 사용 ({len(materials_list)}개)")
                material_names = list(materials_list.keys())
                material_embs = model.encode(material_names, convert_to_numpy=True, batch_size=64, show_progress_bar=False)
                similarities = np.dot(material_embs, answer_emb) / (np.linalg.norm(material_embs, axis=1) * np.linalg.norm(answer_emb))
                top_indices = np.argsort(similarities)[::-1][:top_k*2]
                scored_materials = [(material_names[i], materials_list[material_names[i]], float(similarities[i])) 
                                    for i in top_indices if similarities[i] > 0.3]
        except Exception as e:
            print(f"[WARN] 임베딩 배치 처리 실패, 문자열 매칭으로 폴백: {e}")
            use_fallback = True
    else:
        use_fallback = True
    
    # 폴백: 문자열 매칭
    if use_fallback:
        for name, mat_id in materials_list.items():
            score = calculate_similarity(keywords, name, "")
            if score > 0:
                scored_materials.append((name, mat_id, score))
    
    # 현재 소재 제외 (중복 방지)
    scored_materials = [(n, i, s) for n, i, s in scored_materials if i != current_material_id]
    
    # 유사도 내림차순 정렬
    scored_materials.sort(key=lambda x: x[2], reverse=True)
    
    # 상위 (K-1)개 선택
    top_materials = scored_materials[:top_k-1]
    
    # 현재 소재를 맨 마지막에 추가 (LLM이 첫 번째에만 집중하는 문제 방지)
    result = {name: mat_id for name, mat_id, _ in top_materials}
    if current_name:
        result[current_name] = current_material_id
    
    return result
