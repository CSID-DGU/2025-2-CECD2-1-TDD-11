import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { Mail, ArrowLeft } from 'lucide-react'
import { useMutation } from '@tanstack/react-query'

import Button from '@/components/common/Button'
import InputField from '@/components/common/InputField'
import { authApi } from '@/lib/api/auth'
import { useAlertStore } from '@/store/alert.store'

export default function ResetPasswordPage() {
  const navigate = useNavigate()
  const { showAlert } = useAlertStore()
  const [email, setEmail] = useState('')
  const [error, setError] = useState('')

  const resetMutation = useMutation({
    mutationFn: authApi.resetPassword,
    onSuccess: () => {
      showAlert('비밀번호 재설정 이메일이 발송되었습니다.', 'success')
      navigate('/web/login')
    },
    onError: (error: any) => {
      showAlert(error.message || '이메일 발송에 실패했습니다.', 'error')
    }
  })

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    
    if (!email) {
      setError('이메일을 입력해주세요.')
      return
    }
    
    if (!/\S+@\S+\.\S+/.test(email)) {
      setError('올바른 이메일 형식이 아닙니다.')
      return
    }
    
    resetMutation.mutate({ email })
  }

  return (
    <div className="min-h-screen bg-bg-white flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <button
          onClick={() => navigate('/web/login')}
          className="flex items-center text-gray-600 hover:text-gray-900 mb-6"
        >
          <ArrowLeft size={20} className="mr-2" />
          로그인으로 돌아가기
        </button>

        <div className="text-center mb-8">
          <h1 className="text-heading-32 text-gray-900 mb-2 font-sans">
            비밀번호 재설정
          </h1>
          <p className="text-body-18-regular text-gray-600 font-sans">
            가입하신 이메일로 비밀번호 재설정 링크를 보내드립니다.
          </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          <InputField
            label="이메일"
            type="email"
            value={email}
            onChange={(e) => {
              setEmail(e.target.value)
              setError('')
            }}
            placeholder="이메일을 입력하세요"
            error={error}
          />

          <Button
            type="submit"
            size="lg"
            variant="blue"
            className="w-full"
            loading={resetMutation.isPending}
          >
            재설정 링크 보내기
          </Button>
        </form>
      </div>
    </div>
  )
}
