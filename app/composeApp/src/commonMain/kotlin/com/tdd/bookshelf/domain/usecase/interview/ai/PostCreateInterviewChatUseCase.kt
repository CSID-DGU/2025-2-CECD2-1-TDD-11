package com.tdd.bookshelf.domain.usecase.interview.ai

import com.tdd.bookshelf.domain.base.UseCase
import com.tdd.bookshelf.domain.entity.request.interview.ai.CreateInterviewChatRequestModel
import com.tdd.bookshelf.domain.repository.InterviewAIRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PostCreateInterviewChatUseCase(
    private val repository: InterviewAIRepository,
) : UseCase<CreateInterviewChatRequestModel, Result<String>>() {
    override suspend fun invoke(request: CreateInterviewChatRequestModel): Flow<Result<String>> =
        repository.postCreateInterviewChat(request)
}
