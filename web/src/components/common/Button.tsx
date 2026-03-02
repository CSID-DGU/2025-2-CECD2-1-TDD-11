import { ButtonHTMLAttributes, ReactNode } from 'react'
import LoadingSpinner from './LoadingSpinner'

interface ButtonProps
  extends Readonly<ButtonHTMLAttributes<HTMLButtonElement>> {
  readonly variant?:
    | 'blue'
    | 'green'
    | 'outline'
    | 'cancel'
    | 'ghost'
    | 'red'
    | 'menu'
  readonly size?: 'sm' | 'md' | 'lg'
  readonly rounded?: 'sm' | 'md' | 'lg' | 'full'
  readonly children?: ReactNode
  readonly loading?: boolean
  readonly icon?: ReactNode
  readonly iconPosition?: 'left' | 'right'
  readonly iconOnly?: boolean // 아이콘만 있는 버튼인지 명시
}

export default function Button({
  variant = 'blue',
  size = 'md',
  rounded = 'md',
  children,
  loading = false,
  icon,
  iconPosition = 'left',
  iconOnly = false,
  disabled,
  className = '',
  ...props
}: ButtonProps) {
  const baseStyles =
    `font-sans font-medium rounded-${rounded} transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed inline-flex items-center justify-center`

  const variants = {
    blue: 'bg-point-1 text-white',
    green: 'bg-point-1 text-white',
    outline:
      'border-2 border-gray-300 text-gray-500 hover:border-gray-400 focus:ring-gray-200',
    cancel: 'bg-gray-200 text-gray-500 hover:bg-gray-300',
    ghost:
      'bg-transparent text-gray-700 hover:bg-gray-100 focus:ring-gray-200',
    red: 'bg-error-500 text-white',
    menu: 'bg-transparent text-gray-700 hover:bg-gray-50 focus:ring-gray-200',
  }

  // 아이콘만 있는 버튼의 경우 정사각형 크기
  const iconOnlySizes = {
    sm: 'w-8 h-8 p-0',
    md: 'w-10 h-10 p-0 tablet:w-12 tablet:h-12',
    lg: 'w-12 h-12 p-0 tablet:w-14 tablet:h-14',
  }

  // 일반 버튼 크기 (텍스트 포함)
  const regularSizes = {
    sm: 'px-3 py-2 text-caption-14',
    md: 'px-4 py-3 text-body-18-medium',
    lg: 'px-6 py-4 text-body-20-medium',
  }

  // 아이콘만 있는 버튼인지 자동 감지
  const isIconOnly = iconOnly || (icon && !children)

  // 간격 스타일 (아이콘과 텍스트가 모두 있을 때만)
  const gapStyle = icon && children && !isIconOnly ? 'gap-2' : ''

  const iconSizes = {
    sm: 16,
    md: 20,
    lg: 24,
  }

  return (
    <button
      className={`${baseStyles} ${variants[variant]} ${
        isIconOnly ? iconOnlySizes[size] : `${regularSizes[size]} ${gapStyle}`
      } ${className}`}
      disabled={disabled || loading}
      {...props}
    >
      {loading ? (
        <LoadingSpinner size={iconSizes[size]} />
      ) : (
        <>
          {icon && iconPosition === 'left' && (
            <span className="flex-shrink-0">{icon}</span>
          )}
          {children && <span>{children}</span>}
          {icon && iconPosition === 'right' && (
            <span className="flex-shrink-0">{icon}</span>
          )}
        </>
      )}
    </button>
  )
}
