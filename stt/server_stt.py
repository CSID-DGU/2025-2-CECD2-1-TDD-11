import json
import asyncio
import time
import re
import numpy as np
import webrtcvad

from fastapi import FastAPI, WebSocket, WebSocketDisconnect
from faster_whisper import WhisperModel

app = FastAPI()

SAMPLE_RATE = 16000
BYTES_PER_SAMPLE = 2

INFER_INTERVAL_SEC = 0.35
MIN_AUDIO_SEC = 0.8
CONTEXT_SEC = 2.5
SILENCE_FINAL_SEC = 1.0

MIN_AUDIO_BYTES = int(SAMPLE_RATE * BYTES_PER_SAMPLE * MIN_AUDIO_SEC)
MAX_BUFFER_BYTES = int(SAMPLE_RATE * BYTES_PER_SAMPLE * CONTEXT_SEC)

model = WhisperModel("base", compute_type="int8")
vad = webrtcvad.Vad(2)

def rms_energy(audio_np: np.ndarray) -> float:
    if len(audio_np) == 0:
        return 0.0
    return float(np.sqrt(np.mean(audio_np ** 2)))

def has_speech_pcm16(audio_bytes: bytes, sample_rate: int = 16000) -> bool:
    frame_ms = 30
    frame_bytes = int(sample_rate * 2 * frame_ms / 1000)

    if len(audio_bytes) < frame_bytes:
        return False

    voiced = 0
    total = 0

    for i in range(0, len(audio_bytes) - frame_bytes + 1, frame_bytes):
        frame = audio_bytes[i:i + frame_bytes]
        try:
            if vad.is_speech(frame, sample_rate):
                voiced += 1
        except Exception:
            pass
        total += 1

    if total == 0:
        return False

    return (voiced / total) >= 0.2

def is_hallucinated_text(text: str) -> bool:
    t = text.strip()
    if not t:
        return True

    if t in {"아", "어", "음", "응", "오", "네"}:
        return True

    tokens = t.split()

    if len(tokens) >= 2 and len(set(tokens)) == 1:
        return True

    if len(tokens) >= 4:
        half = len(tokens) // 2
        if len(tokens) % 2 == 0 and tokens[:half] == tokens[half:]:
            return True

    ns = re.sub(r"\s+", "", t)
    if len(ns) >= 3 and len(set(ns)) == 1:
        return True

    return False

def transcribe_pcm16(audio_bytes: bytes) -> str:
    if not audio_bytes:
        return ""

    audio_np = np.frombuffer(audio_bytes, dtype=np.int16).astype(np.float32) / 32768.0

    # 1차: 에너지 기반 무음 컷
    if rms_energy(audio_np) < 0.008:
        return ""

    # 2차: VAD 기반 음성 여부 체크
    if not has_speech_pcm16(audio_bytes, SAMPLE_RATE):
        return ""

    segments, _ = model.transcribe(
        audio_np,
        language="ko",
        vad_filter=False,
        beam_size=1,
        best_of=1,
        temperature=0.0,
        condition_on_previous_text=False,
    )

    text = "".join(seg.text for seg in segments).strip()

    # 3차: hallucination 텍스트 컷
    if is_hallucinated_text(text):
        return ""

    return text

@app.websocket("/stt")
async def websocket_endpoint(ws: WebSocket):
    await ws.accept()

    audio_buffer = bytearray()
    lock = asyncio.Lock()
    disconnected = False

    last_partial = ""
    last_speech_time = time.monotonic()
    in_speech = False

    async def receiver():
        nonlocal disconnected, audio_buffer
        try:
            while True:
                data = await ws.receive_bytes()
                async with lock:
                    audio_buffer.extend(data)
                    if len(audio_buffer) > MAX_BUFFER_BYTES:
                        audio_buffer = audio_buffer[-MAX_BUFFER_BYTES:]
        except WebSocketDisconnect:
            disconnected = True
        except Exception:
            disconnected = True

    async def infer_loop():
        nonlocal disconnected, audio_buffer, last_partial, last_speech_time, in_speech

        while not disconnected:
            await asyncio.sleep(INFER_INTERVAL_SEC)

            async with lock:
                snapshot = bytes(audio_buffer)

            if len(snapshot) < MIN_AUDIO_BYTES:
                continue

            audio_np = np.frombuffer(snapshot, dtype=np.int16).astype(np.float32) / 32768.0
            energy_ok = rms_energy(audio_np) >= 0.008
            speech_ok = has_speech_pcm16(snapshot, SAMPLE_RATE)

            if energy_ok and speech_ok:
                text = transcribe_pcm16(snapshot)
                if text:
                    last_speech_time = time.monotonic()
                    in_speech = True

                    if text != last_partial:
                        last_partial = text
                        try:
                            await ws.send_text(json.dumps({
                                "type": "partial",
                                "text": text
                            }))
                            print("[stt] PARTIAL:", text)
                        except Exception:
                            disconnected = True
                            return
            else:
                # 일정 시간 침묵이면 final 확정 후 버퍼 리셋
                if in_speech and (time.monotonic() - last_speech_time) >= SILENCE_FINAL_SEC:
                    if last_partial:
                        try:
                            await ws.send_text(json.dumps({
                                "type": "final",
                                "text": last_partial
                            }))
                            print("[stt] FINAL:", last_partial)
                        except Exception:
                            disconnected = True
                            return

                    async with lock:
                        audio_buffer = bytearray()

                    last_partial = ""
                    in_speech = False

    receiver_task = asyncio.create_task(receiver())
    infer_task = asyncio.create_task(infer_loop())

    try:
        await receiver_task
    finally:
        disconnected = True
        infer_task.cancel()

        try:
            async with lock:
                remaining = bytes(audio_buffer)

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
