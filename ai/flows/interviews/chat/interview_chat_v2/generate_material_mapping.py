import json

def generate_material_id_mapping():
    """material.json을 기반으로 material_id 매핑을 생성"""
    
    # material.json 로드
    with open('data/material.json', 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    mapping = {}
    
    # 각 카테고리, 청크, 소재에 대해 ID 매핑 생성
    for cat_idx, category in enumerate(data['category']):
        cat_name = category['name']
        
        for chunk_idx, chunk in enumerate(category['chunk']):
            chunk_name = chunk['name']
            
            for mat_idx, material in enumerate(chunk['material']):
                # 소재명 형식: "카테고리 청크 소재명"
                full_material_name = f"{cat_name} {chunk_name} {material}"
                
                # ID는 [카테고리번호+1, 청크번호+1, 소재번호+1] (1부터 시작)
                material_id = [cat_idx + 1, chunk_idx + 1, mat_idx + 1]
                
                mapping[full_material_name] = material_id
    
    # 매핑 파일 저장
    with open('data/material_id_mapping.json', 'w', encoding='utf-8') as f:
        json.dump(mapping, f, ensure_ascii=False, indent=2)
    
    print(f"Generated {len(mapping)} material mappings")
    return mapping

if __name__ == "__main__":
    mapping = generate_material_id_mapping()
    
    # 샘플 출력
    print("\n=== Sample mappings ===")
    for i, (material, id_) in enumerate(mapping.items()):
        if i < 5:  # 처음 5개만 출력
            print(f"{material} -> {id_}")
        else:
            break