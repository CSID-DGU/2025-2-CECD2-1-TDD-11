import React from 'react'

import { useState, useEffect } from 'react'
import { useNavigate, useLocation, Link } from 'react-router-dom'
import { Shield, CheckCircle } from 'lucide-react'
import { useMutation } from '@tanstack/react-query'

import Button from '@/components/common/Button'
import InputField from '@/components/common/InputField'
import { authApi } from '@/lib/api/auth'
import { useAlertStore } from '@/store/alert.store'

export default function VerifyCodePage() {
  const navigate = useNavigate()
  const location = useLocation()
  const { showAlert } = useAlertStore()
  
  const email = location.state?.email || ''
  const [verificationCode, setVerificationCode] = useState('')
  const [error, setError] = useState('')

  // 이메일이 없으면 회원가입 페이지로 리다이렉트
  useEffect(() => {
    if (!email) {
      showAlert('잘못된 접근입니다. 회원가입을 다시 진행해주세요.', 'error')
      navigate('/register')
    }
  }, [email, navigate, showAlert])

  const verifyMutation = useMutation({
    mutationFn: authApi.verifyCode,
    onSuccess: () => {
      showAlert('이메일 인증이 완료되었습니다!', 'success')
      navigate('/login')
    },
    onError: (error: any) => {
      setError(error.message || '인증에 실패했습니다.')
    }
  })

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    
    if (!verificationCode) {
      setError('인증 코드를 입력해주세요.')
      return
    }
    
    if (verificationCode.length !== 6) {
      setError('인증 코드는 6자리입니다.')
      return
    }
    
    verifyMutation.mutate({
      email,
      verificationCode
    })
  }

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value.replace(/\D/g, '').slice(0, 6) // 숫자만, 최대 6자리
    setVerificationCode(value)
    if (error) setError('')
  }

  if (!email) return null

  return (
    <div className="min-h-screen bg-bg-white flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        {/* 헤더 */}
        <div className="text-center mb-8">
          <div className="w-16 h-16 bg-pri-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <Shield size={32} className="text-point-1" />
          </div>
          
          <h1 className="text-heading-32 text-gray-900 mb-2 font-sans">
            이메일 인증
          </h1>
          <p className="text-body-18-regular text-gray-600 font-sans mb-2">
            <span className="font-medium text-point-1">{email}</span>로<br />
            인증 코드를 발송했습니다
          </p>
          <p className="text-caption-14 text-gray-500 font-sans">
            메일함을 확인해주세요 (스팸함 포함)
          </p>
        </div>

        {/* 인증 폼 */}
        <form onSubmit={handleSubmit} className="space-y-6">
          <InputField
            label="인증 코드"
            type="text"
            value={verificationCode}
            onChange={handleInputChange}
            placeholder="6자리 숫자 입력"
            error={error}
            maxLength={6}
            className="text-center text-heading-24 tracking-widest"
          />

          <Button
            type="submit"
            size="lg"
            className="w-full"
            loading={verifyMutation.isPending}
            disabled={verificationCode.length !== 6}
            icon={<CheckCircle size={20} />}
          >
            인증 완료
          </Button>
        </form>

        {/* 링크들 */}
        <div className="mt-6 text-center space-y-4">
          <p className="text-body-18-regular text-gray-600 font-sans">
            인증 메일을 받지 못하셨나요?
          </p>
          
          <div className="space-y-2">
            <Link 
              to="/register"
              className="block text-point-1 hover:text-pri-700 font-medium"
            >
              다시 회원가입하기
            </Link>
            
            <Link 
              to="/login"
              className="block text-gray-600 hover:text-gray-700"
            >
              로그인으로 돌아가기
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}
