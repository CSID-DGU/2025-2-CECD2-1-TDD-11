package com.tdd.bookshelf.domain.usecase.publication

import com.tdd.bookshelf.domain.base.UseCase
import com.tdd.bookshelf.domain.entity.response.publication.PublicationProgressModel
import com.tdd.bookshelf.domain.repository.PublicationRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetPublicationProgressUseCase(
    private val repository: PublicationRepository,
) : UseCase<Int, Result<PublicationProgressModel>>() {
    override suspend fun invoke(request: Int): Flow<Result<PublicationProgressModel>> =
        repository.getPublicationProgress(request)
}
