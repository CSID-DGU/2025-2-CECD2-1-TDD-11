from fastapi import APIRouter, HTTPException, Depends
from promptflow.core import Flow
from starlette.requests import Request

from auth import get_current_user, AuthRequired
from interviews.interview_summary.dto.request import InterviewSummaryRequestDto
from interviews.interview_summary.dto.response import InterviewSummaryResponseDto
from logs import get_logger

logger = get_logger()

router = APIRouter()


@router.post(
    "/api/v2/interviews/summary/{interview_id}",
    dependencies=[Depends(AuthRequired())],
    response_model=InterviewSummaryResponseDto,
    summary="인터뷰 요약",
    description="인터뷰 대화 내역을 입력받아 요약을 생성합니다.",
    tags=["인터뷰 (Interview)"],
)
async def summarize_interview(
    interview_id: int,
    request: Request,
    requestDto: InterviewSummaryRequestDto,
):
    try:
        current_user = get_current_user(request)
        
        flow_path = "d:/lifeLibrarians/2025-2-CECD2-1-TDD-11/ai/flows/interview_summary/standard/summarize_interview/flow.dag.yaml"
        
        flow = Flow.load(flow_path)
        result = flow(
            interview_id=interview_id,
            conversation=[conv.model_dump() for conv in requestDto.conversation]
        )
        
        summary = result.get("summary", "")
        
        if hasattr(summary, '__iter__') and not isinstance(summary, str):
            summary = ''.join(summary)
        
        return InterviewSummaryResponseDto(summary=str(summary))

    except Exception as e:
        logger.error(f"Interview summary error: {str(e)}")
        raise HTTPException(status_code=500, detail=f"error 발생: {str(e)}")
