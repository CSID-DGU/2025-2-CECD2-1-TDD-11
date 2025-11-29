import { createBrowserRouter } from 'react-router-dom'

// Pages
import HomePage from '@/pages/HomePage'
import LoginPage from '@/pages/auth/LoginPage'
import RegisterPage from '@/pages/auth/RegisterPage'
import VerifyCodePage from '@/pages/auth/VerifyCodePage'
import ResetPasswordPage from '@/pages/auth/ResetPassword'
import NotFoundPage from '@/pages/NotFoundPage'
import AutobiographyPage from '@/pages/autobiography/AutobiographyPage'
import AutobiographyInitPage from '@/pages/autobiography/create/AutobiographyInitPage'
import ChatPage from '@/pages/chat/ChatPage'
import OnboardingPage from '@/pages/onboarding/OnboardingPage'
import SettingPage from '@/pages/setting/SettingPage'
import InterviewReviewPage from '@/pages/chat/InterviewReviewPage'
import Layout from '@/components/layout/Layout'


export const router = createBrowserRouter([
  {
    path: '/web',
    element: <Layout />,
    errorElement: <NotFoundPage />,
    children: [
      {
        index: true,
        element: <HomePage />,
      },
      {
        path: 'login',
        element: <LoginPage />,
      },
      {
        path: 'reset-password',
        element: <ResetPasswordPage />,
      },
      {
        path: 'register',
        element: <RegisterPage />,
      },
      {
        path: 'verify-code',
        element: <VerifyCodePage />,
      },
      {
        path: 'autobiography',
        element: <AutobiographyPage />,
      },
      {
        path: 'autobiography/init',
        element: <AutobiographyInitPage />,
      },
      {
        path: 'chat',
        element: <ChatPage />,
      },
      {
        path: 'chat/review/:interviewId',
        element: <InterviewReviewPage />,
      },
      {
        path: 'onboarding',
        element: <OnboardingPage />,
      },
      {
        path: 'setting',
        element: <SettingPage />,
      },
    ],
  },
  {
    path: '*',
    element: <NotFoundPage />,
  },
])
