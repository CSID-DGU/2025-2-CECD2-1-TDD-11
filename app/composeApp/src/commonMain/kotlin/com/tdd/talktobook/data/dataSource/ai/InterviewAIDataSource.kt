package com.tdd.talktobook.data.dataSource.ai

import com.tdd.talktobook.data.entity.request.interview.ai.CreateInterviewChatRequestDto
import com.tdd.talktobook.data.entity.request.interview.ai.InterviewQuestionsRequestDto
import io.ktor.client.statement.HttpResponse

interface InterviewAIDataSource {
    suspend fun postInterviewQuestions(body: InterviewQuestionsRequestDto): HttpResponse

    suspend fun postCreateInterviewChat(body: CreateInterviewChatRequestDto): HttpResponse
}
