package com.tdd.bookshelf.domain.usecase.interview

import com.tdd.bookshelf.domain.base.UseCase
import com.tdd.bookshelf.domain.entity.response.interview.InterviewQuestionListModel
import com.tdd.bookshelf.domain.repository.InterviewRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetInterviewQuestionListUseCase(
    private val repository: InterviewRepository,
) : UseCase<Int, Result<InterviewQuestionListModel>>() {
    override suspend fun invoke(request: Int): Flow<Result<InterviewQuestionListModel>> =
        repository.getInterviewQuestionList(request)
}
