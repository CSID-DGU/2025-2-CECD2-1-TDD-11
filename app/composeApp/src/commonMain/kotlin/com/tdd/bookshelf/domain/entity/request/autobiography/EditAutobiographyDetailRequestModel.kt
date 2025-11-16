package com.tdd.bookshelf.domain.entity.request.autobiography

data class EditAutobiographyDetailRequestModel(
    val autobiographyId: Int = 0,
    val title: String = "",
    val content: String = "",
    val preSignedCoverImageUrl: String = "",
)
