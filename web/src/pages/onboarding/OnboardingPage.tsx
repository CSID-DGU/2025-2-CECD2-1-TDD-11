import React from 'react'

import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { User, Briefcase, Calendar, ArrowRight, ArrowLeft } from 'lucide-react'
import { useMutation } from '@tanstack/react-query'

import Button from '@/components/common/Button'
import InputField from '@/components/common/InputField'
import TextArea from '@/components/common/TextArea'
import StepIndicator from '@/components/common/StepIndicator'
import { feedbackApi } from '@/lib/api/member'
import { autobiographyApi } from '@/lib/api/autobiography'
import { useAlertStore } from '@/store/alert.store'
import { ageGroups, getAgeGroupLabel } from '@/config/age_group'
import { theme } from '@/config/theme'

type Step = 'gender' | 'occupation' | 'age' | 'theme' | 'reason'

export default function OnboardingPage() {
  const navigate = useNavigate()
  const { showAlert } = useAlertStore()

  const [currentStep, setCurrentStep] = useState<Step>('gender')
  const [metadataForm, setMetadataForm] = useState({
    gender: '' as 'MALE' | 'FEMALE' | 'NONE' | '',
    occupation: '',
    ageGroup: '' as keyof typeof ageGroups | '',
  })
  const [themeForm, setThemeForm] = useState({
    theme: '',
    reason: '',
  })
  const [errors, setErrors] = useState<Record<string, string>>({})

  const stepOrder: Step[] = ['gender', 'occupation', 'age', 'theme', 'reason']
  const stepLabels = ['성별', '직업', '연령대', '테마', '이유']
  const currentStepIndex = stepOrder.indexOf(currentStep)

  const metadataMutation = useMutation({
    mutationFn: feedbackApi.updateMemberMetadata,
    onSuccess: () => {
      setCurrentStep('theme')
    },
    onError: (error: any) => {
      showAlert(error.message || '정보 저장에 실패했습니다.', 'error')
    },
  })

  const themeMutation = useMutation({
    mutationFn: autobiographyApi.createAutobiographyMetadata,
    onSuccess: () => {
      showAlert('온보딩이 완료되었습니다!', 'success')
      navigate('/')
    },
    onError: (error: any) => {
      showAlert(error.message || '테마 설정에 실패했습니다.', 'error')
    },
  })

  const handleNext = () => {
    // 유효성 검사
    if (currentStep === 'gender' && !metadataForm.gender) {
      setErrors({ gender: '성별을 선택해주세요.' })
      return
    }
    if (currentStep === 'occupation' && !metadataForm.occupation.trim()) {
      setErrors({ occupation: '직업을 입력해주세요.' })
      return
    }
    if (currentStep === 'age' && !metadataForm.ageGroup) {
      setErrors({ ageGroup: '연령대를 선택해주세요.' })
      return
    }
    if (currentStep === 'theme' && !themeForm.theme) {
      setErrors({ theme: '테마를 선택해주세요.' })
      return
    }
    if (currentStep === 'reason' && !themeForm.reason.trim()) {
      setErrors({ reason: '자서전을 쓰는 이유를 입력해주세요.' })
      return
    }

    setErrors({})

    if (currentStep === 'age') {
      // 메타데이터 저장 후 테마 단계로
      metadataMutation.mutate(metadataForm as any)
    } else if (currentStep === 'reason') {
      // 테마와 이유 저장
      themeMutation.mutate(themeForm)
    } else if (currentStepIndex < stepOrder.length - 1) {
      setCurrentStep(stepOrder[currentStepIndex + 1])
    }
  }

  const handlePrev = () => {
    if (currentStepIndex > 0) {
      setCurrentStep(stepOrder[currentStepIndex - 1])
      setErrors({})
    }
  }

  return (
    <div className="bg-bg-white flex items-center justify-center p-4">
      <div className="w-full max-w-lg flex flex-col justify-center">
        {/* 헤더 */}
        <div className="text-center mb-8">
          <h1 className="text-heading-32 text-gray-900 mb-2 font-sans">
            {currentStep === 'gender' && '성별 선택'}
            {currentStep === 'occupation' && '직업 입력'}
            {currentStep === 'age' && '연령대 선택'}
            {currentStep === 'theme' && '테마 선택'}
            {currentStep === 'reason' && '작성 이유'}
          </h1>
        </div>

        {/* 단계 표시 */}
        <StepIndicator steps={stepLabels} currentStep={currentStepIndex} />
        <div className="h-6"></div>
        {/* 단계별 컨텐츠 */}
        <div className="space-y-6">
          {/* 성별 선택 단계 */}
          {currentStep === 'gender' && (
            <div>
              <div className="grid grid-cols-3 gap-3">
                {[
                  { value: 'MALE', label: '남성' },
                  { value: 'FEMALE', label: '여성' },
                  { value: 'NONE', label: '선택안함' },
                ].map(option => (
                  <Button
                    key={option.value}
                    variant={
                      metadataForm.gender === option.value ? 'blue' : 'cancel'
                    }
                    size="md"
                    onClick={() => {
                      setMetadataForm(prev => ({
                        ...prev,
                        gender: option.value as any,
                      }))
                      if (errors.gender)
                        setErrors(prev => ({ ...prev, gender: '' }))
                    }}
                    className="h-16"
                  >
                    {option.label}
                  </Button>
                ))}
              </div>
              {errors.gender && (
                <p className="mt-2 text-caption-14 text-error-600 font-sans">
                  {errors.gender}
                </p>
              )}
            </div>
          )}

          {/* 직업 입력 단계 */}
          {currentStep === 'occupation' && (
            <InputField
              type="text"
              value={metadataForm.occupation}
              onChange={e => {
                setMetadataForm(prev => ({
                  ...prev,
                  occupation: e.target.value,
                }))
                if (errors.occupation)
                  setErrors(prev => ({ ...prev, occupation: '' }))
              }}
              placeholder="예: 개발자, 학생, 주부 등"
              error={errors.occupation}
            />
          )}

          {/* 연령대 선택 단계 */}
          {currentStep === 'age' && (
            <div>
              <div className="grid grid-cols-2 gap-3">
                {Object.entries(ageGroups).map(([key, label]) => (
                  <Button
                    key={key}
                    variant={metadataForm.ageGroup === key ? 'blue' : 'cancel'}
                    size="md"
                    onClick={() => {
                      setMetadataForm(prev => ({
                        ...prev,
                        ageGroup: key as any,
                      }))
                      if (errors.ageGroup)
                        setErrors(prev => ({ ...prev, ageGroup: '' }))
                    }}
                    className="h-16"
                  >
                    {label}
                  </Button>
                ))}
              </div>
              {errors.ageGroup && (
                <p className="mt-2 text-caption-14 text-error-600 font-sans">
                  {errors.ageGroup}
                </p>
              )}
            </div>
          )}

          {/* 테마 선택 단계 */}
          {currentStep === 'theme' && (
            <div>
              <div className="grid grid-cols-2 tablet:grid-cols-3 gap-3">
                {Object.entries(theme).map(([key, label]) => (
                  <Button
                    key={key}
                    variant={themeForm.theme === key ? 'blue' : 'cancel'}
                    size="md"
                    onClick={() => {
                      setThemeForm(prev => ({ ...prev, theme: key }))
                      if (errors.theme) setErrors(prev => ({ ...prev, theme: '' }))
                    }}
                    className="h-16 text-left justify-start"
                  >
                    {label}
                  </Button>
                ))}
              </div>
              {errors.theme && (
                <p className="mt-2 text-caption-14 text-error-600 font-sans">
                  {errors.theme}
                </p>
              )}
            </div>
          )}

          {/* 이유 입력 단계 */}
          {currentStep === 'reason' && (
            <TextArea
              value={themeForm.reason}
              onChange={(e) => {
                setThemeForm(prev => ({ ...prev, reason: e.target.value }))
                if (errors.reason) setErrors(prev => ({ ...prev, reason: '' }))
              }}
              placeholder="자서전을 쓰고 싶은 이유나 목적을 자유롭게 적어주세요"
              rows={6}
              error={errors.reason}
            />
          )}
        </div>

        <div className="h-12"></div>

        {/* 버튼 영역 */}
        <div className="flex gap-4 mt-8">
          {currentStepIndex > 0 && (
            <Button
              type="button"
              variant="cancel"
              size="lg"
              className="flex-1"
              onClick={handlePrev}
            >
              이전
            </Button>
          )}

          <Button
            type="button"
            size="lg"
            className={currentStepIndex === 0 ? 'w-full' : 'flex-1'}
            onClick={handleNext}
            loading={metadataMutation.isPending || themeMutation.isPending}
          >
            {currentStep === 'reason' ? '완료' : '다음'}
          </Button>
        </div>
      </div>
    </div>
  )
}
