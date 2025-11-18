# ai_mq_producer.py
import json
import pika
import os
from logs import get_logger

logger = get_logger()


def publish_persistence_message(payload: dict):
    """
    interview에 대한 응답 메시지를 publish하는 함수.
    """
    rabbitmq_host = os.environ.get("RABBITMQ_HOST")
    rabbitmq_port = int(os.environ.get("RABBITMQ_PORT", 5672))
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

    channel.basic_publish(
        exchange='ai.request.exchange',
        routing_key='persist',
        body=json.dumps(payload),
        properties=pika.BasicProperties(
            content_type="application/json",
            delivery_mode=2  # persistent
        )
    )

    connection.close()
    
def publish_categories_message(payload: dict):
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

    channel.basic_publish(
        exchange='ai.request.exchange',
        routing_key='persist',
        body=json.dumps(payload),
        properties=pika.BasicProperties(
            content_type="application/json",
            delivery_mode=2  # persistent
        )
    )

    connection.close()