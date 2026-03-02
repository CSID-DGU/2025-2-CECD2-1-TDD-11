import { TextareaHTMLAttributes, forwardRef } from 'react'

interface TextAreaProps extends TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string
  error?: string
  helperText?: string
}

const TextArea = forwardRef<HTMLTextAreaElement, TextAreaProps>(({
  label,
  error,
  helperText,
  className = '',
  ...props
}, ref) => {
  return (
    <div className="w-full">
      {label && (
        <label className="block text-body-18-medium text-gray-700 mb-2 font-sans">
          {label}
        </label>
      )}
      
      <textarea
        ref={ref}
        className={`
          w-full px-5 py-4
          text-body-18-regular font-sans
          bg-white border-2 rounded-lg
          transition-all duration-200
          focus:outline-none focus:ring-2 focus:ring-offset-1
          disabled:opacity-50 disabled:cursor-not-allowed
          resize-none
          ${error 
            ? 'border-error-500 focus:border-error-600 focus:ring-error-200' 
            : 'border-point-1 focus:border-point-1 focus:ring-point-1/15'
          }
          ${className}
        `}
        {...props}
      />
      
      {error && (
        <p className="mt-2 text-caption-14 text-error-600 font-sans">
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
})

TextArea.displayName = 'TextArea'

export default TextArea
