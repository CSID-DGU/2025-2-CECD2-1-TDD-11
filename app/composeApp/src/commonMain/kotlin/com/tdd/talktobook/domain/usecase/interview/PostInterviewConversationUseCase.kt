package com.tdd.talktobook.domain.usecase.interview

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.request.interview.InterviewConversationRequestModel
import com.tdd.talktobook.domain.repository.InterviewRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PostInterviewConversationUseCase(
    private val repository: InterviewRepository,
) : UseCase<InterviewConversationRequestModel, Result<Boolean>>() {
    override suspend fun invoke(request: InterviewConversationRequestModel): Flow<Result<Boolean>> =
        repository.postInterviewConversation(request)
}
