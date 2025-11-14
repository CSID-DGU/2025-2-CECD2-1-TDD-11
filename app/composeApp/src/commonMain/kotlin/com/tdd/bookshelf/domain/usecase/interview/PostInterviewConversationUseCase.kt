package com.tdd.bookshelf.domain.usecase.interview

import com.tdd.bookshelf.domain.base.UseCase
import com.tdd.bookshelf.domain.entity.request.interview.InterviewConversationRequestModel
import com.tdd.bookshelf.domain.repository.InterviewRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PostInterviewConversationUseCase(
    private val repository: InterviewRepository,
) : UseCase<InterviewConversationRequestModel, Result<Boolean>>() {
    override suspend fun invoke(request: InterviewConversationRequestModel): Flow<Result<Boolean>> =
        repository.postInterviewConversation(request)
}
