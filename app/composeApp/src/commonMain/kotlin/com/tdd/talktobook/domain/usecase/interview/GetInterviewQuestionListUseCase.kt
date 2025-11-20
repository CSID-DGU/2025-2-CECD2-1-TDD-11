package com.tdd.talktobook.domain.usecase.interview

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.response.interview.InterviewQuestionListModel
import com.tdd.talktobook.domain.repository.InterviewRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetInterviewQuestionListUseCase(
    private val repository: InterviewRepository,
) : UseCase<Int, Result<InterviewQuestionListModel>>() {
    override suspend fun invoke(request: Int): Flow<Result<InterviewQuestionListModel>> =
        repository.getInterviewQuestionList(request)
}
