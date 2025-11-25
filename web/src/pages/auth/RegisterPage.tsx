import React from 'react'

import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { Mail, Lock, Eye, EyeOff, UserPlus } from 'lucide-react'
import { useMutation } from '@tanstack/react-query'

import Button from '@/components/common/Button'
import InputField from '@/components/common/InputField'
import { authApi } from '@/lib/api/auth'
import { useAlertStore } from '@/store/alert.store'

export default function RegisterPage() {
  const navigate = useNavigate()
  const { showAlert } = useAlertStore()
  
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    confirmPassword: ''
  })
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)
  const [errors, setErrors] = useState<Record<string, string>>({})

  const registerMutation = useMutation({
    mutationFn: authApi.register,
    onSuccess: () => {
      showAlert('회원가입이 완료되었습니다. 이메일 인증을 진행해주세요.', 'success')
      navigate('/verify-code', { 
        state: { email: formData.email } 
      })
    },
    onError: (error: any) => {
      showAlert(error.message || '회원가입에 실패했습니다.', 'error')
    }
  })

  const validateForm = () => {
    const newErrors: Record<string, string> = {}
    
    // 이메일 검증
    if (!formData.email) {
      newErrors.email = '이메일을 입력해주세요.'
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = '올바른 이메일 형식이 아닙니다.'
    }
    
    // 비밀번호 검증
    if (!formData.password) {
      newErrors.password = '비밀번호를 입력해주세요.'
    } else if (formData.password.length < 8) {
      newErrors.password = '비밀번호는 8자 이상이어야 합니다.'
    }
    
    // 비밀번호 확인 검증
    if (!formData.confirmPassword) {
      newErrors.confirmPassword = '비밀번호 확인을 입력해주세요.'
    } else if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = '비밀번호가 일치하지 않습니다.'
    }
    
    return newErrors
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    
    const newErrors = validateForm()
    setErrors(newErrors)
    
    if (Object.keys(newErrors).length === 0) {
      registerMutation.mutate({
        email: formData.email,
        password: formData.password
      })
    }
  }

  const handleInputChange = (field: string) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData(prev => ({ ...prev, [field]: e.target.value }))
    // 입력 시 에러 제거
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: '' }))
    }
  }

  return (
    <div className="min-h-screen bg-bg-white flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        {/* 헤더 */}
        <div className="text-center mb-8">
          <h1 className="text-heading-32 text-gray-900 mb-2 font-sans">
            회원가입
          </h1>
          <p className="text-body-18-regular text-gray-600 font-sans">
            나만의 인생 이야기를 시작해보세요
          </p>
        </div>

        {/* 회원가입 폼 */}
        <form onSubmit={handleSubmit} className="space-y-6">
          <InputField
            label="이메일"
            type="email"
            value={formData.email}
            onChange={handleInputChange('email')}
            placeholder="이메일을 입력하세요"
            error={errors.email}
            helperText="인증 메일이 발송됩니다"
          />

          <InputField
            label="비밀번호"
            type={showPassword ? 'text' : 'password'}
            value={formData.password}
            onChange={handleInputChange('password')}
            rightIcon={showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
            onRightIconClick={() => setShowPassword(!showPassword)}
            placeholder="비밀번호를 입력하세요"
            error={errors.password}
            helperText="8자 이상 입력해주세요"
          />

          <InputField
            label="비밀번호 확인"
            type={showConfirmPassword ? 'text' : 'password'}
            value={formData.confirmPassword}
            onChange={handleInputChange('confirmPassword')}
            rightIcon={showConfirmPassword ? <EyeOff size={20} /> : <Eye size={20} />}
            onRightIconClick={() => setShowConfirmPassword(!showConfirmPassword)}
            placeholder="비밀번호를 다시 입력하세요"
            error={errors.confirmPassword}
          />

          <Button
            type="submit"
            size="lg"
            className="w-full"
            loading={registerMutation.isPending}
            icon={<UserPlus size={20} />}
          >
            회원가입
          </Button>
        </form>

        {/* 링크 */}
        <div className="mt-6 text-center">
          <div className="text-body-18-regular text-gray-600 font-sans">
            이미 계정이 있으신가요?{' '}
            <Link 
              to="/login"
              className="text-point-1 hover:text-pri-700 font-medium"
            >
              로그인
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}
