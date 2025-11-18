from pydantic import BaseModel, Field
from typing import Optional
from datetime import datetime
from typing import List

# AI 인터뷰 질의응답에 대한 payload 정의
class Conversation(BaseModel):
    content: str
    conversationType: str
    timestamp: datetime
    
class InterviewQuestion(BaseModel):
    questionText: str
    questionOrder: int
    timestamp: datetime

class InterviewPayload(BaseModel):
    interviewId: int
    userId: int
    conversation: Conversation
    interviewQuestion: InterviewQuestion

# Categories와 하위 데이터들에 대한 payload 정의

class ChunksPayload(BaseModel):
    categoryId: int
    chunkOrder: int
    weight: int
    timestamp: datetime

class MaterialsPayload(BaseModel):
    chunkId: int
    materialOrder: int
    example: int
    similarEvent: int
    count: int
    principle: List[int] = Field(default=[0,0,0,0,0,0])
    timestamp: datetime

class CategoriesPayload(BaseModel):
    autobiographyId: str
    userId: str
    categoryId: int
    chunks: Optional[list[ChunksPayload]] = []
    materials: Optional[list[MaterialsPayload]] = []