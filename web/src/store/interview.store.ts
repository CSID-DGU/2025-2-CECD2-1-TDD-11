// src/store/interview.store.ts
import { create } from 'zustand'
import { persist } from 'zustand/middleware'

interface InterviewState {
  interviewId: string | null

  // Getters
  getInterviewId: () => string | null
  
  // Actions
  setInterviewId: (id: string) => void
  clearInterviewId: () => void
  clearAll: () => void
}

export const useInterviewStore = create<InterviewState>()(
  persist(
    (set, get) => ({
      interviewId: null,
      currentQuestionId: null,

      getInterviewId: () => {
        const state = get()
        return state.interviewId
      },

      setInterviewId: (id: string) =>
        set({
          interviewId: id,
        }),

      clearInterviewId: () =>
        set({
          interviewId: null,
        }),

      clearAll: () =>
        set({
          interviewId: null
        }),
    }),
    {
      name: 'interview-storage',
      partialize: state => ({
        interviewId: state.interviewId
      }),
    }
  )
)
