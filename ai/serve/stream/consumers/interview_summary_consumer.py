import json
import os
import pika
from pathlib import Path
import sys
from ..dto import InterviewSummaryResponsePayload
from interviews.interview_summary.dto.request import InterviewSummaryRequestDto, ConversationDto
from logs import get_logger

# flow 경로 추가
current_dir = Path(__file__).parent.parent.parent.parent.parent
flows_dir = current_dir / "flows" / "interview_summary" / "standard" / "summarize_interview"
sys.path.insert(0, str(flows_dir))

from promptflow import load_flow

logger = get_logger()

class InterviewSummaryConsumer:
    def __init__(self):
        self.setup_connection()
        self.setup_flow()
    
    def setup_connection(self):
        rabbitmq_host = os.environ.get("RABBITMQ_HOST")
        rabbitmq_port = int(os.environ.get("RABBITMQ_PORT"))
        rabbitmq_user = os.environ.get("RABBITMQ_USER")
        rabbitmq_password = os.environ.get("RABBITMQ_PASSWORD")
        
        credentials = pika.PlainCredentials(rabbitmq_user, rabbitmq_password)
        self.connection = pika.BlockingConnection(
            pika.ConnectionParameters(
                host=rabbitmq_host,
                port=rabbitmq_port,
                credentials=credentials
            )
        )
        self.channel = self.connection.channel()
        
        # 기존 큐 설정에 맞춰서 선언하지 않고 그냥 사용
        # 큐가 이미 존재한다고 가정
    
    def setup_flow(self):
        # flow 로드 - 올바른 경로 사용
        current_dir = Path(__file__).parent.parent.parent.parent
        flow_path = current_dir / "flows" / "interview_summary" / "standard" / "summarize_interview" / "flow.dag.yaml"
        self.flow = load_flow(str(flow_path))
    
    def start_consuming(self):
        self.channel.basic_consume(
            queue='interview.summary.queue',
            on_message_callback=self.on_message,
            auto_ack=False
        )
        logger.info("Starting interview summary consumer...")
        self.channel.start_consuming()
    
    def on_message(self, channel, method, properties, body):
        try:
            # 메시지 파싱
            payload_data = json.loads(body)
            request_dto = InterviewSummaryRequestDto(**payload_data)
            
            # 인터뷰 요약 생성
            result = self.consume_interview_summary(request_dto)
            
            # 결과 큐에 전송
            from stream import publish_interview_summary_result
            publish_interview_summary_result(result)
            
            # ACK
            channel.basic_ack(delivery_tag=method.delivery_tag)
            logger.info(f"Processed interview summary for ID: {request_dto.interviewId}")
            
        except Exception as e:
            logger.error(f"Error processing interview summary message: {e}")
            channel.basic_nack(delivery_tag=method.delivery_tag, requeue=False)
    
    def consume_interview_summary(self, request_dto: InterviewSummaryRequestDto):
        try:
            logger.info(f"Processing interview summary for ID: {request_dto.interviewId}")
            logger.info(f"Input conversations: {[conv.model_dump() for conv in request_dto.conversations]}")
            
            # flow 실행 (기존 API 로직과 동일)
            result = self.flow(
                conversation=[conv.model_dump() for conv in request_dto.conversations]
            )
            
            logger.info(f"Flow result: {result}")
            
            summary = result.get("summary", "")
            
            if hasattr(summary, '__iter__') and not isinstance(summary, str):
                summary = ''.join(summary)
            
            # 결과 변환
            return InterviewSummaryResponsePayload(
                interviewId=request_dto.interviewId,
                userId=request_dto.userId,
                summary=str(summary)
            )
            
        except Exception as e:
            logger.error(f"Interview summary processing error: {str(e)}")
            raise e
    
    def close(self):
        self.connection.close()

if __name__ == "__main__":
    consumer = InterviewSummaryConsumer()
    try:
        consumer.start_consuming()
    except KeyboardInterrupt:
        consumer.close()
