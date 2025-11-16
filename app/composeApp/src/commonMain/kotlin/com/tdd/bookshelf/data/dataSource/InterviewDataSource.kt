package com.tdd.bookshelf.data.dataSource

import com.tdd.bookshelf.data.entity.request.interview.InterviewConversationRequestDto
import io.ktor.client.statement.HttpResponse

interface InterviewDataSource {
    suspend fun getInterviewConversation(interviewId: Int): HttpResponse

    suspend fun postInterviewRenewal(interviewId: Int): HttpResponse

    suspend fun postInterviewConversation(
        interviewId: Int,
        conversation: InterviewConversationRequestDto,
    ): HttpResponse

    suspend fun getInterviewQuestion(interviewId: Int): HttpResponse
}
