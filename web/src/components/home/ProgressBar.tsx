interface ProgressBarProps {
  progress: number
  status: 'EMPTY' | 'PROGRESSING' | 'ENOUGH' | 'CREATING' | 'FINISH'
}

export default function ProgressBar({ progress, status }: ProgressBarProps) {
  const getStatusInfo = () => {
    switch (status) {
      case 'EMPTY':
        return { color: 'bg-gray-400', text: '시작 전', textColor: 'text-gray-600' }
      case 'PROGRESSING':
        return { color: 'bg-warning-500', text: '인터뷰 진행중', textColor: 'text-warning-700' }
      case 'ENOUGH':
        return { color: 'bg-warning-500', text: '인터뷰 충분', textColor: 'text-warning-700' }
      case 'CREATING':
        return { color: 'bg-info-500', text: '자서전 생성중', textColor: 'text-info-700' }
      case 'FINISH':
        return { color: 'bg-success-500', text: '완성', textColor: 'text-success-700' }
      default:
        return { color: 'bg-gray-400', text: '알 수 없음', textColor: 'text-gray-600' }
    }
  }

  const statusInfo = getStatusInfo()

  return (
    <div className="space-y-3">
      {/* 진행률 바 */}
      <div className="relative">
        <div className="w-full bg-gray-200 rounded-full h-3 tablet:h-4">
          <div 
            className={`h-full rounded-full transition-all duration-500 ease-out ${statusInfo.color}`}
            style={{ width: `${Math.min(progress, 100)}%` }}
          />
        </div>
      </div>

      {/* 상태 정보 */}
      <div className="flex items-center justify-between">
        {status === 'PROGRESSING' && (
          <span className="text-caption-14 text-gray-500 font-sans">
            계속해서 인터뷰를 진행해보세요
          </span>
        )}

        {status === 'ENOUGH' && (
          <span className="text-caption-14 text-gray-500 font-sans">
            인터뷰가 충분히 진행되었습니다
          </span>
        )}
        
        {status === 'CREATING' && (
          <span className="text-caption-14 text-gray-500 font-sans">
            AI가 자서전을 작성하고 있습니다
          </span>
        )}
        
        {status === 'FINISH' && (
          <span className="text-caption-14 text-gray-500 font-sans">
            자서전이 완성되었습니다!
          </span>
        )}
      </div>
    </div>
  )
}
