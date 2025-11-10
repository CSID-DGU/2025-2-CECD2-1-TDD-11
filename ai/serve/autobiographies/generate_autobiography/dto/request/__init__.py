from typing import List
from pydantic import BaseModel, Field

from constants import Gender, ConversationType


class UserInfoDto(BaseModel):
    gender: Gender = Field(default=Gender.FEMALE)
    occupation: str = Field(default="프로그래머")
    age_group: str = Field(default="대학교 재학")


class AutobiographyInfoDto(BaseModel):
    theme: str = Field(default="가족")
    category: str = Field(default="부모")
    reason: str = Field(default="이러이러한 사유로 만들고 싶습니다.")


class InterviewContentDto(BaseModel):
    content: str = Field(
        default="회사에서 맡은 첫 프로젝트는 무엇이었고, 어떤 도전이 있었나요?"
    )
    conversation_type: ConversationType = Field(default=ConversationType.HUMAN)


class AutobiographyGenerateRequestDto(BaseModel):
    user_info: UserInfoDto
    autobiography_info: AutobiographyInfoDto
    interviews: List[InterviewContentDto] = Field(
        default=[
            InterviewContentDto(),
            InterviewContentDto(
                content="제가 맡은 첫 프로젝트는 온라인 쇼핑몰 구축 프로젝트였습니다. 처음으로 프로젝트를 맡아서 설계부터 개발까지 전반적인 업무를 담당했는데, 기존에 경험이 부족했기 때문에 많은 어려움을 겪었습니다. 특히, 프로젝트 일정이 타이트했기 때문에 개발 과정에서 많은 야근을 했었죠.",
                conversation_type=ConversationType.HUMAN,
            ),
        ]
    )
