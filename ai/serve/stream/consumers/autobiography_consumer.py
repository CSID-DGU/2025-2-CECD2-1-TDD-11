import json
import os
import pika
from promptflow.core import Flow
from ..dto import InterviewAnswersPayload, GeneratedAutobiographyPayload
from autobiographies.generate_autobiography.dto.request import (
    AutobiographyGenerateRequestDto, 
    UserInfoDto, 
    AutobiographyInfoDto, 
    InterviewContentDto
)
from constants import ConversationType
from logs import get_logger

logger = get_logger()

class AutobiographyConsumer:
    def __init__(self):
        self.setup_connection()
    
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
    
    def start_consuming(self):
        self.channel.basic_consume(
            queue='autobiography.trigger.queue',
            on_message_callback=self.on_message,
            auto_ack=False
        )
        logger.info("Starting autobiography consumer...")
        self.channel.start_consuming()
    
    def on_message(self, channel, method, properties, body):
        try:
            # 메시지 파싱
            payload_data = json.loads(body)
            
            # cycleId와 step 추출
            cycle_id = payload_data.get("cycleId")
            step = payload_data.get("step", 1)
            
            # cycleId가 없으면 메시지 거부
            if not cycle_id:
                logger.warning("Message rejected: cycleId is required for new cycle management system")
                channel.basic_nack(delivery_tag=method.delivery_tag, requeue=False)
                return
            
            payload = InterviewAnswersPayload(**payload_data)
            
            # 자서전 생성
            result = self.consume_interview_answers(payload)
            
            # Aggregator로 완료 결과 전송
            from stream import publish_result_to_aggregator
            publish_result_to_aggregator(
                cycle_id, 
                step, 
                payload.autobiographyId, 
                payload.userId,
                {"title": result.title, "content": result.content}
            )
            
            # 기존 결과 큐에도 전송 (cycleId 포함하여 전송)
            from stream import publish_generated_autobiography
            
            # cycleId와 step을 결과에 포함
            result.cycleId = cycle_id
            result.step = step
                
            publish_generated_autobiography(result)
            
            # ACK
            channel.basic_ack(delivery_tag=method.delivery_tag)
            logger.info(f"Processed autobiography generation for ID: {payload.autobiographyId}, cycle: {cycle_id}")
            
        except Exception as e:
            logger.error(f"Error processing message: {e}")
            channel.basic_nack(delivery_tag=method.delivery_tag, requeue=False)
    
    def consume_interview_answers(self, payload: InterviewAnswersPayload):
        logger.info(f"[TEST] consume_interview_answers 시작 - autobiographyId: {payload.autobiographyId}")
        
        # 데이터 변환
        user_info = UserInfoDto(
            gender=payload.userInfo.gender,
            occupation=payload.userInfo.occupation,
            age_group=payload.userInfo.ageGroup
        )
        
        autobiography_info = AutobiographyInfoDto(
            theme=payload.autobiographyInfo.theme,
            reason=payload.autobiographyInfo.reason,
            category=payload.autobiographyInfo.category
        )
        
        interviews = [
            InterviewContentDto(
                content=answer.content,
                conversation_type=ConversationType(answer.conversationType)
            )
            for answer in payload.answers
        ]
        
        # Flow 직접 실행
        current_dir = os.path.dirname(os.path.abspath(__file__))
        project_root = os.path.join(current_dir, "..", "..", "..", "..")
        flow_path = os.path.join(project_root, "ai", "flows", "autobiographies", "standard", "generate_autobiography", "flow.dag.yaml")
        flow_path = os.path.abspath(flow_path)
        
        flow = Flow.load(flow_path)
        result = flow(
            user_info=user_info.dict(),
            autobiography_info=autobiography_info.dict(),
            interviews=[interview.dict() for interview in interviews],
            autobiography_id=payload.autobiographyId
        )
        logger.info(f"[TEST] Flow 결과 원본: {result}")
        
        # 결과 처리
        title = f"{autobiography_info.theme} - {autobiography_info.category}에 대한 나의 이야기"
        text = "자서전 생성 완료"
        
        # Flow 결과 처리 - dict에서 result 키의 generator 추출
        try:
            
            # Flow 결과에서 result 키 추출
            flow_output = result.get("result") if isinstance(result, dict) else result
            
            # generator 객체인 경우 내용 추출
            if hasattr(flow_output, '__iter__') and not isinstance(flow_output, (str, dict)):
                flow_content = ''.join(str(item) for item in flow_output)
            else:
                flow_content = str(flow_output)
            
            # JSON 파싱 시도
            try:
                parsed = json.loads(flow_content)
                
                if isinstance(parsed, dict):
                    title = parsed.get("title", title)
                    text = parsed.get("autobiographical_text", text)
                else:
                    text = flow_content
            except json.JSONDecodeError as json_error:
                logger.error(f"[TEST] JSON 파싱 실패: {json_error}")
                text = flow_content
                
        except Exception as parse_error:
            text = str(result) if result else text
            logger.error(f"Fail ::: {parse_error}")
        
        # 결과 변환
        final_result = GeneratedAutobiographyPayload(
            autobiographyId=payload.autobiographyId,
            userId=payload.userId,
            title=str(title),
            content=str(text)
        )
        
        return final_result
    

    
    def close(self):
        self.connection.close()

if __name__ == "__main__":
    consumer = AutobiographyConsumer()
    try:
        consumer.start_consuming()
    except KeyboardInterrupt:
        consumer.close()