package com.tdd.bookshelf.domain.entity.response.publication

data class PublicationProgressModel(
    val publicationId: Int = 0,
    val bookId: Int = 0,
    val title: String = "",
    val coverImageUrl: String = "",
    val visibleScope: String = "",
    val page: Int = 0,
    val createdAt: String = "",
    val price: Int = 0,
    val titlePosition: String = "",
    val publishStatus: String = "",
    val requestedAt: String = "",
    val willPublishedAt: String = "",
)
