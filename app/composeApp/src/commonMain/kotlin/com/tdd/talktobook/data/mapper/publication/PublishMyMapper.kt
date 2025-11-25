package com.tdd.talktobook.data.mapper.publication

import com.tdd.talktobook.data.base.BaseMapper
import com.tdd.talktobook.data.entity.response.publication.PublishMyListResponseDto
import com.tdd.talktobook.domain.entity.response.publication.PublishMyListItemModel
import com.tdd.talktobook.domain.entity.response.publication.PublishMyListModel
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

object PublishMyMapper : BaseMapper() {
    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<PublishMyListModel>> {
        return baseMapper(
            apiCall = { apiCall() },
            successDeserializer = PublishMyListResponseDto.serializer(),
            responseToModel = { response ->
                response?.let { data ->
                    PublishMyListModel(
                        results =
                            data.results.map { item ->
                                PublishMyListItemModel(
                                    bookId = item.bookId,
                                    publicationId = item.publicationId,
                                    title = item.title,
                                    contentPreview = item.contentPreview,
                                    coverImageUrl = item.coverImageUrl,
                                    visibleScope = item.visibleScope,
                                    page = item.page,
                                    createdAt = item.createdAt,
                                )
                            },
                        currentPage = data.currentPage,
                        totalElements = data.totalElements,
                        totalPages = data.totalPages,
                        hasNextPage = data.hasNextPage,
                        hasPreviousPage = data.hasPreviousPage,
                    )
                } ?: PublishMyListModel()
            },
        )
    }
}
