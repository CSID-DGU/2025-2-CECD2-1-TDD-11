import { api } from '@/lib/apiClient'
import { buildQuery } from '@/lib/common'

export interface UpdateMemberMetadataRequestDto {
  gender: 'MALE' | 'FEMALE' | 'NONE',
  occupation: string,
  ageGroup: string,
}

export interface MemberMetadataResponseDto {
  gender: 'MALE' | 'FEMALE' | 'NONE',
  occupation: string,
  ageGroup: string,
  successed: boolean,
}

export const feedbackApi = {
  updateMemberMetadata: (data: UpdateMemberMetadataRequestDto): Promise<void> =>
    api.auth.put('/members/me', data),

  getMemberMetadata: (): Promise<MemberMetadataResponseDto> =>
    api.auth.get('/members/me'),
}
