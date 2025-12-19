from pathlib import Path
from promptflow.core import Flow
from .dto import InterviewSummaryRequestDto, InterviewSummaryResponsePayload
from logs import get_logger

logger = get_logger()

# Flow 로드
current_dir = Path(__file__).parent.parent.parent
flow_path = current_dir / "flows" / "interview_summary" / "standard" / "summarize_interview" / "flow.dag.yaml"
flow = Flow.load(str(flow_path))

def generate_summary(request: InterviewSummaryRequestDto) -> InterviewSummaryResponsePayload:
    """인터뷰 요약 생성"""
    logger.info(f"[SUMMARY] Starting interview_id={request.interviewId} user_id={request.userId} conversations={len(request.conversations)}")
    
    result = flow(
        conversation=[{"question": conv.question, "conversation": conv.conversation} for conv in request.conversations]
    )
    
    summary = result.get("summary", "")
    if hasattr(summary, '__iter__') and not isinstance(summary, str):
        summary = ''.join(summary)
    
    logger.info(f"[SUMMARY] Completed interview_id={request.interviewId} summary_length={len(str(summary))}")
    
    return InterviewSummaryResponsePayload(
        interviewId=request.interviewId,
        userId=request.userId,
        summary=str(summary)
    )
