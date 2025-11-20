package com.tdd.talktobook.domain.entity.request.publication

import com.tdd.talktobook.domain.entity.enums.BookTitlePositionType

data class PostPublicationModel(
    val title: String = "",
    val preCoverImage: String = "",
    val titlePosition: BookTitlePositionType = BookTitlePositionType.TOP,
)
