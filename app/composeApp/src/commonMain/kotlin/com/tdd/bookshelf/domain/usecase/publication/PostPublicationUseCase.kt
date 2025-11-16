package com.tdd.bookshelf.domain.usecase.publication

import com.tdd.bookshelf.domain.entity.request.publication.PostPublicationModel
import com.tdd.bookshelf.domain.base.UseCase
import com.tdd.bookshelf.domain.repository.PublicationRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PostPublicationUseCase(
    private val repository: PublicationRepository,
) : UseCase<PostPublicationModel, Result<Boolean>>() {
    override suspend fun invoke(request: PostPublicationModel): Flow<Result<Boolean>> =
        repository.postPublication(request)
}
