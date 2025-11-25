package com.tdd.talktobook.domain.entity.response.autobiography

data class AllAutobiographyItemModel(
    val autobiographyId: Int = 0,
    val title: String = "",
    val status: String = "",
    val contentPreview: String = "",
    val coverImageUrl: String? = null,
    val createdAt: String = "",
    val updatedAt: String = "",
)
