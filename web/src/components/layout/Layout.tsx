import { useState, createContext, useContext } from 'react'
import { Outlet, useLocation } from 'react-router-dom'
import { ChevronUp, ChevronDown } from 'lucide-react'
import TopBar from './TopBar'
import Navigation from './Navigation'
import Button from '@/components/common/Button'

// 네비게이션 상태 컨텍스트
const NavigationContext = createContext<{
  showNavigation: boolean
  setShowNavigation: (show: boolean) => void
}>({
  showNavigation: false,
  setShowNavigation: () => {}
})

export const useNavigation = () => useContext(NavigationContext)

export default function Layout() {
  const location = useLocation()
  const [showNavigation, setShowNavigation] = useState(false)
  
  // 인증 페이지에서는 레이아웃 숨김
  const hideLayout = ['/login', '/register', '/verify-code', '/reset-password'].includes(location.pathname)
  
  // 인터뷰 페이지 확인 (chat 경로 포함)
  const isInterviewPage = location.pathname.startsWith('/chat')
  
  if (hideLayout) {
    return (
      <div className="h-screen bg-bg-white font-sans">
        <Outlet />
      </div>
    )
  }

  if (isInterviewPage) {
    return (
      <NavigationContext.Provider value={{ showNavigation, setShowNavigation }}>
        <div className="h-screen bg-bg-white font-sans flex flex-col">
          <TopBar />
          <main className="flex-1 overflow-y-auto">
            <Outlet />
          </main>
          
          {/* 고정된 네비게이션 (항상 하단에 위치) */}
          <div className={`fixed bottom-0 left-0 right-0 z-40 transition-transform duration-200 ${
            showNavigation ? 'translate-y-0' : 'translate-y-full'
          }`}>
            <Navigation />
          </div>
          
          {/* 토글 버튼 (네비게이션 상태에 따라 위치 조정) */}
          <div className={`fixed right-6 z-50 transition-all duration-200 ${
            showNavigation ? 'bottom-24' : 'bottom-6'
          }`}>
            <Button
              variant="blue"
              size="md"
              icon={showNavigation ? <ChevronDown size={20} /> : <ChevronUp size={20} />}
              onClick={() => setShowNavigation(!showNavigation)}
              className="rounded-full shadow-lg"
              iconOnly
            />
          </div>
        </div>
      </NavigationContext.Provider>
    )
  }

  return (
    <div className="h-screen bg-bg-white font-sans flex flex-col">
      <TopBar />
      <main className="flex-1 overflow-y-auto pb-16 tablet:pb-0">
        <Outlet />
      </main>
      
      {/* 일반 페이지용 네비게이션 */}
      <div className="fixed bottom-0 left-0 right-0 tablet:relative tablet:bottom-auto">
        <Navigation />
      </div>
    </div>
  )
}
