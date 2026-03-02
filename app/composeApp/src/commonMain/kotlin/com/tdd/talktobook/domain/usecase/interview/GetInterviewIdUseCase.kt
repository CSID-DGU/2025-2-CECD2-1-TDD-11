package com.tdd.talktobook.domain.usecase.interview

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.repository.InterviewRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetInterviewIdUseCase(
    private val repository: InterviewRepository,
) : UseCase<Unit, Result<Int>>() {
    override suspend fun invoke(request: Unit): Flow<Result<Int>> =
        repository.getInterviewId()
}
