package com.tdd.talktobook.data.service.ai

import com.tdd.talktobook.data.base.EndPoints
import com.tdd.talktobook.data.entity.request.interview.ai.CreateInterviewChatRequestDto
import com.tdd.talktobook.data.entity.request.interview.ai.InterviewQuestionsRequestDto
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
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
}
