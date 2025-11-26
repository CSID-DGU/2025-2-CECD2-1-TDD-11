import { useParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { ArrowLeft } from 'lucide-react'
import { useNavigate } from 'react-router-dom'

import Button from '@/components/common/Button'
import LoadingSpinner from '@/components/common/LoadingSpinner'
import ChatMessage from '@/components/chat/ChatMessage'
import { interviewApi } from '@/lib/api/interview'

const InterviewReviewPage = () => {
  const { interviewId } = useParams<{ interviewId: string }>()
  const navigate = useNavigate()

  console.log('InterviewReviewPage interviewId:', interviewId, 'Number(interviewId):', Number(interviewId))

  // 인터뷰 대화 기록 조회
  const { data: conversations, isLoading } = useQuery({
    queryKey: ['interview-conversations', interviewId],
    queryFn: () => interviewApi.getInterviewConversations(Number(interviewId), 0, 100),
    enabled: !!interviewId
  })

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <LoadingSpinner size={32} className="mx-auto mb-4" />
          <p className="text-gray-500">인터뷰 기록을 불러오는 중...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* 채팅 기록 */}
      <div className="max-w-4xl mx-auto p-4">
        {conversations?.results && conversations.results.length > 0 ? (
          <div className="space-y-4">
            {conversations.results.map((conversation) => (
              <ChatMessage
                key={conversation.conversationId}
                message={conversation.content}
                isBot={conversation.conversationType === 'BOT'}
                timestamp={conversation.createdAt}
              />
            ))}
          </div>
        ) : (
          <div className="text-center py-12">
            <p className="text-gray-500">인터뷰 기록이 없습니다.</p>
          </div>
        )}
      </div>
    </div>
  )
}

export default InterviewReviewPage
