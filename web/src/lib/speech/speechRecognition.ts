// STT (Speech-to-Text) 모듈
export class SpeechRecognitionService {
  private recognition: SpeechRecognition | null = null
  private isListening = false
  private onResultCallback?: (text: string) => void
  private onInterimResultCallback?: (text: string) => void
  private onErrorCallback?: (error: string) => void
  private onStartCallback?: () => void
  private onEndCallback?: () => void

  constructor() {
    if (typeof window !== 'undefined') {
      const SpeechRecognition = window.SpeechRecognition || (window as any).webkitSpeechRecognition
      
      if (SpeechRecognition) {
        this.recognition = new SpeechRecognition()
        this.setupRecognition()
      }
    }
  }

  private setupRecognition() {
    if (!this.recognition) return

    this.recognition.continuous = true
    this.recognition.interimResults = true
    this.recognition.lang = 'ko-KR'

    this.recognition.onstart = () => {
      this.isListening = true
      this.onStartCallback?.()
    }

    this.recognition.onresult = (event) => {
      let interimTranscript = ''
      let finalTranscript = ''
      
      for (let i = event.resultIndex; i < event.results.length; i++) {
        const transcript = event.results[i][0].transcript
        if (event.results[i].isFinal) {
          finalTranscript += transcript
        } else {
          interimTranscript += transcript
        }
      }

      // 중간 결과 전달
      if (interimTranscript) {
        this.onInterimResultCallback?.(interimTranscript.trim())
      }

      // 최종 결과 전달
      if (finalTranscript) {
        this.onResultCallback?.(finalTranscript.trim())
      }
    }

    this.recognition.onerror = (event) => {
      this.isListening = false
      this.onErrorCallback?.(event.error)
    }

    this.recognition.onend = () => {
      this.isListening = false
      this.onEndCallback?.()
    }
  }

  public startListening() {
    if (!this.recognition) {
      this.onErrorCallback?.('음성 인식이 지원되지 않는 브라우저입니다.')
      return
    }

    if (this.isListening) return

    try {
      this.recognition.start()
    } catch (error) {
      this.onErrorCallback?.('음성 인식을 시작할 수 없습니다.')
    }
  }

  public stopListening() {
    if (this.recognition && this.isListening) {
      this.recognition.stop()
    }
  }

  public onResult(callback: (text: string) => void) {
    this.onResultCallback = callback
  }

  public onInterimResult(callback: (text: string) => void) {
    this.onInterimResultCallback = callback
  }

  public onError(callback: (error: string) => void) {
    this.onErrorCallback = callback
  }

  public onStart(callback: () => void) {
    this.onStartCallback = callback
  }

  public onEnd(callback: () => void) {
    this.onEndCallback = callback
  }

  public get listening() {
    return this.isListening
  }

  public get supported() {
    return !!this.recognition
  }
}

// 싱글톤 인스턴스
export const speechRecognition = new SpeechRecognitionService()
