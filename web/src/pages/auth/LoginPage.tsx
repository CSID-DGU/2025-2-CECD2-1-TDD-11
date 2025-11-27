
import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { Mail, Lock, Eye, EyeOff, LogIn } from 'lucide-react'
import { useMutation } from '@tanstack/react-query'

import Button from '@/components/common/Button'
import InputField from '@/components/common/InputField'
import { authApi } from '@/lib/api/auth'
import { useAuthStore } from '@/store/auth.store'
import { useAlertStore } from '@/store/alert.store'

export default function LoginPage() {
  const navigate = useNavigate()
  const { login } = useAuthStore()
  const { showAlert } = useAlertStore()
  
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  })
  const [showPassword, setShowPassword] = useState(false)
  const [errors, setErrors] = useState<Record<string, string>>({})

  const loginMutation = useMutation({
    mutationFn: authApi.login,
    onSuccess: (data) => {
      login({ 
        accessToken: data.accessToken, 
        refreshToken: data.refreshToken 
      })
      
      // 메타데이터 성공 여부에 따라 라우팅
      if (data.metadataSuccessed) {
        navigate('/web')
      } else {
        navigate('/web/onboarding')
      }
      
      showAlert('로그인되었습니다.', 'success')
    },
    onError: (error: any) => {
      // 네트워크 오류 또는 서버 연결 실패
      if (!error.status || error.message?.includes('fetch') || error.message?.includes('network')) {
        showAlert('서버와 통신할 수 없습니다. 네트워크 연결을 확인해주세요.', 'error')
        return
      }
      
      // 401 Unauthorized - 인증 실패
      if (error.status === 401) {
        showAlert('이메일 또는 비밀번호가 올바르지 않습니다.', 'error')
        return
      }
      
      // 기타 에러
      showAlert(error.message || '로그인에 실패했습니다.', 'error')
    }
  })

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    
    // 간단한 유효성 검사
    const newErrors: Record<string, string> = {}
    
    if (!formData.email) {
      newErrors.email = '이메일을 입력해주세요.'
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = '올바른 이메일 형식이 아닙니다.'
    }
    
    if (!formData.password) {
      newErrors.password = '비밀번호를 입력해주세요.'
    }
    
    setErrors(newErrors)
    
    if (Object.keys(newErrors).length === 0) {
      loginMutation.mutate(formData)
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
            로그인
          </h1>
          <p className="text-body-18-regular text-gray-600 font-sans">
            대화로책
          </p>
        </div>

        {/* 로그인 폼 */}
        <form onSubmit={handleSubmit} className="space-y-6">
          <InputField
            label="이메일"
            type="email"
            value={formData.email}
            onChange={handleInputChange('email')}
            placeholder="이메일을 입력하세요"
            error={errors.email}
          />

          <InputField
            label="비밀번호"
            type={showPassword ? 'text' : 'password'}
            value={formData.password}
            onChange={handleInputChange('password')}
            rightIcon={showPassword ? <EyeOff size={24} /> : <Eye size={24} />}
            onRightIconClick={() => setShowPassword(!showPassword)}
            placeholder="비밀번호를 입력하세요"
            error={errors.password}
          />

          <Button
            type="submit"
            size="lg"
            variant="blue"
            className="w-full"
            loading={loginMutation.isPending}
          >
            로그인
          </Button>
        </form>

        {/* 링크들 */}
        <div className="mt-6 text-center space-y-4">
          <Link 
            to="/web/reset-password"
            className="text-body-18-regular text-point-1 font-sans"
          >
            비밀번호를 잊으셨나요?
          </Link>
          
          <div className="text-body-18-regular text-gray-600 font-sans">
            계정이 없으신가요?{' '}
            <Link 
              to="/web/register"
              className="text-point-1 font-sans"
            >
              회원가입
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}
