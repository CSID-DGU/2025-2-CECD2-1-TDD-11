"""음성 API 라우터"""
from fastapi import APIRouter, HTTPException, UploadFile, File, WebSocket
from fastapi.responses import StreamingResponse
from pydantic import BaseModel
from typing import Optional
import io
import os
import json

from ..aws_client import AWSVoiceClient
from ..streaming_stt import stream_transcribe

router = APIRouter(prefix="/api/v2/voice", tags=["voice"])
aws_client = AWSVoiceClient()

class TTSRequest(BaseModel):
    text: str
    voice_id: str = "Seoyeon"

@router.post("/tts")
async def text_to_speech(request: TTSRequest):
    """텍스트를 음성으로 변환 (TTS)"""
    try:
        audio_data = aws_client.text_to_speech(request.text, request.voice_id)
        return StreamingResponse(
            io.BytesIO(audio_data),
            media_type="audio/mpeg",
            headers={"Content-Disposition": "attachment; filename=speech.mp3"}
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"TTS 실패: {str(e)}")

@router.post("/stt")
async def speech_to_text(audio: UploadFile = File(...)):
    """음성을 텍스트로 변환 (STT) - 파일 업로드 방식"""
    import tempfile
    try:
        with tempfile.NamedTemporaryFile(delete=False, suffix=os.path.splitext(audio.filename)[1]) as temp_file:
            temp_path = temp_file.name
            temp_file.write(await audio.read())
        
        job_name = f"stt-{os.urandom(8).hex()}"
        # TODO: S3 업로드 및 AWS Transcribe 호출 구현 필요
        
        os.unlink(temp_path)
        return {"message": "STT 작업 시작", "job_name": job_name}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"STT 실패: {str(e)}")

@router.websocket("/stt/stream")
async def speech_to_text_stream(websocket: WebSocket):
    """실시간 음성 인식 (WebSocket)"""
    await websocket.accept()
    
    try:
        async def audio_generator():
            while True:
                data = await websocket.receive_bytes()
                if not data:
                    break
                yield data
        
        transcript = await stream_transcribe(audio_generator())
        await websocket.send_json({"transcript": transcript})
    except Exception as e:
        await websocket.send_json({"error": str(e)})
    finally:
        await websocket.close()
