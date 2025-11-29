import { useState, useEffect } from 'react'
import { useQuery } from '@tanstack/react-query'
import { MessageCircle } from 'lucide-react'
import { useNavigate } from 'react-router-dom'

import Button from '@/components/common/Button'
import ProgressBar from '@/components/home/ProgressBar'
import Calendar from '@/components/home/Calendar'
import { autobiographyApi } from '@/lib/api/autobiography'
import { interviewApi } from '@/lib/api/interview'
import { useAuthStore } from '@/store/auth.store'
import { useAutobiographyStore } from '@/store/autobiography.store'
import { useInterviewStore } from '@/store/interview.store'

export default function HomePage() {
  const navigate = useNavigate()
  const { isAuthenticated } = useAuthStore()
  const { setAutobiographyId, setStatus, setCategories } = useAutobiographyStore()
  const { setInterviewId } = useInterviewStore()
  const [selectedDate, setSelectedDate] = useState<string | null>(null)
  const [selectedInterviewId, setSelectedInterviewId] = useState<number | null>(
    null
  )

  // 컴포넌트 마운트 시 오늘 날짜로 초기화
  useEffect(() => {
    const today = new Date().toISOString().split('T')[0]
    setSelectedDate(today)
  }, [])

  // 현재 진행 중인 자서전 ID 조회
  const { data: currentAutobiography } = useQuery({
    queryKey: ['current-autobiography'],
    queryFn: autobiographyApi.getCurrentAutobiographyId,
    enabled: isAuthenticated(),
  })

  // 진행률 조회
  const { data: progress } = useQuery({
    queryKey: ['autobiography-progress', currentAutobiography?.autobiographyId],
    queryFn: () => autobiographyApi.getAutobiographyProgress(),
    enabled: !!currentAutobiography?.autobiographyId,
  })

  // 자서전 테마 조회
  const { data: theme } = useQuery({
    queryKey: ['autobiography-theme', currentAutobiography?.autobiographyId],
    queryFn: () => autobiographyApi.getAutobiographyTheme(),
    enabled: !!currentAutobiography?.autobiographyId,
  })

  // 자서전 ID를 store에 저장
  useEffect(() => {
    if (currentAutobiography?.autobiographyId) {
      setAutobiographyId(currentAutobiography.autobiographyId.toString())
    }
  }, [currentAutobiography, setAutobiographyId])

  // status를 store에 저장
  useEffect(() => {
    if (progress?.status) {
      setStatus(progress.status)
    }
  }, [progress, setStatus])

  // categories를 store에 저장
  useEffect(() => {
    if (theme?.categories) {
      setCategories(theme.categories)
    }
  }, [theme, setCategories])

  // 선택된 날짜의 인터뷰 요약 조회
  const { data: interviewSummary } = useQuery({
    queryKey: [
      'interview-summary',
      currentAutobiography?.autobiographyId,
      selectedDate,
    ],
    queryFn: () => {
      if (!selectedDate || !currentAutobiography?.autobiographyId) return null
      const date = new Date(selectedDate)
      return interviewApi.getInterviewSummaryByDate(
        currentAutobiography.autobiographyId,
        date.getFullYear().toString(),
        (date.getMonth() + 1).toString()
      )
    },
    enabled: !!selectedDate && !!currentAutobiography?.autobiographyId,
  })

  const handleDateSelect = (date: string, interviewId?: number) => {
    console.log('HomePage handleDateSelect:', { date, interviewId })
    setSelectedDate(date)
    setSelectedInterviewId(interviewId || null)
  }

  const getSelectedDateSummary = () => {
    if (!selectedDate || !interviewSummary?.interviews) return null
    return interviewSummary.interviews.find(
      interview => interview.date === selectedDate
    )
  }

  const selectedSummary = getSelectedDateSummary()

  // 오늘 날짜의 interview ID를 store에 저장
  useEffect(() => {
    const today = new Date().toISOString().split('T')[0]
    if (selectedDate === today && selectedSummary?.id) {
      setInterviewId(selectedSummary.id.toString())
    }
  }, [selectedDate, selectedSummary, setInterviewId])

  if (!isAuthenticated()) {
    return null
  }

  return (
    <div className="bg-bg-white mb-6">
      <div className="px-6 py-6 space-y-8">
        {/* 진행률 섹션 */}
        {progress && (
          <div>
            <div className="flex items-center justify-between mb-2">
              <h2 className="text-subheading-18 text-gray-900 font-sans">
                대화 진행률
              </h2>
              <span className="text-body-18-medium text-point-2 font-sans">
                자서전 완성까지 {progress.progressPercentage}%
              </span>
            </div>
            <ProgressBar
              progress={progress.progressPercentage}
              status={progress.status}
            />
          </div>
        )}

        {/* 캘린더 섹션 */}
        <div className="bg-white rounded-xl p-6">
          <Calendar onDateSelect={handleDateSelect} />

          {/* 선택된 날짜의 인터뷰 요약 */}
          {selectedDate && (
            <div className="mt-6 pt-6 border-t border-gray-200">
              <h3 className="text-subheading-18 text-gray-900 font-sans mb-4">
                {new Date(selectedDate).toLocaleDateString('ko-KR', {
                  year: 'numeric',
                  month: 'long',
                  day: 'numeric',
                })}{' '}
                인터뷰 기록
              </h3>

              {selectedSummary && selectedSummary.totalMessageCount > 0 ? (
                <div className="space-y-4">
                  <div className="bg-gray-50 rounded-lg p-4">
                    <div className="flex items-center justify-between mb-2">
                      <span className="text-caption-14 text-gray-600 font-sans">
                        총 {selectedSummary.totalMessageCount}개 메시지 •{' '}
                        {selectedSummary.totalAnswerCount}개 답변
                      </span>
                    </div>
                    <p className="text-body-16-regular text-gray-800 font-sans leading-relaxed">
                      {selectedSummary.summary}
                    </p>
                  </div>

                  <Button
                    variant="outline"
                    size="md"
                    icon={<MessageCircle size={20} />}
                    onClick={() =>
                      navigate(`/chat/review/${selectedInterviewId}`)
                    }
                    className="w-full"
                  >
                    인터뷰 회고하기
                  </Button>
                </div>
              ) : (
                <div className="text-center py-8">
                  <MessageCircle
                    size={48}
                    className="mx-auto text-gray-300 mb-4"
                  />
                  <p className="text-body-16-regular text-gray-500 font-sans">
                    이 날에는 인터뷰 기록이 없습니다
                  </p>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
