import React, { useState, useEffect } from 'react'
import { Plus } from 'lucide-react'
import { autobiographyApi, AutobiographyListResponseDto, AutobiographyDetailResponseDto } from '@/lib/api/autobiography'
import Button from '@/components/common/Button'
import LoadingSpinner from '@/components/common/LoadingSpinner'
import AutobiographyViewer from '@/components/autobiography/AutobiographyViewer'

interface AutobiographySummary {
  autobiographyId: number
  title: string
  status: 'EMPTY' | 'PROGRESSING' | 'ENOUGH' | 'CREATING' | 'FINISH'
  contentPreview: string
  coverImageUrl?: string
  createdAt: string
  updatedAt: string
}

const AutobiographyPage = () => {
  const [autobiographyList, setAutobiographyList] = useState<AutobiographySummary[]>([])
  const [selectedAutobiography, setSelectedAutobiography] = useState<AutobiographyDetailResponseDto | null>(null)
  const [selectedId, setSelectedId] = useState<number | null>(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    loadAutobiographyList()
  }, [])

  const loadAutobiographyList = async () => {
    try {
      const response = await autobiographyApi.getAutobiographyList(0, 20, ['EMPTY', 'PROGRESSING', 'ENOUGH', 'CREATING', 'FINISH'])
      setAutobiographyList(response.results)
    } catch (error) {
      console.error('자서전 목록 로드 실패:', error)
    }
  }

  const handleSelectAutobiography = async (id: number) => {
    if (selectedId === id) return
    
    setSelectedId(id)
    setLoading(true)
    try {
      const detail = await autobiographyApi.getAutobiographyDetail(id)
      setSelectedAutobiography(detail)
    } catch (error) {
      console.error('자서전 상세 로드 실패:', error)
    } finally {
      setLoading(false)
    }
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'EMPTY': return 'bg-gray-100 text-gray-600'
      case 'PROGRESSING': return 'bg-blue-100 text-blue-600'
      case 'ENOUGH': return 'bg-purple-100 text-purple-600'
      case 'CREATING': return 'bg-yellow-100 text-yellow-600'
      case 'FINISH': return 'bg-green-100 text-green-600'
      default: return 'bg-gray-100 text-gray-600'
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      {/* 상단: 자서전 목록 */}
      <div className="mb-8">
        <h1 className="text-2xl font-bold mb-6">나의 자서전</h1>
        <div className="flex gap-4 overflow-x-auto pb-4">
          {autobiographyList.map((autobiography) => (
            <div
              key={autobiography.autobiographyId}
              onClick={() => handleSelectAutobiography(autobiography.autobiographyId)}
              className={`flex-shrink-0 cursor-pointer transition-all ${
                selectedId === autobiography.autobiographyId 
                  ? 'transform scale-105' 
                  : 'hover:transform hover:scale-102'
              }`}
            >
              <div className="w-24 h-24 rounded-full bg-white shadow-md overflow-hidden mb-2 border-4 border-transparent hover:border-blue-200">
                {autobiography.coverImageUrl ? (
                  <img 
                    src={autobiography.coverImageUrl} 
                    alt={autobiography.title}
                    className="w-full h-full object-cover"
                  />
                ) : (
                  <div className="w-full h-full bg-gradient-to-br from-blue-100 to-purple-100 flex items-center justify-center">
                    <span className="text-2xl font-bold text-gray-600">
                      {autobiography.title?.charAt(0) || '?'}
                    </span>
                  </div>
                )}
              </div>
              <div className="text-center max-w-24">
                <h3 className="text-sm font-medium truncate mb-1">{autobiography.title || '제목 없음'}</h3>
                <span className={`text-xs px-2 py-1 rounded-full ${getStatusColor(autobiography.status)}`}>
                  {autobiography.status}
                </span>
              </div>
            </div>
          ))}
          
          {/* 새 자서전 생성 버튼 */}
          <div className="flex-shrink-0">
            <Button
              variant="ghost"
              size="lg"
              iconOnly
              icon={<Plus className="w-8 h-8" />}
              className="w-24 h-24 rounded-full border-2 border-dashed border-gray-300 hover:border-blue-400 mb-2"
            />
            <div className="text-center max-w-24">
              <h3 className="text-sm font-medium text-gray-500">새 자서전</h3>
            </div>
          </div>
        </div>
      </div>

      {/* 하단: 선택된 자서전 상세 */}
      {loading ? (
        <div className="bg-white rounded-lg shadow-sm p-8 text-center">
          <LoadingSpinner size={32} className="mx-auto mb-2" />
          <p className="text-gray-500">자서전을 불러오는 중...</p>
        </div>
      ) : selectedAutobiography ? (
        <AutobiographyViewer autobiography={selectedAutobiography} />
      ) : (
        <div className="bg-white rounded-lg shadow-sm p-8 text-center text-gray-500">
          <p>자서전을 선택해주세요</p>
        </div>
      )}
    </div>
  )
}

export default AutobiographyPage
