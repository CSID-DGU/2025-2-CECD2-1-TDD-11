import { Bot, User, Volume2 } from 'lucide-react'
import LoadingSpinner from '@/components/common/LoadingSpinner'

interface ChatMessageProps {
  message: string
  isBot: boolean
  timestamp?: string
  isLoading?: boolean
  onSpeakClick?: () => void
  isSpeaking?: boolean
}

export default function ChatMessage({ 
  message, 
  isBot, 
  timestamp, 
  isLoading = false,
  onSpeakClick,
  isSpeaking = false
}: ChatMessageProps) {
  const formatTimestamp = (timestamp?: string) => {
    if (!timestamp) return ''
    
    const date = new Date(timestamp)
    const now = new Date()
    const diffInMinutes = Math.floor((now.getTime() - date.getTime()) / (1000 * 60))
    
    if (diffInMinutes < 1) return '방금 전'
    if (diffInMinutes < 60) return `${diffInMinutes}분 전`
    if (diffInMinutes < 1440) return `${Math.floor(diffInMinutes / 60)}시간 전`
    
    return date.toLocaleDateString('ko-KR', {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  return (
    <div className={`flex gap-3 ${isBot ? 'justify-start' : 'justify-end'}`}>
      <div className={`max-w-[80%] tablet:max-w-[70%] ${isBot ? 'order-2' : 'order-1'}`}>
        <div
          className={`px-4 py-3 rounded-2xl font-sans relative group ${
            isBot
              ? 'bg-white border border-gray-200 text-gray-900'
              : 'bg-point-1 text-white'
          }`}
        >
          {isLoading ? (
            <div className="flex items-center gap-2">
              <LoadingSpinner size={16} className="text-point-1" />
              <span className="text-body-18-regular">생각 중...</span>
            </div>
          ) : (
            <>
              <p className="text-body-18-regular whitespace-pre-wrap leading-relaxed">
                {message}
              </p>
              
              {/* TTS 버튼 (Bot 메시지에만 표시) */}
              {isBot && onSpeakClick && (
                <button
                  onClick={onSpeakClick}
                  className={`absolute top-2 right-2 p-1.5 rounded-full transition-all opacity-0 group-hover:opacity-100 ${
                    isSpeaking 
                      ? 'bg-pri-100 text-point-1' 
                      : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                  }`}
                  aria-label="음성으로 듣기"
                >
                  <Volume2 size={14} className={isSpeaking ? 'animate-pulse' : ''} />
                </button>
              )}
            </>
          )}
        </div>

        {timestamp && !isLoading && (
          <p className={`text-caption-12 text-gray-500 mt-1 font-sans ${
            isBot ? 'text-left' : 'text-right'
          }`}>
            {formatTimestamp(timestamp)}
          </p>
        )}
      </div>
    </div>
  )
}