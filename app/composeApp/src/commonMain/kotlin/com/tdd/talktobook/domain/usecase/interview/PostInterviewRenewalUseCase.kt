package com.tdd.talktobook.domain.usecase.interview

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.repository.InterviewRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PostInterviewRenewalUseCase(
    private val repository: InterviewRepository,
) : UseCase<Int, Result<Boolean>>() {
    override suspend fun invoke(request: Int): Flow<Result<Boolean>> =
        repository.postInterviewRenewal(request)
}
