import { InputHTMLAttributes, forwardRef, ReactNode } from 'react'

interface InputFieldProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string
  error?: string
  helperText?: string
  rightIcon?: ReactNode
  onRightIconClick?: () => void
}

const InputField = forwardRef<HTMLInputElement, InputFieldProps>(
  (
    {
      label,
      error,
      helperText,
      rightIcon,
      onRightIconClick,
      className = '',
      ...props
    },
    ref
  ) => {
    return (
      <div className="w-full">
        {label && (
          <label className="block text-body-18-medium text-gray-700 mb-2 font-sans">
            {label}
          </label>
        )}

        <div className="w-full relative">
          <input
            ref={ref}
            className={`
      w-full py-4
      ${rightIcon ? 'pr-12' : ''}
      px-5
      text-body-18-regular font-sans
      bg-white border-2 rounded-lg
      transition-all duration-200
      focus:outline-none focus:ring-2 focus:ring-offset-1
      disabled:opacity-50 disabled:cursor-not-allowed
      ${
        error
          ? 'border-error-500 focus:border-error-600 focus:ring-error-200'
          : 'border-point-1 focus:border-point-1 focus:ring-point-1/15'
      }
      ${className}
    `}
            {...props}
         />

          {rightIcon && (
            <div className="absolute right-4 top-1/2 transform -translate-y-1/2 text-gray-400">
              {rightIcon}
            </div>
          )}
        </div>

        {error && (
          <p className="mt-2 pl-2 text-caption-14 text-error-600 font-sans">
            {error}
          </p>
        )}

        {helperText && !error && (
          <p className="mt-2 text-caption-14 text-gray-500 font-sans">
            {helperText}
          </p>
        )}
      </div>
    )
  }
)

InputField.displayName = 'InputField'

export default InputField
