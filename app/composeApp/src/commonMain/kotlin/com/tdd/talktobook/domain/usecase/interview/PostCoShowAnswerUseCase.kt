package com.tdd.talktobook.domain.usecase.interview

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.request.interview.CoShowAnswerRequestModel
import com.tdd.talktobook.domain.entity.response.interview.CoShowAnswerModel
import com.tdd.talktobook.domain.repository.InterviewRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PostCoShowAnswerUseCase(
    private val repository: InterviewRepository,
) : UseCase<CoShowAnswerRequestModel, Result<CoShowAnswerModel>>() {
    override suspend fun invoke(request: CoShowAnswerRequestModel): Flow<Result<CoShowAnswerModel>> =
        repository.postCoShowAnswer(request)
}
