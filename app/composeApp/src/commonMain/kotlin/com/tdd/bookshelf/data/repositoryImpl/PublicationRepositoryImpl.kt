package com.tdd.bookshelf.data.repositoryImpl

import com.tdd.bookshelf.data.dataSource.PublicationDataSource
import com.tdd.bookshelf.data.mapper.base.DefaultBooleanMapper
import com.tdd.bookshelf.data.mapper.publication.PublishMyMapper
import com.tdd.bookshelf.data.mapper.publication.PublishProgressMapper
import com.tdd.bookshelf.domain.entity.request.default.GetQueryDefaultModel
import com.tdd.bookshelf.domain.entity.request.publication.PostPublicationModel
import com.tdd.bookshelf.domain.entity.response.publication.PublicationProgressModel
import com.tdd.bookshelf.domain.entity.response.publication.PublishMyListModel
import com.tdd.bookshelf.domain.repository.PublicationRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single(binds = [PublicationRepository::class])
class PublicationRepositoryImpl(
    private val publicationDataSource: PublicationDataSource,
) : PublicationRepository {
    override suspend fun postPublication(request: PostPublicationModel): Flow<Result<Boolean>> =
        DefaultBooleanMapper.responseToModel(apiCall = {
            publicationDataSource.postPublication(
                request.title,
                request.preCoverImage,
                request.titlePosition.value,
            )
        })

    override suspend fun getMyPublication(request: GetQueryDefaultModel): Flow<Result<PublishMyListModel>> =
        PublishMyMapper.responseToModel(apiCall = {
            publicationDataSource.getMyPublication(
                request.page,
                request.size,
            )
        })

    override suspend fun getPublicationProgress(request: Int): Flow<Result<PublicationProgressModel>> =
        PublishProgressMapper.responseToModel(apiCall = {
            publicationDataSource.getPublicationProgress(
                request,
            )
        })

    override suspend fun deletePublicationBook(bookId: Int): Flow<Result<Boolean>> =
        DefaultBooleanMapper.responseToModel(apiCall = {
            publicationDataSource.deletePublicationBook(
                bookId,
            )
        })
}
