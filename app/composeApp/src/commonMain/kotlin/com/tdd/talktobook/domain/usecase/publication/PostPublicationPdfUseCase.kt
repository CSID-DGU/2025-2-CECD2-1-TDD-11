package com.tdd.talktobook.domain.usecase.publication

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.repository.PublicationRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PostPublicationPdfUseCase(
    private val repository: PublicationRepository,
): UseCase<Int, Result<String>>() {
    override suspend fun invoke(request: Int): Flow<Result<String>> =
        repository.postPublicationPdf(request)
}