package com.tdd.talktobook.data.dataSource.dataSourceImpl

import com.tdd.talktobook.data.dataSource.InterviewDataSource
import com.tdd.talktobook.data.entity.request.interview.InterviewConversationRequestDto
import com.tdd.talktobook.data.service.AuthService
import com.tdd.talktobook.data.service.InterviewService
import io.ktor.client.statement.HttpResponse
import org.koin.core.annotation.Single

@Single(binds = [InterviewDataSource::class])
class InterviewDataSourceImpl(
    private val interviewService: InterviewService,
    private val authService: AuthService
) : InterviewDataSource {
    override suspend fun getInterviewConversation(interviewId: Int): HttpResponse =
        interviewService.getInterviewConversation(interviewId)

    override suspend fun getCoShowInterviewConversation(interviewId: Int): HttpResponse =
        authService.getCoShowInterviewConversation(interviewId)

    override suspend fun postInterviewRenewal(interviewId: Int): HttpResponse =
        interviewService.postInterviewRenewal(interviewId)

    override suspend fun postInterviewConversation(
        interviewId: Int,
        conversation: InterviewConversationRequestDto,
    ): HttpResponse =
        interviewService.postInterviewChatBotConversation(interviewId, conversation)

    override suspend fun getInterviewQuestion(interviewId: Int): HttpResponse =
        interviewService.getInterviewQuestionList(interviewId)

    override suspend fun getInterviewSummaries(
        autobiographyId: Int,
        year: Int,
        month: Int,
    ): HttpResponse =
        interviewService.getInterviewSummaries(autobiographyId, year, month)

    override suspend fun postCoShowInterviewAnswer(interviewId: Int, answerText: String): HttpResponse =
        authService.postCoShowInterviewAnswer(interviewId, answerText)
}
