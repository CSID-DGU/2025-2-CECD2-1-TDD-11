import React from 'react'
import { AutobiographyDetailResponseDto } from '@/lib/api/autobiography'

interface AutobiographyViewerProps {
  autobiography: AutobiographyDetailResponseDto
}

const AutobiographyViewer: React.FC<AutobiographyViewerProps> = ({ autobiography }) => {
  return (
    <div className="bg-white rounded-lg shadow-sm">
      <div className="p-8">
        <div className="max-w-4xl mx-auto">
          <div className="prose prose-lg max-w-none">
            {autobiography.chapters.map((chapter, index) => (
              <div key={chapter.chapterId} className="mb-12">
                {chapter.coverImageUrl && (
                  <div className="mb-6">
                    <img 
                      src={chapter.coverImageUrl} 
                      alt={chapter.title}
                      className="w-full max-w-md mx-auto rounded-lg shadow-sm"
                    />
                  </div>
                )}
                <h2 className="text-2xl font-bold mb-4 text-gray-800">
                  {chapter.title}
                </h2>
                <div className="text-gray-700 leading-relaxed whitespace-pre-wrap">
                  {chapter.content}
                </div>
                {index < autobiography.chapters.length - 1 && (
                  <hr className="my-8 border-gray-200" />
                )}
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}

export default AutobiographyViewer
