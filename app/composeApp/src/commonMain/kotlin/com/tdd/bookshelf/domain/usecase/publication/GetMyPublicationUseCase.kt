package com.tdd.bookshelf.domain.usecase.publication

import com.tdd.bookshelf.domain.base.UseCase
import com.tdd.bookshelf.domain.entity.request.default.GetQueryDefaultModel
import com.tdd.bookshelf.domain.entity.response.publication.PublishMyListModel
import com.tdd.bookshelf.domain.repository.PublicationRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetMyPublicationUseCase(
    private val repository: PublicationRepository,
) : UseCase<GetQueryDefaultModel, Result<PublishMyListModel>>() {
    override suspend fun invoke(request: GetQueryDefaultModel): Flow<Result<PublishMyListModel>> =
        repository.getMyPublication(request)
}
