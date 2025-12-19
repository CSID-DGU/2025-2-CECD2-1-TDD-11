from pydantic import BaseModel, Field
from typing import Optional, List
from datetime import datetime

# AI 인터뷰 질의응답에 대한 payload 정의
class Conversation(BaseModel):
    content: str
    conversationType: str
    materials: Optional[str] = ""
    
class InterviewQuestion(BaseModel):
    questionText: str
    questionOrder: int
    materials: Optional[str] = ""

class InterviewPayload(BaseModel):
    autobiographyId: int
    userId: int
    conversation: Optional[List[Conversation]] = []
    interviewQuestion: Optional[InterviewQuestion] = None

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
    autobiographyId: int
    userId: int
    themeId: int
    categoryId: int
    chunks: Optional[List[ChunksPayload]] = []
    materials: Optional[List[MaterialsPayload]] = []
    
# 자서전 Generate를 위한 사용자 정보와 answers 정의
class UserInfo(BaseModel):
    gender: str
    occupation: str
    ageGroup: str

class AutobiographyInfo(BaseModel):
    theme: str
    reason: str
    category: str

class InterviewAnswer(BaseModel):
    content: str
    conversationType: str

class InterviewAnswersPayload(BaseModel):
    cycleId: Optional[str] = None
    step: Optional[int] = 1
    autobiographyId: int
    userId: int
    userInfo: UserInfo
    autobiographyInfo: AutobiographyInfo
    answers: List[InterviewAnswer]
    
# generate 자서전에 대한 response payload 정의
class GeneratedAutobiographyPayload(BaseModel):
    cycleId: Optional[str] = None
    step: Optional[int] = None
    autobiographyId: int
    userId: int
    title: str
    content: str
    isLast: bool
    
# interview에 대한 요약 응답 payload 정의
class InterviewSummaryResponsePayload(BaseModel):
    interviewId: int
    userId: int
    summary: str

# interview summary request dto 정의
class ConversationDto(BaseModel):
    question: str = Field(description="질문 내용")
    conversation: str = Field(description="답변 내용")

class InterviewSummaryRequestDto(BaseModel):
    interviewId: int = Field(description="인터뷰 ID")
    userId: int = Field(description="사용자 ID")
    conversations: List[ConversationDto] = Field(description="인터뷰 대화 내역")

# cycle init
class CycleInitMessage(BaseModel):
    cycleId: str
    expectedCount: int
    autobiographyId: int
    userId: int
