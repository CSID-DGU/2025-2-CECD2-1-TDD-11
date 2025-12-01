// src/store/auth.ts
import { create } from 'zustand'
import { persist } from 'zustand/middleware'

interface AuthState {
  accessToken: string | null
  refreshToken: string | null

  // Getters
  isAuthenticated: () => boolean
  // Actions
  login: (
    tokens: { accessToken: string; refreshToken: string }
  ) => void
  logout: () => void
  updateAccessToken: (newToken: string) => void
  updateRefreshToken: (newToken: string) => void
}

const isTokenValid = (token: string | null): boolean => {
  if (!token) return false

  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    return payload.exp * 1000 > Date.now()
  } catch {
    return false
  }
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      isLoggedIn: false,
      accessToken: null,
      refreshToken: null,
      user: null,
      role: null,

      isAuthenticated: () => {
        const state = get()
        return (
          isTokenValid(state.accessToken) && isTokenValid(state.refreshToken)
        )
      },

      login: ({ accessToken, refreshToken }) =>
        set({
          accessToken,
          refreshToken,
        }),

      logout: () =>
        set({
          accessToken: null,
          refreshToken: null,
        }),

      updateAccessToken: newToken =>
        set({
          accessToken: newToken,
        }),
      updateRefreshToken: newToken =>
        set({
          refreshToken: newToken,
        }),
    }),
    {
      name: 'auth-storage',
      partialize: state => ({
        accessToken: state.accessToken,
        refreshToken: state.refreshToken
      }),
    }
  )
)
