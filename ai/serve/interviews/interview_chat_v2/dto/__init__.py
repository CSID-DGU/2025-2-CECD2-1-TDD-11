from pydantic import BaseModel, Field
from typing import List, Dict, Any, Optional


# ===== Shared DTOs =====
class MaterialDto(BaseModel):
    order: int
    name: str
    principle: List[int] = Field(default=[0, 0, 0, 0, 0, 0])
    example: int = 0
    similar_event: int = 0
    count: int = 0


class ChunkDto(BaseModel):
    chunk_num: int
    chunk_name: str
    chunk_weight: int = 0
    materials: List[MaterialDto] = Field(default_factory=list)


class CategoryDto(BaseModel):
    category_num: int
    category_name: str
    chunks: List[ChunkDto] = Field(default_factory=list)


class EngineStateDto(BaseModel):
    last_material_id: Optional[List[int]] = Field(default=None)
    last_material_streak: int = 0
    epsilon: float = 0.10


class MetricsDto(BaseModel):
    session_id: str
    categories: List[CategoryDto]
    engine_state: EngineStateDto = Field(default_factory=EngineStateDto)
    asked_total: int = 0
    policy_version: str = "v2.0.0"


# ===== Session Start DTOs =====
class SessionStartRequestDto(BaseModel):
    session_id: str
    user_id: Optional[str] = None
    preferred_categories: List[int] = Field(default_factory=list)
    previous_metrics: Optional[MetricsDto] = None
    
    class Config:
        json_schema_extra = {
            "example": {
                "session_id": "session-12345",
                "preferred_categories": [1, 2]
            }
        }


class SessionStartResponseDto(BaseModel):
    session_id: str
    first_question: Optional[Dict[str, Any]]
    
    class Config:
        json_schema_extra = {
            "example": {
                "session_id": "session-12345",
                "first_question": {
                    "id": "q-fcf8ea8c",
                    "material": "좋아했던 과목",
                    "type": "w1",
                    "text": "학업 전공 공부 좋아했던 과목에 대해 '언제' 측면에서 더 구체적으로 들려주세요.",
                    "material_id": [1, 1, 1]
                }
            }
        }


# ===== Turn DTOs =====
class InterviewChatV2RequestDto(BaseModel):
    session_id: str
    answer_text: str
    
    class Config:
        json_schema_extra = {
            "example": {
                "session_id": "session-12345",
                "answer_text": "저는 내성적인 성격이에요. 사람들과 어울리는 것보다는 혼자 있는 시간을 더 좋아하고, 새로운 환경에 적응하는데 시간이 좀 걸리는 편이에요."
            }
        }


class InterviewChatV2ResponseDto(BaseModel):
    next_question: Optional[Dict[str, Any]]
    
    class Config:
        json_schema_extra = {
            "example": {
                "next_question": {
                    "id": "q-def456",
                    "material": "설명",
                    "type": "ex",
                    "text": "설명과 관련된 구체적인 '예시 한 가지'를 자세히 이야기해 주세요.",
                    "material_id": [8, 1, 1]
                }
            }
        }


# ===== Session End DTOs =====
class SessionEndRequestDto(BaseModel):
    session_id: str


class SessionEndResponseDto(BaseModel):
    session_id: str
    final_metrics: Optional[MetricsDto]
    pool_to_save: List[Dict[str, Any]] = Field(default_factory=list)