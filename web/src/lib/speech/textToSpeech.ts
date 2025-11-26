import { awsTextToSpeech } from './awsSpeech'

// TTS (Text-to-Speech) 모듈 - AWS 우선, Web Speech API fallback
export class TextToSpeechService {
  private synthesis: SpeechSynthesis | null = null
  private voices: SpeechSynthesisVoice[] = []
  private currentUtterance: SpeechSynthesisUtterance | null = null
  private isSpeaking = false
  private currentAudio: HTMLAudioElement | null = null
  private useAWS = true

  constructor() {
    if (typeof window !== 'undefined' && 'speechSynthesis' in window) {
      this.synthesis = window.speechSynthesis
      this.loadVoices()
      
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

  public async speak(text: string, options?: {
    rate?: number
    pitch?: number
    volume?: number
    onStart?: () => void
    onEnd?: () => void
    onError?: (error: string) => void
  }) {
    this.stop()

    // AWS TTS 시도
    if (this.useAWS) {
      try {
        const audioBlob = await awsTextToSpeech.speak(text)
        const audioUrl = URL.createObjectURL(audioBlob)
        const audio = new Audio(audioUrl)
        
        audio.onplay = () => {
          this.isSpeaking = true
          options?.onStart?.()
        }
        
        audio.onended = () => {
          this.isSpeaking = false
          this.currentAudio = null
          URL.revokeObjectURL(audioUrl)
          options?.onEnd?.()
        }
        
        audio.onerror = () => {
          this.isSpeaking = false
          this.currentAudio = null
          URL.revokeObjectURL(audioUrl)
          // AWS 실패 시 fallback
          this.useAWS = false
          this.speakWithWebAPI(text, options)
        }
        
        this.currentAudio = audio
        audio.play()
        return
      } catch (error) {
        console.warn('AWS TTS 실패, Web Speech API로 fallback:', error)
        this.useAWS = false
      }
    }

    // Fallback: Web Speech API
    this.speakWithWebAPI(text, options)
  }

  private speakWithWebAPI(text: string, options?: {
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

    const utterance = new SpeechSynthesisUtterance(text)
    const voice = this.getKoreanVoice()
    
    if (voice) utterance.voice = voice
    
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
    // AWS Audio 중지
    if (this.currentAudio) {
      this.currentAudio.pause()
      this.currentAudio = null
    }
    
    // Web Speech API 중지
    if (this.synthesis && this.isSpeaking) {
      this.synthesis.cancel()
      this.currentUtterance = null
    }
    
    this.isSpeaking = false
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
