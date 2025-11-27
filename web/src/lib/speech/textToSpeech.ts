import { speechApi } from '@/lib/api/speech'

// TTS (Text-to-Speech) 모듈 - 서버 API 사용
export class TextToSpeechService {
  private currentAudio: HTMLAudioElement | null = null
  private isSpeaking = false

  constructor() {}



  public async speak(text: string, options?: {
    rate?: number
    pitch?: number
    volume?: number
    onStart?: () => void
    onEnd?: () => void
    onError?: (error: string) => void
  }) {
    // 이전 음성 중지
    this.stop()

    try {
      this.isSpeaking = true
      options?.onStart?.()

      const response = await speechApi.textToSpeech(text)
      
      // base64 오디오를 Audio 요소로 재생
      this.currentAudio = new Audio(`data:audio/mpeg;base64,${response.audio}`)
      this.currentAudio.volume = options?.volume || 1.0
      
      this.currentAudio.onended = () => {
        this.isSpeaking = false
        this.currentAudio = null
        options?.onEnd?.()
      }

      this.currentAudio.onerror = () => {
        this.isSpeaking = false
        this.currentAudio = null
        options?.onError?.('음성 재생에 실패했습니다.')
      }

      await this.currentAudio.play()
    } catch (error) {
      console.error('TTS error:', error)
      this.isSpeaking = false
      options?.onError?.('음성 합성에 실패했습니다.')
    }
  }

  public stop() {
    if (this.currentAudio) {
      this.currentAudio.pause()
      this.currentAudio = null
      this.isSpeaking = false
    }
  }

  public pause() {
    if (this.currentAudio) {
      this.currentAudio.pause()
    }
  }

  public resume() {
    if (this.currentAudio) {
      this.currentAudio.play()
    }
  }

  public get speaking() {
    return this.isSpeaking
  }

  public get supported() {
    return true
  }
}

// 싱글톤 인스턴스
export const textToSpeech = new TextToSpeechService()
