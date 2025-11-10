from pydantic import BaseModel


class AutobiographyGenerateResponseDto(BaseModel):
    title: str
    autobiographical_text: str
