package com.tdd.bookshelf.domain.entity.response.autobiography

data class AllAutobiographyItemModel(
    val autobiographyId: Int = 0,
    val interviewId: Int = 0,
    val chapterId: Int = 0,
    val title: String = "",
    val contentPreview: String = "",
    val coverImageUrl: String? = null,
    val createdAt: String = "",
    val updatedAt: String = "",
)
