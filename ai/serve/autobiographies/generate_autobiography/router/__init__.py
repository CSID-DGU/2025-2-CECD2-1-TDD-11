import json

from fastapi import APIRouter, HTTPException
from fastapi.params import Depends
from promptflow.core import Flow
import asyncio
from pydantic_core import ValidationError
from starlette.requests import Request

from auth import MemberRole, get_current_user, AuthRequired
from autobiographies.generate_autobiography.dto.request import (
    AutobiographyGenerateRequestDto,
)
from autobiographies.generate_autobiography.dto.response import (
    AutobiographyGenerateResponseDto,
)
from logs import get_logger

logger = get_logger()

router = APIRouter()


@router.post(
    "/generate/{autobiography_id}",
    dependencies=[Depends(AuthRequired())],
    response_model=AutobiographyGenerateResponseDto,
    summary="자서전 생성",
    description="유저의 정보와 인터뷰 대화 내역을 입력받아 자서전을 생성합니다.",
    tags=["자서전 (Autobiography)"],
)
async def generate_autobiography(
    autobiography_id: int,
    request: Request,
    requestDto: AutobiographyGenerateRequestDto,
):
    current_user = get_current_user(request)
    try:
        logger.info(f"Generating autobiography for user {current_user.member_id}")
        # Collect the results as they are returned by the flow
        flow = Flow.load(
            "../flows/autobiographies/standard/generate_autobiography/flow.dag.yaml"
        )

        # 자서전 생성 (병렬 처리)
        async def generate_async():
            return flow(
                user_info=requestDto.user_info.dict(),
                autobiography_info=requestDto.autobiography_info.dict(),
                interviews=[interview.dict() for interview in requestDto.interviews],
                autobiography_id=autobiography_id
            )
        
        result = await generate_async()
        
        return AutobiographyGenerateResponseDto(
            title=result.get("title", ""),
            autobiographical_text=result.get("autobiographical_text", "")
        )

    except json.JSONDecodeError:
        raise HTTPException(
            status_code=500,
            detail="Failed to parse the autobiography generation result.",
        )

    except ValidationError as e:
        raise HTTPException(status_code=400, detail=str(e))

    except Exception as e:
        raise HTTPException(
            status_code=500, detail=f"An unexpected error occurred: {str(e)}"
        )
