package com.tdd.talktobook.data.dataSource.dataSourceImpl.ai

import com.tdd.talktobook.data.dataSource.ai.InterviewAIDataSource
import com.tdd.talktobook.data.entity.request.interview.ai.CreateInterviewChatRequestDto
import com.tdd.talktobook.data.entity.request.interview.ai.InterviewQuestionsRequestDto
import com.tdd.talktobook.data.entity.request.interview.ai.StartInterviewRequestDto
import com.tdd.talktobook.data.service.ai.InterviewAIService
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

    override suspend fun postStartInterview(autobiographyId: Int, body: StartInterviewRequestDto): HttpResponse =
        interviewAIService.postStartInterview(autobiographyId, body)
}
