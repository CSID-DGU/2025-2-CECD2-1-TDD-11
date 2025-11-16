package com.tdd.bookshelf.data.entity.response.publication

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PublishMyListResponseDto(
    @SerialName("results")
    val results: List<PublishResult> = emptyList(),
    @SerialName("currentPage")
    val currentPage: Int,
    @SerialName("totalElements")
    val totalElements: Int,
    @SerialName("totalPages")
    val totalPages: Int,
    @SerialName("hasNextPage")
    val hasNextPage: Boolean,
    @SerialName("hasPreviousPage")
    val hasPreviousPage: Boolean,
) {
    @Serializable
    data class PublishResult(
        @SerialName("bookId")
        val bookId: Int,
        @SerialName("publicationId")
        val publicationId: Int,
        @SerialName("title")
        val title: String,
        @SerialName("contentPreview")
        val contentPreview: String,
        @SerialName("coverImageUrl")
        val coverImageUrl: String,
        @SerialName("visibleScope")
        val visibleScope: String,
        @SerialName("page")
        val page: Int,
        @SerialName("createdAt")
        val createdAt: String,
    )
}
