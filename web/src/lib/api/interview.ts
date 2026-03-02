import { api } from '@/lib/apiClient'
import { buildQuery } from '@/lib/common'

// 인터뷰 시작 요청
export interface StartInterviewRequestDto {
  preferred_categories: number[] 
}

export interface StartInterviewResponseDto {
  first_question: QuestionDate
}

// 인터뷰 질문 채팅 요청
export interface ChatInterviewRequestDto {
  answer_text: string
}

export interface ChatInterviewResponseDto {
  last_answer_materials_id: number[][],
  next_question: QuestionDate
}

interface QuestionDate {
  id: string,
  material: string,
  material_id: number[],
  text: string,
  type: string
}

// 인터뷰 질문 목록 조회 응답
export interface InterviewQuestionsResponseDto {
  currentQuestionId: number,
  results: InterviewQuestion[],
}

interface InterviewQuestion {
  questionId: number,
  questionText: string,
  questionOrder: number,
}

// 인터뷰 대회 목록 조회
export interface InterviewConversationsResponseDto {
  results: InterviewConversation[],
  currentPage: number,
  totalPages: number,
  totalElements: number,
  hasNextPage: boolean,
  hasPreviousPage: boolean,
}

interface InterviewConversation {
  conversationId: number,
  content: string,
  conversationType: 'HUMAN' | 'BOT',
  createdAt: string,
}

// 특정 날짜의 인터뷰 요약 조회
export interface InterviewSummaryByDateResponseDto {
  interviews: InterviewSummary[],
}

interface InterviewSummary {
  id: number,
  totalMessageCount: number,
  summary: string,
  totalAnswerCount: number,
  date: string,
}

// 교과목 API 함수들
export const interviewApi = {
  // 인터뷰 시작 요청
  startInterview: (autobiographyId: number, data: StartInterviewRequestDto): Promise<StartInterviewResponseDto> => api.ai.auth.postJson(`/interviews/start/${autobiographyId}`, data),

  // 인터뷰 질문 응답 요청
  chatInterview: (autobiographyId: number, data: ChatInterviewRequestDto): Promise<ChatInterviewResponseDto> => api.ai.auth.postJson(`/interviews/chat/${autobiographyId}`, data),

  // 인터뷰 질문 목록 조회
  getInterviewQuestions: (interviewId: number): Promise<InterviewQuestionsResponseDto> => api.auth.get(`/interviews/${interviewId}/questions`),

  // 인터뷰 대화 목록 조회
  getInterviewConversations: (interviewId: number, page: number, size: number): Promise<InterviewConversationsResponseDto> => {
    const query = buildQuery({ page, size });
    return api.auth.get(`/interviews/${interviewId}/conversations/?${query}`);
  },

  // 특정 날짜의 인터뷰 요약 조회
  getInterviewSummaryByDate: (autobiographyId: number, year: string, month: string): Promise<InterviewSummaryByDateResponseDto> => {
    const query = buildQuery({ year, month });
    return api.auth.get(`/interviews/${autobiographyId}/interviews/summaries?${query}`);
  },
}
