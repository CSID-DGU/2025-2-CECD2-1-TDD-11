import { api } from '@/lib/apiClient'

// 이메일 로그인
export interface LoginRequest {
  email: string
  password: string
  deviceToken?: string
}

export interface LoginResponse {
  accessToken: string
  refreshToken: string
  metadataSuccessed: boolean
}

// 토큰 갱신
export interface RefreshRequest {
  refreshToken: string
}

export interface RefreshResponse {
  accessToken: string
  refreshToken: string
}

// 이메일 회원가입
export interface RegisterRequest {
  email: string
  password: string
}

// 이메일 인증 요청
export interface VerifyCodeRequest {
  email: string
  verificationCode: string
}

// 비밀번호 초기화 요청
export interface ResetPasswordRequest {
  email: string
}

// 인증 API 함수들
export const authApi = {
  // 로그인
  login: (data: LoginRequest): Promise<LoginResponse> =>
    api.post('/auth/email-login', data),

  // 회원가입
  register: (data: RegisterRequest): Promise<void> =>
    api.post('/auth/email-register', data),

  // 이메일 인증 요청
  verifyCode: (data: VerifyCodeRequest): Promise<void> =>
    api.post('/auth/email-verify', data),
  
  refresh: (data: RefreshRequest): Promise<RefreshResponse> =>
    api.post('/auth/reissue', data),

  resetPassword: (data: ResetPasswordRequest): Promise<void> =>
    api.auth.post('/auth/reset-password', data),

  // 로그아웃
  logout: (): Promise<void> =>
    api.auth.post('/auth/logout'),

  // 회원 탈퇴
  unregister: (): Promise<void> =>
    api.auth.delete('/auth/unregister'),
}
