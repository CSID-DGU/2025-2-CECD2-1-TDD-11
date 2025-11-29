import { api } from '@/lib/apiClient'
import { Pagination, buildQuery } from '@/lib/common'
import { useAutobiographyStore } from '@/store/autobiography.store'

// 특정 자서전 상세 조회
export interface AutobiographyDetailResponseDto {
  autobiographyId: number,
  chapters: AutobiographyChapter[],
  createdAt: string,
  updatedAt: string,
}

interface AutobiographyChapter {
  chapterId: number,
  title: string,
  content: string,
  coverImageUrl?: string,
}

// 특정 자서전 수정 요청
export interface UpdateAutobiographyRequestDto {
  title: string,
  content: string,
  preSignedImageUrl?: string,
}

// 테마와 자서전 생성 이유 등록 요청
export interface CreateAutobiographyMetadataRequestDto {
  theme: string,
  reason: string,
}

// 특정 자서전의 생성 이유 수정 요청
export interface UpdateAutobiographyReasonRequestDto {
  reason: string,
}

// 자서전 목록 조회
export interface AutobiographyListResponseDto extends Pagination {
  results: AutobiographySummary[];
}

interface AutobiographySummary {
  autobiographyId: number,
  title: string,
  status: 'EMPTY' | 'PROGRESSING' | 'ENOUGH' | 'CREATING' | 'FINISH',
  contentPreview: string,
  coverImageUrl?: string,
  createdAt: string,
  updatedAt: string,
}

// 선택한 자서전에 대한 테마 조회
export interface AutobiographyThemeResponseDto {
  theme: string,
  categories: number[],
}

// 현재 진행 중인 자서전 인터뷰 진행률 조회
export interface AutobiographyProgressResponseDto {
  progressPercentage: number,
  status: 'EMPTY' | 'PROGRESSING' | 'ENOUGH' | 'CREATING' | 'FINISH',
}

// 특정 자서전에서 count된 소재를 오름차순으로 반환
export interface AutobiographyCategoryCount extends Pagination {
  popularMeterlas: popularMeterlas[];
}

interface popularMeterlas {
  id: number,
  order: number,
  rank: number,
  name: string,
  imageUrl: string,
  count: number,
}

// 현재 진행 중인 자서전 id 조회
export interface CurrentAutobiographyIdResponseDto {
  autobiographyId: number,
}

// 자서전 API 함수들
export const autobiographyApi = {
  // 특정 자서전 수정 요청
  updateAutobiography: (data: UpdateAutobiographyRequestDto): Promise<void> => {
    const autobiographyId = useAutobiographyStore.getState().getAutobiographyId()
    if (!autobiographyId) throw new Error('자서전 ID가 없습니다.')
    return api.auth.put(`/autobiographies/${autobiographyId}`, data)
  },

  // 특정 자서전 삭제 요청
  deleteAutobiography: (): Promise<void> => {
    const autobiographyId = useAutobiographyStore.getState().getAutobiographyId()
    if (!autobiographyId) throw new Error('자서전 ID가 없습니다.')
    return api.auth.delete(`/autobiographies/${autobiographyId}`)
  },

  // 테마와 자서전 생성 이유 등록 요청
  createAutobiographyMetadata: (data: CreateAutobiographyMetadataRequestDto): Promise<void> => api.auth.post(`/autobiographies/init`, data),
  
  // 현재 진행중인 자서전의 상태를 변경
  updateAutobiographyStatus: (status: 'EMPTY' | 'PROGRESSING' | 'ENOUGH' | 'CREATING' | 'FINISH'): Promise<void> => {
    const autobiographyId = useAutobiographyStore.getState().getAutobiographyId()
    if (!autobiographyId) throw new Error('자서전 ID가 없습니다.')
    return api.auth.patch(`/autobiographies/${autobiographyId}/status`, { status })
  },

  // 특정 자서전의 생성 이유 수정 요청
  updateAutobiographyMetadata: (data: UpdateAutobiographyReasonRequestDto): Promise<void> => {
    const autobiographyId = useAutobiographyStore.getState().getAutobiographyId()
    if (!autobiographyId) throw new Error('자서전 ID가 없습니다.')
    return api.auth.patch(`/autobiographies/${autobiographyId}/reason`, data)
  },

  // 현재 진행 중인 자서전 생성 요청
  generateAutobiography: (): Promise<void> => {
    const autobiographyId = useAutobiographyStore.getState().getAutobiographyId()
    if (!autobiographyId) throw new Error('자서전 ID가 없습니다.')
    return api.auth.post(`/autobiographies/${autobiographyId}/generate`)
  },

  // 자서전 목록 조회
  getAutobiographyList: (page: number, size: number, statuses: string[]): Promise<AutobiographyListResponseDto> => {
    const query = buildQuery({ page, size, statuses });
    return api.auth.get(`/autobiographies?${query}`);
  },

    // 특정 자서전 상세 조회
  getAutobiographyDetail: (autobiographyId?: number): Promise<AutobiographyDetailResponseDto> => {
    const id = autobiographyId || useAutobiographyStore.getState().getAutobiographyId()
    if (!id) throw new Error('자서전 ID가 없습니다.')
    return api.auth.get(`/autobiographies/${id}`)
  },

  // 선택한 자서전에 대한 테마 조회
  getAutobiographyTheme: (): Promise<AutobiographyThemeResponseDto> => {
    const autobiographyId = useAutobiographyStore.getState().getAutobiographyId()
    if (!autobiographyId) throw new Error('자서전 ID가 없습니다.')
    return api.auth.get(`/autobiographies/${autobiographyId}/theme`)
  },

  // 현재 진행 중인 자서전 인터뷰 진행률 조회
  getAutobiographyProgress: (): Promise<AutobiographyProgressResponseDto> => {
    const autobiographyId = useAutobiographyStore.getState().getAutobiographyId()
    if (!autobiographyId) throw new Error('자서전 ID가 없습니다.')
    return api.auth.get(`/autobiographies/${autobiographyId}/progress`)
  },

  // 특정 자서전에서 count된 소재를 오름차순으로 반환
  getAutobiographyCategoryCount: (page: number, size: number, sort: string): Promise<AutobiographyCategoryCount> => {
    const autobiographyId = useAutobiographyStore.getState().getAutobiographyId()
    if (!autobiographyId) throw new Error('자서전 ID가 없습니다.')
    const query = buildQuery({ page, size, sort });
    return api.auth.get(`/autobiographies/${autobiographyId}/materials?${query}`);
  },

  // 현재 진행 중인 자서전 id 조회
  getCurrentAutobiographyId: (): Promise<CurrentAutobiographyIdResponseDto> => api.auth.get(`/autobiographies/current`),
}
