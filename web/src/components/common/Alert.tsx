import { useEffect } from 'react'
import { X, CheckCircle, XCircle, AlertTriangle, Info } from 'lucide-react'
import { useAlertStore } from '@/store/alert.store'

export default function Alert() {
  const { message, type, isVisible, hideAlert } = useAlertStore()

  useEffect(() => {
    if (isVisible) {
      const timer = setTimeout(() => {
        hideAlert()
      }, 5000)
      return () => clearTimeout(timer)
    }
  }, [isVisible, hideAlert])

  if (!isVisible) return null

  const styles = {
    success: 'bg-success-50 border-success-500 text-success-800',
    error: 'bg-error-50 border-error-500 text-error-800',
    warning: 'bg-warning-50 border-warning-500 text-warning-800',
    info: 'bg-info-50 border-info-500 text-info-800',
  }

  const icons = {
    success: <CheckCircle size={24} className="text-success-600" />,
    error: <XCircle size={24} className="text-error-600" />,
    warning: <AlertTriangle size={24} className="text-warning-600" />,
    info: <Info size={24} className="text-info-600" />,
  }

  return (
    <div className="fixed top-4 right-4 z-50 animate-slide-in">
      <div className={`flex items-center gap-3 px-4 py-3 rounded-lg border-2 shadow-lg min-w-[300px] max-w-[500px] ${styles[type]}`}>
        {icons[type]}
        <p className="flex-1 text-body-16-regular font-sans">{message}</p>
        <button
          onClick={hideAlert}
          className="hover:opacity-70 transition-opacity"
        >
          <X size={20} />
        </button>
      </div>
    </div>
  )
}
