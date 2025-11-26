package com.tdd.talktobook.data.service

import com.tdd.talktobook.data.base.EndPoints
import com.tdd.talktobook.data.entity.request.interview.InterviewConversationRequestDto
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Multipart
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Part
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
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

    @GET(EndPoints.Interview.INTERVIEW_SUMMARY)
    suspend fun getInterviewSummaries(
        @Path("autobiographyId") autobiographyId: Int,
        @Query("year") year: Int,
        @Query("month") month: Int,
    ): HttpResponse
}
