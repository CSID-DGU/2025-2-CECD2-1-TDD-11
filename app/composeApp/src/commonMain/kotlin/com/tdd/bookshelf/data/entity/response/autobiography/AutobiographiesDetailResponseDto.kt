package com.tdd.bookshelf.data.entity.response.autobiography

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AutobiographiesDetailResponseDto(
    @SerialName("autobiographyId")
    val autobiographyId: Int = 0,
    @SerialName("interviewId")
    val interviewId: Int = 0,
    @SerialName("title")
    val title: String = "",
    @SerialName("content")
    val content: String = "",
    @SerialName("coverImageUrl")
    val coverImageUrl: String? = null,
    @SerialName("createdAt")
    val createdAt: String = "",
    @SerialName("updatedAt")
    val updatedAt: String = "",
)
