import json
import os

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
    try:
        current_user = get_current_user(request)
        # Flow 로드 및 실행
        # 프로젝트 루트 기준 상대 경로 사용
        current_dir = os.path.dirname(os.path.abspath(__file__))
        project_root = os.path.join(current_dir, "..", "..", "..", "..")
        flow_path = os.path.join(project_root, "flows", "autobiographies", "standard", "generate_autobiography", "flow.dag.yaml")
        flow_path = os.path.abspath(flow_path)
        
        if not os.path.exists(flow_path):
            raise HTTPException(status_code=500, detail=f"Flow file not found at: {flow_path}")
        
        flow = Flow.load(str(flow_path))
        result = flow(
            user_info=requestDto.user_info.dict(),
            autobiography_info=requestDto.autobiography_info.dict(),
            interviews=[interview.dict() for interview in requestDto.interviews],
            autobiography_id=autobiography_id
        )
        
        # 기본값
        title = f"{requestDto.autobiography_info.theme} - {requestDto.autobiography_info.category}에 대한 나의 이야기"
        text = "인터뷰 내용을 바탕으로 자서전을 생성하는 중입니다..."
        
        # Flow 결과 처리
        if isinstance(result, dict) and "result" in result:
            flow_output = result["result"]
            
            # Generator 처리
            if hasattr(flow_output, '__iter__') and not isinstance(flow_output, str):
                try:
                    flow_output = ''.join(flow_output)
                except:
                    flow_output = "자서전 생성 중 오류 발생"
            
            try:
                parsed = json.loads(str(flow_output))
                if isinstance(parsed, dict):
                    title = parsed.get("title", title)
                    text = parsed.get("autobiographical_text", text)
            except:
                text = str(flow_output) if flow_output else text


        
        return AutobiographyGenerateResponseDto(
            title=str(title),
            autobiographical_text=str(text)
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
