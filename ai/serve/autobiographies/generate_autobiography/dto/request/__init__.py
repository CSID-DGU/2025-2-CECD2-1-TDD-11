from typing import List
from pydantic import BaseModel, Field

from constants import Gender, ConversationType


class UserInfoDto(BaseModel):
    gender: Gender = Field(default=Gender.FEMALE, description="사용자 성별", example="FEMALE")
    occupation: str = Field(default="프로그래머", description="직업", example="소프트웨어 엔지니어")
    age_group: str = Field(default="대학교 재학", description="연령대/학력", example="20대 직장인")


class AutobiographyInfoDto(BaseModel):
    theme: str = Field(default="가족", description="자서전 주제", example="성장과 도전")
    category: str = Field(default="부모", description="카테고리", example="학업과 진로")
    reason: str = Field(default="이러이러한 사유로 만들고 싶습니다.", description="작성 이유", example="대학 생활의 소중한 추억들을 기록하고 싶어서 자서전을 작성하게 되었습니다.")


class InterviewContentDto(BaseModel):
    content: str = Field(
        default="회사에서 맡은 첫 프로젝트는 무엇이었고, 어떤 도전이 있었나요?",
        description="인터뷰 질문 또는 답변 내용",
        example="대학교에서 가장 기억에 남는 경험은 무엇인가요?"
    )
    conversation_type: ConversationType = Field(default=ConversationType.HUMAN)


class AutobiographyGenerateRequestDto(BaseModel):
    user_info: UserInfoDto
    autobiography_info: AutobiographyInfoDto
    interviews: List[InterviewContentDto] = Field(
        default=[
            InterviewContentDto(),
            InterviewContentDto(
                content="가장 기억에 남는 것은 졸업 프로젝트를 진행했던 경험입니다. 팀원들과 함께 6개월 동안 웹 애플리케이션을 개발했는데, 처음에는 의견 충돌도 많았지만 서로 소통하며 문제를 해결해 나가는 과정에서 많이 성장할 수 있었습니다. 특히 밤늦게까지 함께 코딩하며 디버깅했던 순간들이 지금도 생생하게 기억납니다.",
                conversation_type=ConversationType.HUMAN,
            ),
        ]
    )
