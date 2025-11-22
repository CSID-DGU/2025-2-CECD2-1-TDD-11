package com.tdd.talktobook.data.dataSource.dataSourceImpl.ai

import com.tdd.talktobook.data.dataSource.ai.InterviewAIDataSource
import com.tdd.talktobook.data.entity.request.interview.ai.ChatInterviewRequestDto
import com.tdd.talktobook.data.entity.request.interview.ai.StartInterviewRequestDto
import com.tdd.talktobook.data.service.ai.InterviewAIService
import io.ktor.client.statement.HttpResponse
import org.koin.core.annotation.Single

@Single(binds = [InterviewAIDataSource::class])
class InterviewAIDataSourceImpl(
    private val interviewAIService: InterviewAIService,
) : InterviewAIDataSource {
    override suspend fun postStartInterview(autobiographyId: Int, body: StartInterviewRequestDto): HttpResponse =
        interviewAIService.postStartInterview(autobiographyId, body)

    override suspend fun postChatInterview(autobiographyId: Int, body: ChatInterviewRequestDto): HttpResponse =
        interviewAIService.postChatInterview(autobiographyId, body)
}
