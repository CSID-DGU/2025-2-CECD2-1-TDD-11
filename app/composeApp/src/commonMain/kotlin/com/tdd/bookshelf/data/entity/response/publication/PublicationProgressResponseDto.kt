package com.tdd.bookshelf.data.entity.response.publication

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PublicationProgressResponseDto(
    @SerialName("publicationId")
    val publicationId: Int,
    @SerialName("bookId")
    val bookId: Int,
    @SerialName("title")
    val title: String,
    @SerialName("coverImageUrl")
    val coverImageUrl: String,
    @SerialName("visibleScope")
    val visibleScope: String,
    @SerialName("page")
    val page: Int,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("price")
    val price: Int,
    @SerialName("titlePosition")
    val titlePosition: String,
    @SerialName("publishStatus")
    val publishStatus: String,
    @SerialName("requestedAt")
    val requestedAt: String,
    @SerialName("willPublishedAt")
    val willPublishedAt: String,
)
