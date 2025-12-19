import json
from pathlib import Path
from promptflow.core import Flow
from .dto import InterviewAnswersPayload, GeneratedAutobiographyPayload
from .cycle_manager import CycleManager
from logs import get_logger

logger = get_logger()

# Flow 로드
current_dir = Path(__file__).parent.parent.parent
flow_path = current_dir / "flows" / "autobiographies" / "standard" / "generate_autobiography" / "flow.dag.yaml"
flow = Flow.load(str(flow_path))

cycle_manager = CycleManager()

def generate_autobiography(request: InterviewAnswersPayload) -> GeneratedAutobiographyPayload:
    """자서전 생성"""
    logger.info(f"[GENERATE] Starting autobiography_id={request.autobiographyId} user_id={request.userId} cycle_id={request.cycleId} step={request.step}")
    
    result = flow(
        user_info=request.userInfo.dict(),
        autobiography_info=request.autobiographyInfo.dict(),
        interviews=[answer.dict() for answer in request.answers],
        autobiography_id=request.autobiographyId
    )
    
    title = f"{request.autobiographyInfo.theme} - {request.autobiographyInfo.category}에 대한 나의 이야기"
    text = "자서전 생성 완료"
    
    try:
        flow_output = result.get("result") if isinstance(result, dict) else result
        
        if hasattr(flow_output, '__iter__') and not isinstance(flow_output, (str, dict)):
            flow_output = ''.join(str(item) for item in flow_output)
        else:
            flow_output = str(flow_output)
        
        try:
            parsed = json.loads(flow_output)
            if isinstance(parsed, dict):
                title = parsed.get("title", title)
                text = parsed.get("autobiographical_text", text)
        except json.JSONDecodeError:
            text = flow_output
    except Exception as e:
        logger.error(f"[GENERATE] Result processing failed: {e}")
        text = str(result) if result else text
    
    # Cycle 완료 여부 확인
    is_last = False
    if request.cycleId:
        is_last = cycle_manager.increment_completed(request.cycleId)
    
    logger.info(f"[GENERATE] Completed autobiography_id={request.autobiographyId} cycle_id={request.cycleId} is_last={is_last}")
    
    return GeneratedAutobiographyPayload(
        cycleId=request.cycleId,
        step=request.step,
        autobiographyId=request.autobiographyId,
        userId=request.userId,
        title=str(title),
        content=str(text),
        isLast=is_last
    )
