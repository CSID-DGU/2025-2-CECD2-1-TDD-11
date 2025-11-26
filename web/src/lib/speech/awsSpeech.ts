// AWS TTS/STT API 클라이언트
const API_BASE_URL = import.meta.env.VITE_AI_API_URL || 'http://localhost:3000'

export class AWSTextToSpeechService {
  async speak(text: string, voiceId: string = 'Seoyeon'): Promise<Blob> {
    const response = await fetch(`${API_BASE_URL}/voice/tts`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ text, voice_id: voiceId })
    })
    
    if (!response.ok) throw new Error('TTS 요청 실패')
    return await response.blob()
  }
}

export class AWSSpeechRecognitionService {
  private ws: WebSocket | null = null
  private onResultCallback?: (text: string) => void
  private onErrorCallback?: (error: string) => void

  async startStreaming() {
    const wsUrl = API_BASE_URL.replace('http', 'ws')
    this.ws = new WebSocket(`${wsUrl}/voice/stt/stream`)
    
    this.ws.onmessage = (event) => {
      const data = JSON.parse(event.data)
      if (data.transcript) {
        this.onResultCallback?.(data.transcript)
      } else if (data.error) {
        this.onErrorCallback?.(data.error)
      }
    }
    
    this.ws.onerror = () => {
      this.onErrorCallback?.('WebSocket 연결 실패')
    }
  }

  sendAudio(audioData: Blob) {
    if (this.ws?.readyState === WebSocket.OPEN) {
      this.ws.send(audioData)
    }
  }

  stop() {
    this.ws?.close()
    this.ws = null
  }

  onResult(callback: (text: string) => void) {
    this.onResultCallback = callback
  }

  onError(callback: (error: string) => void) {
    this.onErrorCallback = callback
  }
}

export const awsTextToSpeech = new AWSTextToSpeechService()
export const awsSpeechRecognition = new AWSSpeechRecognitionService()
