package com.tdd.bookshelf.data.mapper.publication

import com.tdd.bookshelf.data.base.BaseMapper
import com.tdd.bookshelf.data.entity.response.publication.PublicationProgressResponseDto
import com.tdd.bookshelf.domain.entity.response.publication.PublicationProgressModel
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

object PublishProgressMapper : BaseMapper() {
    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<PublicationProgressModel>> {
        return baseMapper(
            apiCall = { apiCall() },
            successDeserializer = PublicationProgressResponseDto.serializer(),
            responseToModel = { response ->
                response?.let { data ->
                    PublicationProgressModel(
                        publicationId = data.publicationId,
                        bookId = data.bookId,
                        title = data.title,
                        coverImageUrl = data.coverImageUrl,
                        visibleScope = data.visibleScope,
                        page = data.page,
                        createdAt = data.createdAt,
                        price = data.price,
                        titlePosition = data.titlePosition,
                        publishStatus = data.publishStatus,
                        requestedAt = data.requestedAt,
                        willPublishedAt = data.willPublishedAt,
                    )
                } ?: PublicationProgressModel()
            },
        )
    }
}
