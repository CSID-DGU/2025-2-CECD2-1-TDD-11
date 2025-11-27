import { useState, useEffect, useCallback } from 'react'
import { speechRecognition } from '@/lib/speech/speechRecognition'
import { textToSpeech } from '@/lib/speech/textToSpeech'

export function useSpeechRecognition() {
  const [isListening, setIsListening] = useState(false)
  const [transcript, setTranscript] = useState('')
  const [interimTranscript, setInterimTranscript] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [isSupported, setIsSupported] = useState(false)

  useEffect(() => {
    setIsSupported(speechRecognition.supported)

    speechRecognition.onStart(() => {
      setIsListening(true)
      setError(null)
    })

    speechRecognition.onEnd(() => {
      setIsListening(false)
      // interimTranscript 초기화 제거 - 텍스트 보존
    })

    speechRecognition.onResult(text => {
      setTranscript(prev => (prev ? prev + ' ' + text : text))
      setInterimTranscript('')
    })

    speechRecognition.onInterimResult(text => {
      setInterimTranscript(text)
    })

    speechRecognition.onError(error => {
      console.error('Speech recognition error:', error)
      setError(error)
      setIsListening(false)
      setInterimTranscript('')
      
      // Alert 표시
      if (typeof window !== 'undefined' && error) {
        alert(error)
      }
    })
  }, [])

  const startListening = useCallback(() => {
    console.log('startListening called, supported:', speechRecognition.supported)
    setTranscript('')
    setInterimTranscript('')
    setError(null)
    speechRecognition.startListening()
  }, [])

  const stopListening = useCallback(() => {
    speechRecognition.stopListening()
  }, [])

  const resetTranscript = useCallback(() => {
    setTranscript('')
    setInterimTranscript('')
    setError(null)
  }, [])

  return {
    isListening,
    transcript,
    interimTranscript,
    error,
    isSupported,
    startListening,
    stopListening,
    resetTranscript,
  }
}

export function useTextToSpeech() {
  const [isSpeaking, setIsSpeaking] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [isSupported, setIsSupported] = useState(false)

  useEffect(() => {
    setIsSupported(textToSpeech.supported)
  }, [])

  const speak = useCallback(
    (
      text: string,
      options?: {
        rate?: number
        pitch?: number
        volume?: number
      }
    ) => {
      setError(null)

      textToSpeech.speak(text, {
        ...options,
        onStart: () => setIsSpeaking(true),
        onEnd: () => setIsSpeaking(false),
        onError: error => {
          setError(error)
          setIsSpeaking(false)
        },
      })
    },
    []
  )

  const stop = useCallback(() => {
    textToSpeech.stop()
    setIsSpeaking(false)
  }, [])

  const pause = useCallback(() => {
    textToSpeech.pause()
  }, [])

  const resume = useCallback(() => {
    textToSpeech.resume()
  }, [])

  return {
    isSpeaking,
    error,
    isSupported,
    speak,
    stop,
    pause,
    resume,
  }
}
