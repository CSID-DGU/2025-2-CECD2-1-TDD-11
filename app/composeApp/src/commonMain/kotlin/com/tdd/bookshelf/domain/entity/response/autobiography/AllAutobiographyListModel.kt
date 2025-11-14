package com.tdd.bookshelf.domain.entity.response.autobiography

data class AllAutobiographyListModel(
    val results: List<AllAutobiographyItemModel> = emptyList(),
    val currentPage: Int? = 0,
    val totalPages: Int? = 0,
    val totalElements: Int? = 0,
    val isLast: Boolean? = false,
)
