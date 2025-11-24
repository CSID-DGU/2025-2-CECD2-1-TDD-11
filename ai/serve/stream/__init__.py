# ai_mq_producer.py
import json
import pika
import os
from logs import get_logger
from .dto import InterviewPayload, CategoriesPayload, GeneratedAutobiographyPayload, InterviewSummaryResponsePayload

logger = get_logger()


def publish_persistence_message(payload: InterviewPayload):
    """
    interview에 대한 응답 메시지를 publish하는 함수.
    """
    rabbitmq_host = os.environ.get("RABBITMQ_HOST")
    rabbitmq_port = int(os.environ.get("RABBITMQ_PORT"))
    rabbitmq_user = os.environ.get("RABBITMQ_USER")
    rabbitmq_password = os.environ.get("RABBITMQ_PASSWORD")
    
    # rabbitmq 연결 설정
    credentials = pika.PlainCredentials(rabbitmq_user, rabbitmq_password) # 인증 정보 설정
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(
            host=rabbitmq_host,
            port=rabbitmq_port,
            credentials=credentials
        )
    )
    # 채널 생성
    channel = connection.channel()
    logger.info("Successfully connected to persistence queue.")

    # 순수 json은 불가능, pydantic 모델을 dict로 변환 후 json으로 직렬화
    body = payload.model_dump_json()

    channel.basic_publish(
        exchange='ai.request.exchange',
        routing_key='ai.persistence',
        body=body,
        properties=pika.BasicProperties(
            content_type="application/json",
            delivery_mode=2  # persistent
        )
    )

    connection.close()
    
def publish_categories_message(payload: CategoriesPayload):
    """
    autobiography에 대한 categories, chunks, materials 변경 사항 메시지를 publish하는 함수.
    """
    rabbitmq_host = os.environ.get("RABBITMQ_HOST")
    rabbitmq_port = int(os.environ.get("RABBITMQ_PORT", 5672))
    rabbitmq_user = os.environ.get("RABBITMQ_USER")
    rabbitmq_password = os.environ.get("RABBITMQ_PASSWORD")
    
    credentials = pika.PlainCredentials(rabbitmq_user, rabbitmq_password) # 인증 정보 설정
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(
            host=rabbitmq_host,
            port=rabbitmq_port,
            credentials=credentials
        )
    )
    channel = connection.channel()
    logger.info("Successfully connected to categories queue.")

    # 순수 json은 불가능, pydantic 모델을 dict로 변환 후 json으로 직렬화
    body = payload.model_dump_json()
    
    channel.basic_publish(
        exchange='interview.meta.exchange',
        routing_key='interview.meta',
        body=body,
        properties=pika.BasicProperties(
            content_type="application/json",
            delivery_mode=2  # persistent
        )
    )

    connection.close()

def publish_generated_autobiography(payload: GeneratedAutobiographyPayload):
    """
    생성된 자서전 결과를 publish하는 함수.
    """
    rabbitmq_host = os.environ.get("RABBITMQ_HOST")
    rabbitmq_port = int(os.environ.get("RABBITMQ_PORT", 5672))
    rabbitmq_user = os.environ.get("RABBITMQ_USER")
    rabbitmq_password = os.environ.get("RABBITMQ_PASSWORD")
    
    credentials = pika.PlainCredentials(rabbitmq_user, rabbitmq_password)
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(
            host=rabbitmq_host,
            port=rabbitmq_port,
            credentials=credentials
        )
    )
    channel = connection.channel()
    
    # 큐 선언
    channel.queue_declare(queue='generated_autobiography_queue', durable=True)
    
    body = payload.model_dump_json()
    
    channel.basic_publish(
        exchange='autobiography.trigger.exchange',
        routing_key='autobiography.trigger.result',
        body=body,
        properties=pika.BasicProperties(
            content_type="application/json",
            delivery_mode=2
        )
    )
    
    logger.info(f"Published generated autobiography for ID: {payload.autobiographyId}")
    connection.close()

def publish_interview_summary_result(payload: InterviewSummaryResponsePayload):
    """
    인터뷰 요약 결과를 publish하는 함수.
    """
    rabbitmq_host = os.environ.get("RABBITMQ_HOST")
    rabbitmq_port = int(os.environ.get("RABBITMQ_PORT", 5672))
    rabbitmq_user = os.environ.get("RABBITMQ_USER")
    rabbitmq_password = os.environ.get("RABBITMQ_PASSWORD")
    
    credentials = pika.PlainCredentials(rabbitmq_user, rabbitmq_password)
    connection = pika.BlockingConnection(
        pika.ConnectionParameters(
            host=rabbitmq_host,
            port=rabbitmq_port,
            credentials=credentials
        )
    )
    channel = connection.channel()
    
    body = payload.model_dump_json()
    
    channel.basic_publish(
        exchange='interview.summary.exchange',
        routing_key='interview.summary.result',
        body=body,
        properties=pika.BasicProperties(
            content_type="application/json",
            delivery_mode=2
        )
    )
    
    logger.info(f"Published interview summary result for ID: {payload.interviewId}")
    connection.close()