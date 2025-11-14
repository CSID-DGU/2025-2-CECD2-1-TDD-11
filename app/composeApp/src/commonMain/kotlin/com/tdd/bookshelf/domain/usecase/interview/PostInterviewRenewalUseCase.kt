package com.tdd.bookshelf.domain.usecase.interview

import com.tdd.bookshelf.domain.base.UseCase
import com.tdd.bookshelf.domain.repository.InterviewRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PostInterviewRenewalUseCase(
    private val repository: InterviewRepository,
) : UseCase<Int, Result<Boolean>>() {
    override suspend fun invoke(request: Int): Flow<Result<Boolean>> =
        repository.postInterviewRenewal(request)
}
