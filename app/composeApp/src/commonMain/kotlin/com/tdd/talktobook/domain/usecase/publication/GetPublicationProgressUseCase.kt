package com.tdd.talktobook.domain.usecase.publication

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.response.publication.PublicationProgressModel
import com.tdd.talktobook.domain.repository.PublicationRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetPublicationProgressUseCase(
    private val repository: PublicationRepository,
) : UseCase<Int, Result<PublicationProgressModel>>() {
    override suspend fun invoke(request: Int): Flow<Result<PublicationProgressModel>> =
        repository.getPublicationProgress(request)
}
