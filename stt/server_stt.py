import json
import asyncio
import numpy as np
from fastapi import FastAPI, WebSocket, WebSocketDisconnect
from faster_whisper import WhisperModel

app = FastAPI()

SAMPLE_RATE = 16000
BYTES_PER_SAMPLE = 2

# UI 반응 속도
INFER_INTERVAL_SEC = 0.4       # 0.4초마다 추론 시도
MIN_AUDIO_SEC = 0.8            # 최소 0.8초 쌓이면 추론
CONTEXT_SEC = 2.5              # 최근 2.5초만 유지

MIN_AUDIO_BYTES = int(SAMPLE_RATE * BYTES_PER_SAMPLE * MIN_AUDIO_SEC)
MAX_BUFFER_BYTES = int(SAMPLE_RATE * BYTES_PER_SAMPLE * CONTEXT_SEC)

model = WhisperModel("base", compute_type="int8")

def has_enough_volume(audio_np: np.ndarray, threshold: float = 0.01) -> bool:
    rms = np.sqrt(np.mean(audio_np ** 2))
    return rms > threshold

def is_bad_repetition(text: str) -> bool:
    cleaned = text.strip()
    if not cleaned:
        return True

    tokens = cleaned.split()

    # 같은 단어 반복
    if len(tokens) >= 3 and len(set(tokens)) == 1:
        return True

    # 같은 1~2글자 패턴 반복
    no_space = cleaned.replace(" ", "")
    if len(no_space) >= 4:
        if no_space == no_space[0] * len(no_space):
            return True
        if len(no_space) % 2 == 0 and no_space == no_space[:2] * (len(no_space) // 2):
            return True

    return False

def transcribe_pcm16(audio_bytes: bytes) -> str:
    if not audio_bytes:
        return ""
    
    audio_np = np.frombuffer(audio_bytes, dtype=np.int16).astype(np.float32) / 32768.0

    if not has_enough_volume(audio_np):
        return ""

    segments, _ = model.transcribe(
        audio_np,
        language="ko",
        vad_filter=False,              # 짧은 발화 잘림 방지
        beam_size=1,                   # 속도 우선
        best_of=1,
        temperature=0.0,
        condition_on_previous_text=False
    )

    text = "".join(seg.text for seg in segments).strip()

    if is_bad_repetition(text):
        return ""
    
    return text


@app.websocket("/stt")
async def websocket_endpoint(ws: WebSocket):
    await ws.accept()

    audio_buffer = bytearray()
    lock = asyncio.Lock()
    disconnected = False
    last_sent_text = ""

    async def receiver():
        nonlocal disconnected, audio_buffer
        try:
            while True:
                data = await ws.receive_bytes()
                async with lock:
                    audio_buffer.extend(data)

                    # 최근 문맥만 유지
                    if len(audio_buffer) > MAX_BUFFER_BYTES:
                        audio_buffer = audio_buffer[-MAX_BUFFER_BYTES:]
        except WebSocketDisconnect:
            disconnected = True
        except Exception:
            disconnected = True

    async def infer_loop():
        nonlocal disconnected, last_sent_text, audio_buffer

        while not disconnected:
            await asyncio.sleep(INFER_INTERVAL_SEC)

            async with lock:
                if len(audio_buffer) < MIN_AUDIO_BYTES:
                    continue
                snapshot = bytes(audio_buffer)

            text = transcribe_pcm16(snapshot)

            # 같은 partial 반복 전송 방지
            if text and text != last_sent_text:
                last_sent_text = text
                try:
                    await ws.send_text(json.dumps({
                        "type": "partial",
                        "text": text
                    }))
                    print("[stt] PARTIAL:", text)
                except Exception:
                    disconnected = True
                    break

    receiver_task = asyncio.create_task(receiver())
    infer_task = asyncio.create_task(infer_loop())

    try:
        await receiver_task
    finally:
        disconnected = True
        infer_task.cancel()

        # 종료 시 마지막 오디오 final 처리
        try:
            async with lock:
                remaining = bytes(audio_buffer)

            if len(remaining) >= int(SAMPLE_RATE * BYTES_PER_SAMPLE * 0.3):
                final_text = transcribe_pcm16(remaining)
                if final_text:
                    await ws.send_text(json.dumps({
                        "type": "final",
                        "text": final_text
                    }))
                    print("[stt] FINAL:", final_text)
        except Exception:
            pass

        print("client disconnected")
