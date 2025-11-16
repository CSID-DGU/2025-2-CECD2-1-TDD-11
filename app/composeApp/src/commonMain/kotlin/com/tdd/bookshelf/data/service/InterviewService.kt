package com.tdd.bookshelf.data.service

import com.tdd.bookshelf.data.base.EndPoints
import com.tdd.bookshelf.data.entity.request.interview.InterviewConversationRequestDto
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.client.statement.HttpResponse

interface InterviewService {
    @GET(EndPoints.Interview.INTERVIEWCONVERSATION)
    suspend fun getInterviewConversation(
        @Path("interviewId") interviewId: Int,
    ): HttpResponse

    @POST(EndPoints.Interview.INTERVIEWRENEWAL)
    suspend fun postInterviewRenewal(
        @Path("interviewId") interviewId: Int,
    ): HttpResponse

    @POST(EndPoints.Interview.INTERVIEWCONVERSATION)
    suspend fun postInterviewChatBotConversation(
        @Path("interviewId") interviewId: Int,
        @Body body: InterviewConversationRequestDto,
    ): HttpResponse

    @GET(EndPoints.Interview.INTERVIEWQUESTIONLIST)
    suspend fun getInterviewQuestionList(
        @Path("interviewId") interviewId: Int,
    ): HttpResponse
}
