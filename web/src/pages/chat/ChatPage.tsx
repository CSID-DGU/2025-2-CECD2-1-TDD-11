import { useState, useEffect, useRef } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Bot, Loader2 } from 'lucide-react'

import Button from '@/components/common/Button'
import ChatMessage from '@/components/chat/ChatMessage'
import { interviewApi } from '@/lib/api/interview'
import { autobiographyApi } from '@/lib/api/autobiography'
import { useAlertStore } from '@/store/alert.store'
import { useAutobiographyStore } from '@/store/autobiography.store'
import { useInterviewStore } from '@/store/interview.store'
import { useVoiceChat } from '@/lib/utils/speechUtils'
import { useNavigation } from '@/components/layout/Layout'

export default function ChatPage() {
  const { showAlert } = useAlertStore()
  const { getAutobiographyId, getStatus, getCategories } =
    useAutobiographyStore()
  const { getInterviewId } = useInterviewStore()
  const queryClient = useQueryClient()
  const messagesEndRef = useRef<HTMLDivElement>(null)
  const [message, setMessage] = useState('')
  const { showNavigation } = useNavigation()

  // 음성 채팅 기능
  const { recording } = useVoiceChat()

  // store에서 데이터 가져오기
  const autobiographyId = getAutobiographyId()
  const status = getStatus()
  const categories = getCategories()
  const interviewId = getInterviewId()

  // 채팅 가능 여부 확인
  const isChatDisabled = status === 'CREATING' || status === 'FINISH'

  // 인터뷰 대화 목록 조회 (interviewId 사용)
  const { data: conversations, isLoading: conversationsLoading } = useQuery({
    queryKey: ['interview-conversations', interviewId],
    queryFn: () =>
      interviewApi.getInterviewConversations(Number(interviewId), 0, 100),
    enabled: !!interviewId,
  })

  // 인터뷰 시작 mutation (status가 EMPTY일 때)
  const startInterviewMutation = useMutation({
    mutationFn: (data: { preferred_categories: number[] }) =>
      interviewApi.startInterview(Number(autobiographyId), data),
    onSuccess: data => {
      queryClient.invalidateQueries({ queryKey: ['interview-conversations'] })
      // 자서전 진행률 업데이트
      autobiographyApi.getAutobiographyProgress().catch(() => {})
    },
    onError: error => showAlert('인터뷰 시작에 실패했습니다.', 'error'),
  })

  // 채팅 mutation (status가 PROGRESSING일 때)
  const chatMutation = useMutation({
    mutationFn: (data: { answer_text: string }) =>
      interviewApi.chatInterview(Number(autobiographyId), data),
    onSuccess: data => {
      queryClient.invalidateQueries({ queryKey: ['interview-conversations'] })
    },
    onError: error => showAlert('메시지 전송에 실패했습니다.', 'error'),
  })

  // status가 EMPTY일 때 자동으로 인터뷰 시작
  useEffect(() => {
    if (status === 'EMPTY' && categories && categories.length > 0) {
      startInterviewMutation.mutate({ preferred_categories: categories })
    }
  }, [status, categories])

  // 메시지 전송
  const handleSendMessage = (messageToSend?: string) => {
    const actualMessage = messageToSend || message
    console.log('handleSendMessage:', {
      messageToSend,
      message,
      actualMessage,
      trimmed: actualMessage.trim(),
      isChatDisabled,
      status
    })
    
    if (!actualMessage.trim() || isChatDisabled) return

    if (status === 'PROGRESSING') {
      console.log('Calling chatMutation.mutate with:', { answer_text: actualMessage })
      chatMutation.mutate({ answer_text: actualMessage })
    }
    setMessage('')
  }

  // 음성 메시지 전송
  const handleSendVoiceMessage = () => {
    const voiceMessage = recording.actions.confirmMessage()
    console.log('handleSendVoiceMessage:', {
      voiceMessage,
      trimmed: voiceMessage.trim(),
      isChatDisabled,
      status
    })
    
    if (voiceMessage.trim() && !isChatDisabled) {
      handleSendMessage(voiceMessage)
    }
  }

  const isLoading = startInterviewMutation.isPending || chatMutation.isPending

  return (
    <div className="flex flex-col h-full bg-bg-white">
      {/* 메시지 영역 */}
      <div className="flex-1 overflow-y-auto px-4 tablet:px-6 py-4">
        <div className="max-w-4xl mx-auto space-y-4">
          {/* 채팅 불가능 상태 안내 */}
          {isChatDisabled && (
            <div className="text-center py-12">
              <Bot size={48} className="mx-auto text-gray-300 mb-4" />
              <h2 className="text-subheading-18 text-gray-900 mb-2 font-sans">
                {status === 'CREATING'
                  ? '자서전 생성 중...'
                  : '인터뷰가 완료되었습니다'}
              </h2>
              <p className="text-body-18-regular text-gray-600 font-sans">
                {status === 'CREATING'
                  ? '자서전을 생성하고 있어 인터뷰를 진행할 수 없습니다.'
                  : '인터뷰가 완료되어 더 이상 대화할 수 없습니다.'}
              </p>
            </div>
          )}

          {/* 로딩 상태 */}
          {conversationsLoading && (
            <div className="flex justify-center py-8">
              <Loader2 className="animate-spin" size={32} />
            </div>
          )}

          {/* 대화 목록 */}
          {conversations?.results?.map((conversation, index) => (
            <ChatMessage
              key={index}
              message={conversation.content}
              isBot={conversation.conversationType === 'BOT'}
              timestamp={conversation.createdAt}
            />
          ))}

          {/* AI 응답 로딩 중 */}
          {chatMutation.isPending && (
            <ChatMessage
              message="생각 중입니다.."
              isBot={true}
              isLoading={true}
            />
          )}

          {/* 실시간 음성 인식 표시 */}
          {recording.state.isListening && (
            <div className="flex justify-end">
              <div className="max-w-[80%] bg-point-1 text-white px-4 py-3 rounded-2xl">
                <p className="text-body-18-regular">
                  {recording.state.transcript +
                    ' ' +
                    recording.state.interimTranscript}
                </p>
                <div className="flex items-center gap-2 mt-2">
                  <div className="w-2 h-2 bg-white rounded-full animate-pulse"></div>
                  <span className="text-caption-12">말하는 중...</span>
                </div>
              </div>
            </div>
          )}

          {/* 음성 인식 완료 확인 UI */}
          {recording.state.showConfirmation && recording.state.transcript && !isChatDisabled && (
            <div className="flex justify-end">
              <div className="max-w-[80%] bg-point-1 text-white px-4 py-3 rounded-2xl">
                <p className="text-body-18-regular">
                  {recording.state.transcript}
                </p>
              </div>
            </div>
          )}

          <div ref={messagesEndRef} />
        </div>
      </div>

      {/* 하단 통합 버튼 */}
      <div
        className={`px-6 py-6 transition-all duration-200 ${
          showNavigation ? 'mb-16' : 'mb-0'
        }`}
      >
        <div className="max-w-4xl mx-auto">
          {recording.state.showConfirmation && recording.state.transcript && !isChatDisabled ? (
            <div className="flex gap-3">
              <Button
                variant="blue"
                size="lg"
                onClick={handleSendVoiceMessage}
                className="flex-1"
              >
                답장 전송하기
              </Button>
              <Button
                variant="outline"
                size="lg"
                onClick={recording.actions.retryRecording}
                className="flex-1"
              >
                다시 녹음하기
              </Button>
            </div>
          ) : (
            <Button
              variant={recording.state.isListening ? 'red' : 'blue'}
              size="lg"
              onClick={
                recording.state.isListening
                  ? recording.actions.stopRecording
                  : recording.actions.startRecording
              }
              disabled={isLoading || isChatDisabled}
              className="w-full"
            >
              {isChatDisabled
                ? status === 'CREATING'
                  ? '자서전 생성 중...'
                  : '인터뷰 완료'
                : recording.state.isListening
                  ? '녹음 중지하기'
                  : '녹음 시작하기'}
            </Button>
          )}
        </div>
      </div>
    </div>
  )
}
