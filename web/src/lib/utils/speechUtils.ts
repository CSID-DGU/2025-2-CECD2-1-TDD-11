import React, { useState, useCallback } from 'react'
import { useSpeechRecognition, useTextToSpeech } from '@/hooks/useSpeech'

export interface VoiceRecordingState {
  isListening: boolean
  transcript: string
  interimTranscript: string
  showConfirmation: boolean
  error: string | null
}

export interface VoiceRecordingActions {
  startRecording: () => void
  stopRecording: () => void
  confirmMessage: () => string
  retryRecording: () => void
  resetRecording: () => void
}

export interface VoicePlaybackState {
  isSpeaking: boolean
  autoTTS: boolean
  error: string | null
}

export interface VoicePlaybackActions {
  speak: (text: string) => void
  stopSpeaking: () => void
  toggleAutoTTS: () => void
}

/**
 * 음성 녹음 기능을 제공하는 훅
 */
export function useVoiceRecording(): [VoiceRecordingState, VoiceRecordingActions] {
  const {
    isListening,
    transcript,
    interimTranscript,
    error,
    startListening,
    stopListening,
    resetTranscript
  } = useSpeechRecognition()

  const [showConfirmation, setShowConfirmation] = useState(false)

  const startRecording = useCallback(() => {
    setShowConfirmation(false)
    resetTranscript()
    startListening()
  }, [startListening, resetTranscript])

  const stopRecording = useCallback(() => {
    stopListening()
    if (transcript) {
      setShowConfirmation(true)
    }
  }, [stopListening, transcript])

  const confirmMessage = useCallback(() => {
    const message = transcript
    setShowConfirmation(false)
    resetTranscript()
    return message
  }, [transcript, resetTranscript])

  const retryRecording = useCallback(() => {
    setShowConfirmation(false)
    resetTranscript()
    startListening()
  }, [startListening, resetTranscript])

  const resetRecording = useCallback(() => {
    setShowConfirmation(false)
    resetTranscript()
  }, [resetTranscript])

  // transcript가 있고 녹음이 끝났을 때 확인 UI 표시
  React.useEffect(() => {
    if (transcript && !isListening && !showConfirmation) {
      setShowConfirmation(true)
    }
  }, [transcript, isListening, showConfirmation])

  const state: VoiceRecordingState = {
    isListening,
    transcript,
    interimTranscript,
    showConfirmation,
    error
  }

  const actions: VoiceRecordingActions = {
    startRecording,
    stopRecording,
    confirmMessage,
    retryRecording,
    resetRecording
  }

  return [state, actions]
}

/**
 * 음성 재생 기능을 제공하는 훅
 */
export function useVoicePlayback(): [VoicePlaybackState, VoicePlaybackActions] {
  const { isSpeaking, speak: ttsSpeak, stop, error } = useTextToSpeech()
  const [autoTTS, setAutoTTS] = useState(true)

  const speak = useCallback((text: string) => {
    if (autoTTS) {
      ttsSpeak(text)
    }
  }, [autoTTS, ttsSpeak])

  const stopSpeaking = useCallback(() => {
    stop()
  }, [stop])

  const toggleAutoTTS = useCallback(() => {
    setAutoTTS(prev => !prev)
    if (isSpeaking) {
      stop()
    }
  }, [isSpeaking, stop])

  const state: VoicePlaybackState = {
    isSpeaking,
    autoTTS,
    error
  }

  const actions: VoicePlaybackActions = {
    speak,
    stopSpeaking,
    toggleAutoTTS
  }

  return [state, actions]
}

/**
 * 음성 녹음과 재생을 모두 제공하는 통합 훅
 */
export function useVoiceChat() {
  const [recordingState, recordingActions] = useVoiceRecording()
  const [playbackState, playbackActions] = useVoicePlayback()

  return {
    recording: { state: recordingState, actions: recordingActions },
    playback: { state: playbackState, actions: playbackActions }
  }
}
