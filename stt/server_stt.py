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

# 튜닝 포인트
INFER_INTERVAL_SEC = 0.4
MIN_AUDIO_SEC = 0.8
CONTEXT_SEC = 4.0
SILENCE_FINAL_SEC = 1.3

MIN_AUDIO_BYTES = int(SAMPLE_RATE * BYTES_PER_SAMPLE * MIN_AUDIO_SEC)
MAX_BUFFER_BYTES = int(SAMPLE_RATE * BYTES_PER_SAMPLE * CONTEXT_SEC)

model = WhisperModel("base", compute_type="int8")
vad = webrtcvad.Vad(2)


def normalize_text(text: str) -> str:
    return re.sub(r"\s+", " ", text).strip()


def join_text(left: str, right: str) -> str:
    left = normalize_text(left)
    right = normalize_text(right)

    if not left:
        return right
    if not right:
        return left
    return f"{left} {right}"


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
    t = normalize_text(text)
    if not t:
        return True

    # 너무 짧은 감탄사/잡음성 결과 제거
    if t in {"아", "어", "음", "응", "오", "네"}:
        return True

    tokens = t.split()

    # 같은 단어 반복
    if len(tokens) >= 2 and len(set(tokens)) == 1:
        return True

    # 같은 절반 반복
    if len(tokens) >= 4:
        half = len(tokens) // 2
        if len(tokens) % 2 == 0 and tokens[:half] == tokens[half:]:
            return True

    # 같은 글자 반복
    ns = re.sub(r"\s+", "", t)
    if len(ns) >= 3 and len(set(ns)) == 1:
        return True

    return False


def common_prefix_len(a: str, b: str) -> int:
    n = min(len(a), len(b))
    i = 0
    while i < n and a[i] == b[i]:
        i += 1
    return i

# 현재 발화 후보를 교체 방식으로 partial 갱신
def choose_better_partial(prev_text: str, new_text: str) -> str:
    prev_text = normalize_text(prev_text)
    new_text = normalize_text(new_text)

    if not new_text:
        return prev_text
    if not prev_text:
        return new_text

    # 가장 안전한 경우: 확장
    if new_text.startswith(prev_text):
        return new_text

    prefix_len = common_prefix_len(prev_text, new_text)
    min_required = max(2, int(len(prev_text) * 0.7))

    # 공통 앞부분이 충분하고 새 값이 더 길면 확장으로 간주
    if prefix_len >= min_required and len(new_text) >= len(prev_text):
        return new_text

    # 새 값이 너무 짧고 이전 값보다 정보가 적으면 이전 유지
    if len(new_text) + 1 < len(prev_text):
        return prev_text

    # 완전 재해석이면 새 값으로 바꿀지, 이전 유지할지 선택
    return new_text


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

    text = normalize_text("".join(seg.text for seg in segments))

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

    # 이미 확정된 전체 텍스트
    committed_text = ""

    # 현재 발화 중의 partial 후보
    current_partial_text = ""

    # 마지막으로 보낸 partial 전체 문장
    last_sent_partial = ""

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

    async def send_partial(text: str):
        await ws.send_text(json.dumps({
            "type": "partial",
            "text": text
        }))
        print("[stt] PARTIAL:", text)

    async def send_final(text: str):
        await ws.send_text(json.dumps({
            "type": "final",
            "text": text
        }))
        print("[stt] FINAL:", text)

    async def finalize_current_utterance():
        nonlocal committed_text, current_partial_text, last_sent_partial, in_speech, audio_buffer

        final_text = join_text(committed_text, current_partial_text)

        if final_text and final_text != committed_text:
            await send_final(final_text)
            committed_text = final_text

        current_partial_text = ""
        last_sent_partial = committed_text
        in_speech = False

        async with lock:
            audio_buffer = bytearray()

    async def infer_loop():
        nonlocal disconnected
        nonlocal audio_buffer
        nonlocal committed_text
        nonlocal current_partial_text
        nonlocal last_sent_partial
        nonlocal last_speech_time
        nonlocal in_speech

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

                    current_partial_text = choose_better_partial(
                        current_partial_text,
                        text,
                    )

                    merged = join_text(committed_text, current_partial_text)

                    if merged and merged != last_sent_partial:
                        last_sent_partial = merged
                        try:
                            await send_partial(merged)
                        except Exception:
                            disconnected = True
                            return

            else:
                # 일정 시간 침묵이면 현재 발화 확정
                if in_speech and (time.monotonic() - last_speech_time) >= SILENCE_FINAL_SEC:
                    try:
                        await finalize_current_utterance()
                    except Exception:
                        disconnected = True
                        return

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

            if remaining:
                tail = transcribe_pcm16(remaining)
                if tail:
                    current_partial_text = choose_better_partial(current_partial_text, tail)

            final_text = join_text(committed_text, current_partial_text)
            if final_text:
                await send_final(final_text)
        except Exception:
            pass

        print("client disconnected")
