import { useState, useRef, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Settings, LogOut, UserX } from 'lucide-react'
import { useAuthStore } from '@/store/auth.store'
import { authApi } from '@/lib/api/auth'
import Button from '@/components/common/Button'
import Modal from '@/components/common/Modal'

export default function TopBar() {
  const navigate = useNavigate()
  const { isAuthenticated, logout } = useAuthStore()
  const [showDropdown, setShowDropdown] = useState(false)
  const [showLogoutModal, setShowLogoutModal] = useState(false)
  const [showUnregisterModal, setShowUnregisterModal] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const dropdownRef = useRef<HTMLDivElement>(null)

  // 드롭다운 외부 클릭 시 닫기
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setShowDropdown(false)
      }
    }

    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  const handleLogout = async () => {
    setIsLoading(true)
    try {
      await authApi.logout()
      logout()
      navigate('/auth/login')
    } catch (error) {
      console.error('로그아웃 실패:', error)
    } finally {
      setIsLoading(false)
      setShowLogoutModal(false)
    }
  }

  const handleUnregister = async () => {
    setIsLoading(true)
    try {
      await authApi.unregister()
      logout()
      navigate('/auth/login')
    } catch (error) {
      console.error('회원탈퇴 실패:', error)
    } finally {
      setIsLoading(false)
      setShowUnregisterModal(false)
    }
  }

  return (
    <>
      <header className="bg-bg-white px-6 py-4">
        <div className="flex items-center justify-between">
          <button
            className="text-subheading-24 text-gray-900 cursor-pointer font-sans bg-transparent border-none p-0"
            onClick={() => navigate('/')}
            aria-label="Navigate to home"
          >
            대화로책
          </button>

          {isAuthenticated() && (
            <div className="relative" ref={dropdownRef}>
              <Button
                variant="menu"
                size="md"
                onClick={() => setShowDropdown(!showDropdown)}
                icon={<Settings size={24} />}
                aria-label="설정"
              />

              {/* 드롭다운 메뉴 */}
              {showDropdown && (
                <div className="absolute right-0 top-full mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-200 py-2 z-50">
                  <button
                    onClick={() => {
                      setShowLogoutModal(true)
                      setShowDropdown(false)
                    }}
                    className="w-full px-4 py-2 text-left text-gray-700 hover:bg-gray-50 flex items-center gap-3"
                  >
                    <LogOut size={16} />
                    로그아웃
                  </button>
                  <button
                    onClick={() => {
                      setShowUnregisterModal(true)
                      setShowDropdown(false)
                    }}
                    className="w-full px-4 py-2 text-left text-red-600 hover:bg-red-50 flex items-center gap-3"
                  >
                    <UserX size={16} />
                    회원탈퇴
                  </button>
                </div>
              )}
            </div>
          )}
        </div>
      </header>

      {/* 로그아웃 확인 모달 */}
      <Modal
        isOpen={showLogoutModal}
        onClose={() => setShowLogoutModal(false)}
        title="로그아웃"
        size="sm"
      >
        <div className="space-y-4">
          <p className="text-gray-700">정말 로그아웃하시겠습니까?</p>
          <div className="flex gap-3 justify-end">
            <Button
              variant="cancel"
              size="md"
              onClick={() => setShowLogoutModal(false)}
            >
              취소
            </Button>
            <Button
              variant="blue"
              size="md"
              onClick={handleLogout}
              loading={isLoading}
            >
              로그아웃
            </Button>
          </div>
        </div>
      </Modal>

      {/* 회원탈퇴 확인 모달 */}
      <Modal
        isOpen={showUnregisterModal}
        onClose={() => setShowUnregisterModal(false)}
        title="회원탈퇴"
        size="sm"
      >
        <div className="space-y-4">
          <div>
            <p className="text-gray-700 mb-2">정말 회원탈퇴하시겠습니까?</p>
            <p className="text-sm text-red-600">탈퇴 시 모든 데이터가 삭제되며 복구할 수 없습니다.</p>
          </div>
          <div className="flex gap-3 justify-end">
            <Button
              variant="cancel"
              size="md"
              onClick={() => setShowUnregisterModal(false)}
            >
              취소
            </Button>
            <Button
              variant="red"
              size="md"
              onClick={handleUnregister}
              loading={isLoading}
            >
              회원탈퇴
            </Button>
          </div>
        </div>
      </Modal>
    </>
  )
}
