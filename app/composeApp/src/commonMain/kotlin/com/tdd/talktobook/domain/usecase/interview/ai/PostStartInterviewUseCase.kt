package com.tdd.talktobook.domain.usecase.interview.ai

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.request.interview.ai.StartInterviewRequestModel
import com.tdd.talktobook.domain.entity.response.interview.ai.StartInterviewResponseModel
import com.tdd.talktobook.domain.repository.InterviewAIRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PostStartInterviewUseCase(
    private val repository: InterviewAIRepository
): UseCase<StartInterviewRequestModel, Result<StartInterviewResponseModel>>() {
    override suspend fun invoke(request: StartInterviewRequestModel): Flow<Result<StartInterviewResponseModel>> =
        repository.postStartInterview(request)
}