package com.tdd.bookshelf.domain.usecase.publication

import com.tdd.bookshelf.domain.base.UseCase
import com.tdd.bookshelf.domain.repository.PublicationRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class DeletePublicationBookUseCase(
    private val repository: PublicationRepository,
) : UseCase<Int, Result<Boolean>>() {
    override suspend fun invoke(request: Int): Flow<Result<Boolean>> =
        repository.deletePublicationBook(request)
}
