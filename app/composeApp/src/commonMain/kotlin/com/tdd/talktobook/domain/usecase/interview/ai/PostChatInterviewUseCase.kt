package com.tdd.talktobook.domain.usecase.interview.ai

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.request.interview.ai.ChatInterviewRequestModel
import com.tdd.talktobook.domain.entity.response.interview.ai.ChatInterviewResponseModel
import com.tdd.talktobook.domain.repository.InterviewAIRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PostChatInterviewUseCase(
    private val repository: InterviewAIRepository,
) : UseCase<ChatInterviewRequestModel, Result<ChatInterviewResponseModel>>() {
    override suspend fun invoke(request: ChatInterviewRequestModel): Flow<Result<ChatInterviewResponseModel>> =
        repository.postChatInterview(request)
}