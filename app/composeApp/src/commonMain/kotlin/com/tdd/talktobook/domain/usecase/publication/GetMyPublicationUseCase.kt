package com.tdd.talktobook.domain.usecase.publication

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.request.default.GetQueryDefaultModel
import com.tdd.talktobook.domain.entity.response.publication.PublishMyListModel
import com.tdd.talktobook.domain.repository.PublicationRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetMyPublicationUseCase(
    private val repository: PublicationRepository,
) : UseCase<GetQueryDefaultModel, Result<PublishMyListModel>>() {
    override suspend fun invoke(request: GetQueryDefaultModel): Flow<Result<PublishMyListModel>> =
        repository.getMyPublication(request)
}
