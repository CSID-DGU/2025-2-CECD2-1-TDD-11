package com.tdd.bookshelf.domain.entity.response.publication

data class PublishMyListItemModel(
    val bookId: Int = 0,
    val publicationId: Int = 0,
    val title: String = "",
    val contentPreview: String = "",
    val coverImageUrl: String = "",
    val visibleScope: String = "",
    val page: Int = 0,
    val createdAt: String = "",
)
