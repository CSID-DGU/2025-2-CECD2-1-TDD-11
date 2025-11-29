import { useLocation, useNavigate } from 'react-router-dom'
import { Home, MessageCircle, BookOpen } from 'lucide-react'
import { useAuthStore } from '@/store/auth.store'

export default function Navigation() {
  const navigate = useNavigate()
  const location = useLocation()
  const { isAuthenticated } = useAuthStore()

  if (!isAuthenticated()) return null

  const navItems = [
    { path: '/', label: '홈', icon: Home },
    { path: '/chat', label: '인터뷰', icon: MessageCircle },
    { path: '/autobiography', label: '자서전', icon: BookOpen },
  ]

  return (
    <nav className="relative">
      {/* 글래스모피즘 배경 */}
      <div className="fixed relative bottom-auto left-auto right-auto">
        <div className="bg-white/90 backdrop-blur-lg border border-white/30 rounded-2xl shadow-lg rounded-xl">
          
          <div className="relative mx-auto max-w-none max-w-4xl">
            <div className="flex justify-center">
              {navItems.map((item) => {
                const IconComponent = item.icon
                const isActive = location.pathname === item.path
                
                return (
                  <button
                    key={item.path}
                    onClick={() => navigate(item.path)}
                    className={`
                      w-full px-8 py-4 text-center transition-all duration-200 rounded-xl
                      ${isActive 
                        ? 'text-point-1' 
                        : 'text-gray-500 hover:text-gray-700'
                      }
                    `}
                  >   
                    <div className="relative z-10 flex flex-col items-center">
                      {/* 아이콘 */}
                      <div className="flex justify-center items-center mb-1 tablet:mb-2">
                        <IconComponent 
                          size={22} 
                          className="tablet:w-6 tablet:h-6 pc:w-7 pc:h-7" 
                        />
                      </div>
                      
                      {/* 라벨 */}
                      <div className="text-caption-12 tablet:text-caption-14 pc:text-body-18-regular font-sans font-medium">
                        {item.label}
                      </div>
                    </div>
                  </button>
                )
              })}
            </div>
          </div>
        </div>
      </div>
    </nav>
  )
}