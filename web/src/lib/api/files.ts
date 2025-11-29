import { api } from '@/lib/apiClient'
import { buildQuery } from '@/lib/common'

export interface PresignedUrlRequest {
  imageUrl: string
}

// 파일 API 함수들
export const filesApi = {
  // Presigned URL 생성
  getPresignedUrl: (data: PresignedUrlRequest): Promise<string> =>
    api.auth.post('/files/presigned-url', data),

  // 파일 업로드 (Presigned URL 사용)
  uploadFile: async (file: File, uploadUrl: string): Promise<void> => {
    const response = await fetch(uploadUrl, {
      method: 'PUT',
      body: file,
      headers: {
        'Content-Type': file.type,
      },
    })

    if (!response.ok) {
      throw new Error(`파일 업로드에 실패 하였습니다. ${response.status}`)
    }
  },
}

// 지원되는 파일 타입
export const SUPPORTED_FILE_TYPES = [
  'image/jpeg',
  'image/png',
  'image/gif',
  'image/webp',
  'application/pdf',
  'application/msword',
  'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
  'application/vnd.ms-excel',
  'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
  'text/plain',
  'application/zip',
]
