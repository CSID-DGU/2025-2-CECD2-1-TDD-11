from fastapi import APIRouter, HTTPException
from .dto import CycleInitMessage, InterviewSummaryRequestDto, InterviewSummaryResponsePayload, InterviewAnswersPayload, GeneratedAutobiographyPayload
from .cycle_manager import CycleManager
from .summary_service import generate_summary
from .autobiography_service import generate_autobiography
from logs import get_logger

logger = get_logger()
router = APIRouter()
cycle_manager = CycleManager()

@router.post("/api/v2/cycle/init", summary="Cycle 초기화", tags=["Cycle"])
async def init_cycle(request: CycleInitMessage):
    """자서전 생성 Cycle 초기화"""
    try:
        cycle_manager.init_cycle(
            request.cycleId,
            request.expectedCount,
            request.autobiographyId,
            request.userId
        )
        return {"status": "success", "cycleId": request.cycleId}
    except Exception as e:
        logger.error(f"[CYCLE] Init failed: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/api/v2/summary/generate", response_model=InterviewSummaryResponsePayload, summary="인터뷰 요약 생성", tags=["Summary"])
async def create_summary(request: InterviewSummaryRequestDto):
    """인터뷰 요약 생성 (함수 호출 방식)"""
    try:
        return generate_summary(request)
    except Exception as e:
        logger.error(f"[SUMMARY] Generation failed: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/api/v2/autobiography/generate", response_model=GeneratedAutobiographyPayload, summary="자서전 생성", tags=["Autobiography"])
async def create_autobiography(request: InterviewAnswersPayload):
    """자서전 생성 (함수 호출 방식)"""
    try:
        return generate_autobiography(request)
    except Exception as e:
        logger.error(f"[GENERATE] Generation failed: {e}")
        raise HTTPException(status_code=500, detail=str(e))
