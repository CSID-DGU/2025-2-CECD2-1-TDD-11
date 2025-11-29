import { ReactNode, useEffect } from 'react'

interface ModalProps {
  isOpen: boolean
  onClose: () => void
  title?: string
  children: ReactNode
  showCloseButton?: boolean
  size?: 'sm' | 'md' | 'lg'
}

export default function Modal({ 
  isOpen, 
  onClose, 
  title, 
  children, 
  showCloseButton = true,
  size = 'md'
}: ModalProps) {
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden'
    } else {
      document.body.style.overflow = 'unset'
    }
    
    return () => {
      document.body.style.overflow = 'unset'
    }
  }, [isOpen])

  if (!isOpen) return null

  const sizes = {
    sm: 'max-w-md',
    md: 'max-w-lg tablet:max-w-xl',
    lg: 'max-w-2xl tablet:max-w-4xl'
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      {/* Backdrop */}
      <div 
        className="absolute inset-0 bg-black bg-opacity-50 transition-opacity"
        onClick={onClose}
      />
      
      {/* Modal */}
      <div className={`
        relative bg-white rounded-xl shadow-xl w-full ${sizes[size]}
        max-h-[90vh] overflow-y-auto
        transform transition-all duration-200
      `}>
        {/* Header */}
        {(title || showCloseButton) && (
          <div className="flex items-center justify-between p-4 tablet:p-6 border-b border-gray-200">
            {title && (
              <h2 className="text-subheading-18 tablet:text-subheading-24 text-gray-900 font-sans">
                {title}
              </h2>
            )}
            {showCloseButton && (
              <button
                onClick={onClose}
                className="p-2 text-gray-400 hover:text-gray-600 transition-colors rounded-lg hover:bg-gray-100"
                aria-label="닫기"
              >
                <span className="text-xl">✕</span>
              </button>
            )}
          </div>
        )}
        
        {/* Content */}
        <div className="p-4 tablet:p-6">
          {children}
        </div>
      </div>
    </div>
  )
}