import { speechApi } from '@/lib/api/speech'

// STT (Speech-to-Text) 모듈 - 서버 API 우선, 실패 시 브라우저 API 폴백
export class SpeechRecognitionService {
  private mediaRecorder: MediaRecorder | null = null
  private audioChunks: Blob[] = []
  private recognition: SpeechRecognition | null = null
  private useServerAPI = true
  private isListening = false
  private onResultCallback?: (text: string) => void
  private onInterimResultCallback?: (text: string) => void
  private onErrorCallback?: (error: string) => void
  private onStartCallback?: () => void
  private onEndCallback?: () => void

  constructor() {
    // 브라우저 Web Speech API 초기화 (폴백용)
    if (typeof window !== 'undefined') {
      const SpeechRecognition = window.SpeechRecognition || (window as any).webkitSpeechRecognition
      if (SpeechRecognition) {
        this.recognition = new SpeechRecognition()
        this.setupBrowserRecognition()
      }
    }
  }

  private setupBrowserRecognition() {
    if (!this.recognition) return

    this.recognition.continuous = true
    this.recognition.interimResults = true
    this.recognition.lang = 'ko-KR'

    this.recognition.onstart = () => {
      console.log('Browser recognition started')
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

      if (interimTranscript) {
        this.onInterimResultCallback?.(interimTranscript.trim())
      }

      if (finalTranscript) {
        this.onResultCallback?.(finalTranscript.trim())
      }
    }

    this.recognition.onerror = (event) => {
      console.error('Browser recognition error:', event.error)
      this.isListening = false
      this.onErrorCallback?.('음성 인식 오류: ' + event.error)
    }

    this.recognition.onend = () => {
      console.log('Browser recognition ended')
      this.isListening = false
      this.onEndCallback?.()
    }
  }

  private async setupMediaRecorder(stream: MediaStream) {
    this.audioChunks = []
    this.mediaRecorder = new MediaRecorder(stream, {
      mimeType: 'audio/webm'
    })

    this.mediaRecorder.ondataavailable = (event) => {
      if (event.data.size > 0) {
        this.audioChunks.push(event.data)
      }
    }

    this.mediaRecorder.onstop = async () => {
      console.log('MediaRecorder stopped')
      const audioBlob = new Blob(this.audioChunks, { type: 'audio/webm' })
      
      try {
        const result = await speechApi.speechToText(audioBlob)
        if (result.text) {
          this.onResultCallback?.(result.text)
        }
      } catch (error: any) {
        console.error('STT API error:', error)
        
        // 서버 API 실패 시 브라우저 API로 전환
        console.log('서버 STT API 실패, 브라우저 API로 전환')
        this.useServerAPI = false
        // 에러 메시지 표시 안함 (자동 폴백)
      } finally {
        this.isListening = false
        this.onEndCallback?.()
      }
    }
  }

  public async startListening() {
    console.log('speechRecognition.startListening called, useServerAPI:', this.useServerAPI)
    
    if (this.isListening) {
      console.log('이미 녹음 중')
      return
    }

    // 브라우저 API 사용
    if (!this.useServerAPI && this.recognition) {
      try {
        this.recognition.start()
      } catch (error) {
        console.error('Browser recognition start failed:', error)
      }
      return
    }

    // 서버 API 사용
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
      await this.setupMediaRecorder(stream)
      
      this.mediaRecorder?.start()
      this.isListening = true
      this.onStartCallback?.()
      console.log('녹음 시작')
    } catch (error) {
      console.error('마이크 접근 실패:', error)
      this.onErrorCallback?.('마이크 권한이 거부되었습니다.')
    }
  }

  public stopListening() {
    if (!this.useServerAPI && this.recognition) {
      this.recognition.stop()
      return
    }
    
    if (this.mediaRecorder && this.isListening) {
      this.mediaRecorder.stop()
      this.mediaRecorder.stream.getTracks().forEach(track => track.stop())
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
    return !!(navigator.mediaDevices && navigator.mediaDevices.getUserMedia)
  }
}

// 싱글톤 인스턴스
export const speechRecognition = new SpeechRecognitionService()
