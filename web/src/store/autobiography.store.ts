// src/store/autobiography.store.ts
import { create } from 'zustand'
import { persist } from 'zustand/middleware'

interface AutobiographyState {
  autobiographyId: string | null
  status: 'EMPTY' | 'PROGRESSING' | 'ENOUGH' | 'CREATING' | 'FINISH' | null
  categories: number[] | null

  // Getters
  getAutobiographyId: () => string | null
  getStatus: () => 'EMPTY' | 'PROGRESSING' | 'ENOUGH' | 'CREATING' | 'FINISH' | null
  getCategories: () => number[] | null

  // Actions
  setAutobiographyId: (id: string) => void
  setStatus: (
    status: 'EMPTY' | 'PROGRESSING' | 'ENOUGH' | 'CREATING' | 'FINISH' | null
  ) => void
  setCategories: (categories: number[]) => void
  clearAutobiographyId: () => void
  clearStatus: () => void
  clearCategories: () => void
  clearAll: () => void
}

export const useAutobiographyStore = create<AutobiographyState>()(
  persist(
    (set, get) => ({
      autobiographyId: null,
      status: null,
      categories: null,
      
      getAutobiographyId: () => {
        const state = get()
        return state.autobiographyId
      },

      getStatus: () => {
        const state = get()
        return state.status
      },

      getCategories: () => {
        const state = get()
        return state.categories
      },

      setAutobiographyId: (id: string) =>
        set({
          autobiographyId: id,
        }),

      setStatus: (
        status: 'EMPTY' | 'PROGRESSING' | 'CREATING' | 'FINISH' | null
      ) =>
        set({
          status: status,
        }),

      setCategories: (categories: number[]) =>
        set({
          categories: categories,
        }),

      clearAutobiographyId: () =>
        set({
          autobiographyId: null,
        }),

      clearStatus: () =>
        set({
          status: null,
        }),

      clearCategories: () =>
        set({
          categories: null,
        }),

      clearAll: () =>
        set({
          autobiographyId: null,
          status: null,
          categories: null,
        }),
    }),
    {
      name: 'autobiography-storage',
      partialize: state => ({
        autobiographyId: state.autobiographyId,
        status: state.status,
        categories: state.categories,
      }),
    }
  )
)
