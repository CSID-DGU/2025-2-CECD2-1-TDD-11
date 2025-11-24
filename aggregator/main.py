import asyncio
import json
import logging
import os
import uuid
from typing import Dict, Any

import pika
import redis
from fastapi import FastAPI
from pydantic import BaseModel
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(title="Aggregator Service")

class CycleInitMessage(BaseModel):
    cycleId: str
    expectedCount: int
    autobiographyId: int
    userId: int

class ResultMessage(BaseModel):
    cycleId: str
    step: int
    autobiographyId: int
    userId: int
    result: Dict[str, Any] = {}
    status: str = "completed"

class AggregatorService:
    def __init__(self):
        self.redis_client = redis.Redis(
            host=os.getenv('REDIS_HOST'),
            port=int(os.getenv('REDIS_PORT')),
            decode_responses=True
        )
        self.setup_rabbitmq()
        
    def setup_rabbitmq(self):
        credentials = pika.PlainCredentials(
            os.getenv('RABBITMQ_DEFAULT_USER'),
            os.getenv('RABBITMQ_DEFAULT_PASS')
        )
        self.connection = pika.BlockingConnection(
            pika.ConnectionParameters(
                host=os.getenv('RABBITMQ_HOST'),
                port=int(os.getenv('RABBITMQ_PORT')),
                credentials=credentials
            )
        )
        self.channel = self.connection.channel()
        
    def start_consuming(self):
        self.channel.basic_consume(
            queue='autobiography.trigger.cycle.init.queue',
            on_message_callback=self.on_cycle_init_message,
            auto_ack=False
        )
        self.channel.basic_consume(
            queue='autobiography.trigger.cycle.result.queue',
            on_message_callback=self.on_result_message,
            auto_ack=False
        )
        self.channel.start_consuming()
        
    def on_cycle_init_message(self, channel, method, properties, body):
        try:
            message_data = json.loads(body)
            init_msg = CycleInitMessage(**message_data)
            
            # Set expected count in Redis
            expected_count_key = f"cycle:{init_msg.cycleId}:expected_count"
            self.redis_client.setex(expected_count_key, 3600, init_msg.expectedCount)
            
            logger.info(f"Initialized cycle {init_msg.cycleId} with expected_count: {init_msg.expectedCount}")
            channel.basic_ack(delivery_tag=method.delivery_tag)
            
        except Exception as e:
            logger.error(f"Error processing cycle init message: {e}")
            channel.basic_nack(delivery_tag=method.delivery_tag, requeue=False)
        
    def on_result_message(self, channel, method, properties, body):
        try:
            # Parse message
            message_data = json.loads(body)
            result_msg = ResultMessage(**message_data)
            
            cycle_id = result_msg.cycleId
            logger.info(f"Processing result for cycle_id: {cycle_id}, step: {result_msg.step}")
            
            # Get expected count from Redis
            expected_count_key = f"cycle:{cycle_id}:expected_count"
            expected_count = self.redis_client.get(expected_count_key)
            
            if not expected_count:
                # Auto-initialize with default count if not set
                logger.warning(f"No expected_count found for cycle_id: {cycle_id}, auto-initializing with count=1")
                expected_count = 1
                expected_count_key = f"cycle:{cycle_id}:expected_count"
                self.redis_client.setex(expected_count_key, 3600, expected_count)
            else:
                expected_count = int(expected_count)
            
            # Store result in Redis
            result_key = f"cycle:{cycle_id}:results:{result_msg.step}"
            self.redis_client.setex(result_key, 3600, json.dumps(result_msg.dict()))
            
            # Increment done count
            done_count_key = f"cycle:{cycle_id}:done_count"
            done_count = self.redis_client.incr(done_count_key)
            self.redis_client.expire(done_count_key, 3600)
            
            logger.info(f"Cycle {cycle_id}: {done_count}/{expected_count} completed")
            
            # Check if cycle is complete
            if done_count >= expected_count:
                logger.info(f"Cycle {cycle_id} completed! Triggering merge...")
                self.trigger_merge(cycle_id, result_msg.autobiographyId, result_msg.userId)
                self.cleanup_cycle(cycle_id)
                
            channel.basic_ack(delivery_tag=method.delivery_tag)
            
        except Exception as e:
            logger.error(f"Error processing result message: {e}")
            channel.basic_nack(delivery_tag=method.delivery_tag, requeue=False)
            
    def trigger_merge(self, cycle_id: str, autobiography_id: int, user_id: int):
        """Trigger merge process by publishing to merge-queue"""
        merge_message = {
            "cycleId": cycle_id,
            "autobiographyId": autobiography_id,
            "userId": user_id,
            "action": "merge"
        }
        
        self.channel.basic_publish(
            exchange='autobiography.trigger.exchange',
            routing_key='autobiography.trigger.cycle.merge',
            body=json.dumps(merge_message),
            properties=pika.BasicProperties(
                delivery_mode=2,  # Make message persistent
                headers={'cycle_id': cycle_id}
            )
        )
        logger.info(f"Merge triggered for cycle_id: {cycle_id}")
        
    def cleanup_cycle(self, cycle_id: str):
        """Clean up Redis keys for completed cycle"""
        pattern = f"cycle:{cycle_id}:*"
        keys = self.redis_client.keys(pattern)
        if keys:
            self.redis_client.delete(*keys)
            logger.info(f"Cleaned up {len(keys)} keys for cycle_id: {cycle_id}")

# Global aggregator instance
aggregator = AggregatorService()

@app.on_event("startup")
async def startup_event():
    # Start consuming in background
    import threading
    consumer_thread = threading.Thread(target=aggregator.start_consuming, daemon=True)
    consumer_thread.start()
    logger.info("Aggregator service started")

@app.get("/health")
async def health_check():
    return {"status": "healthy"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8001)
