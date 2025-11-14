package com.tdd.bookshelf.domain.usecase.interview

import com.tdd.bookshelf.domain.base.UseCase
import com.tdd.bookshelf.domain.entity.response.interview.InterviewConversationListModel
import com.tdd.bookshelf.domain.repository.InterviewRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetInterviewConversationUseCase(
    private val repository: InterviewRepository,
) : UseCase<Int, Result<InterviewConversationListModel>>() {
    override suspend fun invoke(request: Int): Flow<Result<InterviewConversationListModel>> =
        repository.getInterviewConversation(request)
}
