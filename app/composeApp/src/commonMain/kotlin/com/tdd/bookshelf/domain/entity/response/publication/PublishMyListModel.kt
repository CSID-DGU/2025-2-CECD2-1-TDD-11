package com.tdd.bookshelf.domain.entity.response.publication

data class PublishMyListModel(
    val results: List<PublishMyListItemModel> = emptyList(),
    val currentPage: Int = 0,
    val totalElements: Int = 0,
    val totalPages: Int = 0,
    val hasNextPage: Boolean = false,
    val hasPreviousPage: Boolean = false,
)
