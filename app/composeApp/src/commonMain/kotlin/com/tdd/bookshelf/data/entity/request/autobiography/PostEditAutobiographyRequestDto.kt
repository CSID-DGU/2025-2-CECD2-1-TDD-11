package com.tdd.bookshelf.data.entity.request.autobiography

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostEditAutobiographyRequestDto(
    @SerialName("title")
    val title: String = "",
    @SerialName("content")
    val content: String = "",
    @SerialName("preSignedCoverImageUrl")
    val preSignedCoverImageUrl: String = "",
)
