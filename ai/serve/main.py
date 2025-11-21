import os

from fastapi import FastAPI
from promptflow.connections import AzureOpenAIConnection, OpenAIConnection
from promptflow.client import PFClient
from dotenv import load_dotenv

from autobiographies.generate_autobiography.router import (
    router as autobiographies_generate_autobiography_router,
)
from interviews.interview_chat_v2.router import (
    router as interviews_request_interview_chat_v2_router,
)
from interviews.interview_summary.router import (
    router as interviews_summary_router,
)

from logs import get_logger
import stream.consumers  # 컨슈머 자동 시작

load_dotenv()

logger = get_logger()


def create_connection():
    api_key = os.environ.get("AZURE_OPENAI_API_KEY")
    connection = None
    if api_key.startswith("sk-"):
        connection = OpenAIConnection(
            name="open_ai_connection",
            api_key=os.environ.get("AZURE_OPENAI_API_KEY"),
        )
    else:
        connection = AzureOpenAIConnection(
            name="open_ai_connection",
            api_key=os.environ.get("AZURE_OPENAI_API_KEY"),
            api_base=os.environ.get("AZURE_OPENAI_API_BASE"),
            azure_endpoint=os.environ.get("AZURE_OPENAI_API_BASE", "azure"),
            api_version=os.environ.get(
                "AZURE_OPENAI_API_VERSION", "2023-07-01-preview"
            ),
        )

    pf = PFClient()
    conn = pf.connections.create_or_update(connection)

    logger.info(f"Successfully created connection {conn}")


create_connection()

app = FastAPI(
    description="Life Bookshelf AI API",
    version="0.0.1",
)

# 유지되는 API들
app.include_router(autobiographies_generate_autobiography_router, prefix="/api/v2/autobiographies")
app.include_router(interviews_request_interview_chat_v2_router, prefix="/api/v2/interviews")
app.include_router(interviews_summary_router, prefix="/api/v2/interviews")


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=3000)
