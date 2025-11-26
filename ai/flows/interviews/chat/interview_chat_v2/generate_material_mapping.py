"""
material.json에서 material_id_mapping.json 생성
구조: "카테고리 청크 소재": [category_order, chunk_order, material_order]
"""
import json
import os

def generate_material_mapping():
    # material.json 로드
    current_dir = os.path.dirname(__file__)
    material_json_path = os.path.join(current_dir, "data", "material.json")
    
    with open(material_json_path, 'r', encoding='utf-8') as f:
        material_data = json.load(f)
    
    mapping = {}
    
    # 카테고리 순회
    for category in material_data.get("category", []):
        cat_order = category.get("order")
        cat_name = category.get("name")
        
        # 청크 순회
        for chunk in category.get("chunk", []):
            chunk_order = chunk.get("order")
            chunk_name = chunk.get("name")
            
            # 소재 순회
            for material in chunk.get("material", []):
                mat_order = material.get("order")
                mat_name = material.get("name")
                
                # 키: "카테고리 청크 소재"
                key = f"{cat_name} {chunk_name} {mat_name}"
                # 값: [category_order, chunk_order, material_order]
                mapping[key] = [cat_order, chunk_order, mat_order]
    
    # 저장
    output_path = os.path.join(current_dir, "data", "material_id_mapping.json")
    with open(output_path, 'w', encoding='utf-8') as f:
        json.dump(mapping, f, ensure_ascii=False, indent=2)
    
    print(f"✅ material_id_mapping.json 생성 완료")
    print(f"   총 {len(mapping)}개 소재 매핑")
    print(f"   저장 위치: {output_path}")
    
    # 샘플 출력
    print("\n📝 샘플 (처음 3개):")
    for i, (key, value) in enumerate(list(mapping.items())[:3]):
        print(f"   \"{key}\": {value}")

if __name__ == "__main__":
    generate_material_mapping()
