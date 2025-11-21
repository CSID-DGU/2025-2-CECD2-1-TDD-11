import threading
import os
from logs import get_logger

logger = get_logger()

def start_all_consumers():
    """모든 RabbitMQ consumers를 백그라운드에서 시작"""

    def start_interview_summary_consumer():
        try:
            from .interview_summary_consumer import InterviewSummaryConsumer
            consumer = InterviewSummaryConsumer()
            logger.info("Starting interview summary consumer...")
            consumer.start_consuming()
        except Exception as e:
            logger.error(f"Interview summary consumer error: {e}")

    # 환경변수 체크
    if not all([
        os.environ.get("RABBITMQ_HOST"),
        os.environ.get("RABBITMQ_USER"),
        os.environ.get("RABBITMQ_PASSWORD")
    ]):
        logger.warning("RabbitMQ environment variables not set, skipping consumers")
        return

    # 각 consumer를 별도 스레드에서 실행
    threads = []
    
    interview_summary_thread = threading.Thread(
        target=start_interview_summary_consumer,
        name="InterviewSummaryConsumer", 
        daemon=True
    )
    
    threads.extend([interview_summary_thread])
    
    for thread in threads:
        thread.start()
        logger.info(f"Started {thread.name}")
    
    logger.info("All RabbitMQ consumers started successfully")

# 모듈 import 시 자동으로 consumers 시작
start_all_consumers()
