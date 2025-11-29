import { Check } from 'lucide-react'

interface StepIndicatorProps {
  readonly steps: readonly string[]
  readonly currentStep: number // 0부터 시작
  readonly className?: string
}

export default function StepIndicator({
  steps,
  currentStep,
  className = '',
}: StepIndicatorProps) {
  return (
    <div className={`w-full ${className}`}>
      {/* 단계 표시 */}
      <div className="flex items-center justify-between">
        {steps.map((step, index) => (
          <div
            key={`${step}-${index}`}
            className="flex flex-col items-center"
            style={{ width: `${100 / steps.length}%` }}
          >
            {/* 원형 단계 표시 */}
            <div className="relative flex items-center justify-center w-full">
              {/* 연결선 (첫 번째 제외) */}
              {index > 0 && (
                <div className="absolute left-0 right-1/2 h-0.5 -translate-y-0">
                  <div
                    className={`h-full transition-all duration-300 ${
                      index <= currentStep ? 'bg-point-1' : 'bg-gray-200'
                    }`}
                  />
                </div>
              )}

              {/* 단계 원 */}
              <div
                className={`
                  relative z-10 w-8 h-8 tablet:w-10 tablet:h-10 rounded-full flex items-center justify-center
                  transition-all duration-300 font-sans font-medium text-caption-14 tablet:text-body-18-medium
                  ${
                    index < currentStep
                      ? 'bg-point-1 text-white'
                      : index === currentStep
                        ? 'bg-point-1 text-white ring-4 ring-point-1'
                        : 'bg-gray-200 text-gray-500'
                  }
                `}
              >
                {index < currentStep ? (
                  <Check size={16} className="tablet:w-5 tablet:h-5" />
                ) : (
                  <span>{index + 1}</span>
                )}
              </div>

              {/* 연결선 (마지막 제외) */}
              {index < steps.length - 1 && (
                <div className="absolute right-0 left-1/2 h-0.5 -translate-y-0">
                  <div
                    className={`h-full transition-all duration-300 ${
                      index < currentStep ? 'bg-point-1' : 'bg-gray-200'
                    }`}
                  />
                </div>
              )}
            </div>

            {/* 단계 라벨 */}
            <span
              className={`
                mt-2 text-caption-12 tablet:text-caption-14 font-sans text-center
                transition-colors duration-300 px-1
                ${index <= currentStep ? 'text-point-1 font-medium' : 'text-gray-500'}
              `}
            >
              {step}
            </span>
          </div>
        ))}
      </div>
    </div>
  )
}
