package com.tdd.bookshelf.data.entity.request.interview.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChapterInfoDto(
    @SerialName("title")
    val title: String = "",
    @SerialName("description")
    val description: String = "",
)
