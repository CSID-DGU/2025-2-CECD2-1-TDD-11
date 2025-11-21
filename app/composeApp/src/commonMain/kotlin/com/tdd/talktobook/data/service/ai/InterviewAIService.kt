package com.tdd.talktobook.data.service.ai

import com.tdd.talktobook.data.base.EndPoints
import com.tdd.talktobook.data.entity.request.interview.ai.CreateInterviewChatRequestDto
import com.tdd.talktobook.data.entity.request.interview.ai.InterviewQuestionsRequestDto
import com.tdd.talktobook.data.entity.request.interview.ai.StartInterviewRequestDto
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.client.statement.HttpResponse

interface InterviewAIService {
    @POST(EndPoints.Interview.INTERVIEWQUESTION)
    suspend fun postCreateInterviewQuestion(
        @Body body: InterviewQuestionsRequestDto,
    ): HttpResponse

    @POST(EndPoints.Interview.CREATEINTERVIEW)
    suspend fun postCreateInterviewChat(
        @Body body: CreateInterviewChatRequestDto,
    ): HttpResponse

    @POST(EndPoints.Interview.START_INTERVIEW)
    suspend fun postStartInterview(
        @Path("autobiography_id") autobiographyId: Int = 0,
        @Body body: StartInterviewRequestDto
    ): HttpResponse
}
