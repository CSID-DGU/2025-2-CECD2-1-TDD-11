// TTS (Text-to-Speech) 모듈
export class TextToSpeechService {
  private synthesis: SpeechSynthesis | null = null
  private voices: SpeechSynthesisVoice[] = []
  private currentUtterance: SpeechSynthesisUtterance | null = null
  private isSpeaking = false

  constructor() {
    if (typeof window !== 'undefined' && 'speechSynthesis' in window) {
      this.synthesis = window.speechSynthesis
      this.loadVoices()
      
      // 음성 목록이 로드될 때까지 기다림
      if (this.synthesis.onvoiceschanged !== undefined) {
        this.synthesis.onvoiceschanged = () => this.loadVoices()
      }
    }
  }

  private loadVoices() {
    if (!this.synthesis) return
    
    this.voices = this.synthesis.getVoices()
  }

  private getKoreanVoice(): SpeechSynthesisVoice | null {
    // 한국어 음성 우선 선택
    const koreanVoice = this.voices.find(voice => 
      voice.lang.startsWith('ko') || voice.name.includes('Korean')
    )
    
    return koreanVoice || this.voices[0] || null
  }

  public speak(text: string, options?: {
    rate?: number
    pitch?: number
    volume?: number
    onStart?: () => void
    onEnd?: () => void
    onError?: (error: string) => void
  }) {
    if (!this.synthesis) {
      options?.onError?.('음성 합성이 지원되지 않는 브라우저입니다.')
      return
    }

    // 이전 음성 중지
    this.stop()

    const utterance = new SpeechSynthesisUtterance(text)
    const voice = this.getKoreanVoice()
    
    if (voice) {
      utterance.voice = voice
    }
    
    utterance.rate = options?.rate || 1.0
    utterance.pitch = options?.pitch || 1.0
    utterance.volume = options?.volume || 1.0
    utterance.lang = 'ko-KR'

    utterance.onstart = () => {
      this.isSpeaking = true
      options?.onStart?.()
    }

    utterance.onend = () => {
      this.isSpeaking = false
      this.currentUtterance = null
      options?.onEnd?.()
    }

    utterance.onerror = (event) => {
      this.isSpeaking = false
      this.currentUtterance = null
      options?.onError?.(event.error)
    }

    this.currentUtterance = utterance
    this.synthesis.speak(utterance)
  }

  public stop() {
    if (this.synthesis && this.isSpeaking) {
      this.synthesis.cancel()
      this.isSpeaking = false
      this.currentUtterance = null
    }
  }

  public pause() {
    if (this.synthesis && this.isSpeaking) {
      this.synthesis.pause()
    }
  }

  public resume() {
    if (this.synthesis) {
      this.synthesis.resume()
    }
  }

  public get speaking() {
    return this.isSpeaking
  }

  public get supported() {
    return !!this.synthesis
  }

  public get availableVoices() {
    return this.voices
  }
}

// 싱글톤 인스턴스
export const textToSpeech = new TextToSpeechService()
