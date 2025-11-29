import { useState } from 'react'
import { ChevronLeft, ChevronRight, MessageCircle } from 'lucide-react'
import { useQuery } from '@tanstack/react-query'
import { interviewApi } from '@/lib/api/interview'
import { autobiographyApi } from '@/lib/api/autobiography'
import Button from '@/components/common/Button'

interface CalendarProps {
  onDateSelect?: (date: string, interviewId?: number) => void
}

export default function Calendar({ onDateSelect }: CalendarProps) {
  const [currentDate, setCurrentDate] = useState(new Date())
  const [selectedDate, setSelectedDate] = useState<string | null>(null)

  // 현재 자서전 ID 조회
  const { data: currentAutobiography } = useQuery({
    queryKey: ['current-autobiography'],
    queryFn: autobiographyApi.getCurrentAutobiographyId,
  })

  // 인터뷰 요약 데이터 조회
  const { data: interviewSummary } = useQuery({
    queryKey: [
      'interview-summary',
      currentAutobiography?.autobiographyId,
      currentDate.getFullYear(),
      currentDate.getMonth() + 1,
    ],
    queryFn: () =>
      interviewApi.getInterviewSummaryByDate(
        currentAutobiography!.autobiographyId,
        currentDate.getFullYear().toString(),
        (currentDate.getMonth() + 1).toString()
      ),
    enabled: !!currentAutobiography?.autobiographyId,
  })

  const year = currentDate.getFullYear()
  const month = currentDate.getMonth()

  // 달력 생성 로직
  const firstDayOfMonth = new Date(year, month, 1)
  const lastDayOfMonth = new Date(year, month + 1, 0)
  const firstDayWeekday = firstDayOfMonth.getDay()
  const daysInMonth = lastDayOfMonth.getDate()

  const days = []

  // 이전 달의 빈 칸들
  for (let i = 0; i < firstDayWeekday; i++) {
    days.push(null)
  }

  // 현재 달의 날짜들
  for (let day = 1; day <= daysInMonth; day++) {
    days.push(day)
  }

  const goToPreviousMonth = () => {
    setCurrentDate(new Date(year, month - 1, 1))
    setSelectedDate(null)
  }

  const goToNextMonth = () => {
    setCurrentDate(new Date(year, month + 1, 1))
    setSelectedDate(null)
  }

  const getInterviewForDate = (day: number) => {
    if (!interviewSummary?.interviews) return null

    // 로컬 시간대 기준으로 날짜 문자열 생성
    const date = new Date(year, month, day)
    const dateString = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`

    return interviewSummary.interviews.find(
      interview => interview.date === dateString
    )
  }

  const handleDateClick = (day: number) => {
    // 로컬 시간대 기준으로 날짜 문자열 생성
    const date = new Date(year, month, day)
    const dateString = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    const interview = getInterviewForDate(day)

    setSelectedDate(dateString)

    // 부모 컴포넌트에 날짜 선택 알림 (interview ID와 함께)
    if (onDateSelect) {
      onDateSelect(dateString, interview?.id)
    }
  }

  const monthNames = [
    '1월',
    '2월',
    '3월',
    '4월',
    '5월',
    '6월',
    '7월',
    '8월',
    '9월',
    '10월',
    '11월',
    '12월',
  ]

  const weekdays = ['일', '월', '화', '수', '목', '금', '토']

  return (
    <div className="w-full">
      {/* 헤더 */}
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-subheading-18 text-gray-900 font-sans">
          {year}년 {monthNames[month]}
        </h3>

        <div className="flex items-center gap-2">
          <Button
            variant="ghost"
            size="sm"
            rounded="full"
            onClick={goToPreviousMonth}
            icon={<ChevronLeft size={20} />}
            iconOnly
            aria-label="이전 달"
          />

          <Button
            variant="ghost"
            size="sm"
            rounded="full"
            onClick={goToNextMonth}
            icon={<ChevronRight size={20} />}
            iconOnly
            aria-label="다음 달"
          />
        </div>
      </div>

      {/* 요일 헤더 */}
      <div className="grid grid-cols-7 gap-1">
        {weekdays.map((weekday, index) => (
          <div
            key={weekday}
            className={`text-center py-2 text-caption-14 font-sans font-medium ${
              index === 0
                ? 'text-error-600'
                : index === 6
                  ? 'text-point-1'
                  : 'text-gray-600'
            }`}
          >
            {weekday}
          </div>
        ))}
      </div>

      {/* 날짜 그리드 */}
      <div className="grid grid-cols-7 gap-1">
        {days.map((day, index) => {
          if (!day) {
            return <div key={index} className="aspect-square"></div>
          }

          const dateString = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`
          const isToday =
            new Date().getFullYear() === year &&
            new Date().getMonth() === month &&
            new Date().getDate() === day
          const isSelected = selectedDate === dateString
          const interview = getInterviewForDate(day)
          const hasInterview = !!interview && interview.totalMessageCount > 0

          return (
            <button
              key={index}
              onClick={() => handleDateClick(day)}
              className={`
                aspect-square flex flex-col items-center justify-center p-1 rounded-full text-caption-14 font-sans relative transition-colors duration-200
                ${isToday ? 'bg-point-1 text-white font-medium' : 'hover:bg-gray-100'}
                ${!isSelected && !isToday ? 'text-gray-700' : ''}
              `}
            >
              <span className="text-center">{day}</span>

              {/* 인터뷰 진행일 표시 - 상단에 동그라미 */}
              {hasInterview && (
                <div className="absolute top-6 left-1/2 transform -translate-x-1/2">
                  <div
                    className={`w-2.5 h-2.5 rounded-full ${
                      isToday ? 'bg-white' : 'bg-point-1'
                    }`}
                  ></div>
                </div>
              )}
            </button>
          )
        })}
      </div>
    </div>
  )
}
