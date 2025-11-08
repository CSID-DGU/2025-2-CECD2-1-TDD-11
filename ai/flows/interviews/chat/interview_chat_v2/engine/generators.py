from typing import Dict, List, Optional
from uuid import uuid4
from pathlib import Path
from .core import InterviewEngine

#V2 추가 함수 - 첫 질문 생성
def generate_first_question(engine: InterviewEngine, metrics: Dict) -> Dict:
    """
    첫 질문 생성 - 카테고리 소개 질문
    
    기존 Legacy 알고리즘은 바로 소재별 질문을 생성했지만,
    V2에서는 첫 질문에서 카테고리 전체에 대한 소개 질문을 먼저 생성.
    예: '가족에 대해서 어떤 이야기를 하고 싶으신가요?'
    """
    try:
        # 선호 카테고리가 있으면 모든 카테고리에 대해 질문
        preferred_categories = metrics.get("preferred_categories", [])
        if preferred_categories:
            # 선호 카테고리 이름들 수집
            category_names = []
            for cat_num in preferred_categories:
                category = engine.categories.get(cat_num)
                if category:
                    category_names.append(category.category_name)
            
            if category_names:
                categories_text = ", ".join(category_names)
                question_text = f"{categories_text}에 대해서 주로 이야기하게 될 거에요. 만약 이 책이 만들어진다면 누구에게 가장 필요할 것 같나요?"
                selected_cat_num = preferred_categories[0]
            else:
                return {"next_question": None}
        else:
            # 선호 카테고리가 없으면 그냥 어떤 이야기가 하고 싶냐고 물어봄
            question_text = "이 책이 만들어지면 누구에게 가장 필요할 거 같으세요?"
            selected_cat_num = 0
            category_names = []

        return {
            "next_question": {
                "id": f"q-{uuid4().hex[:8]}",
                "material": f"{', '.join(category_names) if preferred_categories else '일반'}_소개",
                "type": "category_intro",
                "text": question_text,
                "material_id": [selected_cat_num, 0, 0]
            }
        }
        
    except Exception as e:
        print(f"[ERROR] 첫 질문 생성 실패: {e}")
        return {"next_question": None}

#V2 추가 함수 - LLM 질문 생성
def generate_question_llm(material: str, target: str, context_answer: Optional[str] = None) -> str:
    """LLM으로 질문 생성"""
    try:
        current_dir = Path(__file__).parent.parent
        flows_dir = current_dir.parent.parent.parent
        flow_path = flows_dir / "interviews" / "standard" / "generate_interview_questions_v2" / "flow.dag.yaml"
        
        if not flow_path.exists():
            print(f"[WARNING] Flow not found: {flow_path}")
            raise FileNotFoundError(f"Flow not found: {flow_path}")
        
        from promptflow import load_flow
        flow = load_flow(str(flow_path.absolute()))
        keywords = [context_answer] if context_answer is not None else []
        
        result = flow(
            material=material,
            type=target,
            keywords=keywords,
            tone="따뜻하고 존중하는 어조",
            max_len=120,
            model="gpt-4o-mini",
            temperature=0.8
        )
        
        question_text = result.get("question", {}).get("text", "")
        if question_text:
            return question_text
        else:
            print(f"[WARNING] LLM returned empty question for {material}, {target}")
            raise ValueError("Empty question returned")
            
    except Exception as e:
        print(f"[ERROR] LLM 질문 생성 실패: {e}")
        print(f"[INFO] Using simple fallback for {material}, {target}")
        return f"{material}에 대해 더 자세히 이야기해 주세요."

#V2 추가 함수 - 테마 관리
class ThemeManager:
    def __init__(self, engine: InterviewEngine, theme_json: dict):
        self.engine = engine
        self.category_name_to_num: Dict[str, int] = {
            cat.category_name: cat.category_num for cat in engine.categories.values()
        }
        themes = {}
        for t in theme_json.get("theme", []):
            theme_name = t.get("name")
            cat_names = t.get("category", [])
            nums = [self.category_name_to_num[n] for n in cat_names if n in self.category_name_to_num]
            if nums:
                themes[theme_name] = nums
        self.themes = themes

    def select_theme(self, theme_name: str, initial_weight: int = 10, *, force: bool = False) -> List[int]:
        nums = self.themes.get(theme_name, [])
        if nums:
            self.engine.boost_theme(nums, initial_weight=initial_weight, force=force)
        return nums