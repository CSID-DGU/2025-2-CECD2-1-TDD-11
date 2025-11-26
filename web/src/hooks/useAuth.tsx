import { useNavigate } from 'react-router-dom'
import { useMutation } from '@tanstack/react-query'
import { authApi } from '@/lib/api/auth'
import { useAuthStore } from '@/store/auth.store'
import { useAlertStore } from '@/store/alert.store'

export function useAuth() {
  const navigate = useNavigate()
  const { login, logout: logoutStore, isAuthenticated } = useAuthStore()
  const { showAlert } = useAlertStore()

  const loginMutation = useMutation({
    mutationFn: authApi.login,
    onSuccess: (data) => {
      login({ 
        accessToken: data.accessToken, 
        refreshToken: data.refreshToken 
      })
      
      if (data.metadataSuccessed) {
        navigate('/')
      } else {
        navigate('/onboarding')
      }
      
      showAlert('로그인되었습니다.', 'success')
    },
    onError: (error: any) => {
      showAlert(error.message || '로그인에 실패했습니다.', 'error')
    }
  })

  const registerMutation = useMutation({
    mutationFn: authApi.register,
    onSuccess: (_, variables) => {
      showAlert('회원가입이 완료되었습니다. 이메일 인증을 진행해주세요.', 'success')
      navigate('/verify-code', { 
        state: { email: variables.email } 
      })
    },
    onError: (error: any) => {
      showAlert(error.message || '회원가입에 실패했습니다.', 'error')
    }
  })

  const verifyCodeMutation = useMutation({
    mutationFn: authApi.verifyCode,
    onSuccess: () => {
      showAlert('이메일 인증이 완료되었습니다!', 'success')
      navigate('/login')
    },
    onError: (error: any) => {
      throw error // 컴포넌트에서 처리
    }
  })

  const logoutMutation = useMutation({
    mutationFn: authApi.logout,
    onSuccess: () => {
      logoutStore()
      navigate('/login')
      showAlert('로그아웃되었습니다.', 'success')
    },
    onError: () => {
      // 로그아웃 실패해도 로컬 상태는 정리
      logoutStore()
      navigate('/login')
    }
  })

  const resetPasswordMutation = useMutation({
    mutationFn: authApi.resetPassword,
    onSuccess: () => {
      showAlert('비밀번호 재설정 메일을 발송했습니다.', 'success')
      navigate('/login')
    },
    onError: (error: any) => {
      showAlert(error.message || '비밀번호 재설정에 실패했습니다.', 'error')
    }
  })

  return {
    // Mutations
    loginMutation,
    registerMutation,
    verifyCodeMutation,
    logoutMutation,
    resetPasswordMutation,
    
    // States
    isAuthenticated: isAuthenticated(),
    
    // Actions
    logout: () => logoutMutation.mutate()
  }
}
