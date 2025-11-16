package com.tdd.bookshelf.data.dataSource.dataSourceImpl.ai

import com.tdd.bookshelf.data.dataSource.ai.InterviewAIDataSource
import com.tdd.bookshelf.data.entity.request.interview.ai.CreateInterviewChatRequestDto
import com.tdd.bookshelf.data.entity.request.interview.ai.InterviewQuestionsRequestDto
import com.tdd.bookshelf.data.service.ai.InterviewAIService
import io.ktor.client.statement.HttpResponse
import org.koin.core.annotation.Single

@Single(binds = [InterviewAIDataSource::class])
class InterviewAIDataSourceImpl(
    private val interviewAIService: InterviewAIService,
) : InterviewAIDataSource {
    override suspend fun postInterviewQuestions(body: InterviewQuestionsRequestDto): HttpResponse =
        interviewAIService.postCreateInterviewQuestion(body)

    override suspend fun postCreateInterviewChat(body: CreateInterviewChatRequestDto): HttpResponse =
        interviewAIService.postCreateInterviewChat(body)
}
