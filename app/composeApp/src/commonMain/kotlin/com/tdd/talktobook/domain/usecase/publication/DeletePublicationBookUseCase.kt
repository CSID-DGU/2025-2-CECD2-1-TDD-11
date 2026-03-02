package com.tdd.talktobook.domain.usecase.publication

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.repository.PublicationRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class DeletePublicationBookUseCase(
    private val repository: PublicationRepository,
) : UseCase<Int, Result<Boolean>>() {
    override suspend fun invoke(request: Int): Flow<Result<Boolean>> =
        repository.deletePublicationBook(request)
}
