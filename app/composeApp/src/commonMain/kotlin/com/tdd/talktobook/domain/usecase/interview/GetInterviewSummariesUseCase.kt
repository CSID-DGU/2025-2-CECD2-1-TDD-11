package com.tdd.talktobook.domain.usecase.interview

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.request.interview.InterviewSummariesRequestModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewSummariesListModel
import com.tdd.talktobook.domain.repository.InterviewRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetInterviewSummariesUseCase(
    private val repository: InterviewRepository,
) : UseCase<InterviewSummariesRequestModel, Result<InterviewSummariesListModel>>() {
    override suspend fun invoke(request: InterviewSummariesRequestModel): Flow<Result<InterviewSummariesListModel>> =
        repository.getInterviewSummaries(request)
}