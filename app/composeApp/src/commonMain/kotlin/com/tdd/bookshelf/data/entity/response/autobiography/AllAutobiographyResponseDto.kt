package com.tdd.bookshelf.data.entity.response.autobiography

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AllAutobiographyResponseDto(
    @SerialName("results")
    val results: List<AutobiographyItem> = emptyList(),
    @SerialName("currentPage")
    val currentPage: Int? = 0,
    @SerialName("totalPages")
    val totalPages: Int? = 0,
    @SerialName("totalElements")
    val totalElements: Int? = 0,
    @SerialName("isLast")
    val isLast: Boolean? = false,
) {
    @Serializable
    data class AutobiographyItem(
        @SerialName("autobiographyId")
        val autobiographyId: Int,
        @SerialName("interviewId")
        val interviewId: Int,
        @SerialName("chapterId")
        val chapterId: Int,
        @SerialName("title")
        val title: String,
        @SerialName("contentPreview")
        val contentPreview: String,
        @SerialName("coverImageUrl")
        val coverImageUrl: String?,
        @SerialName("createdAt")
        val createdAt: String,
        @SerialName("updatedAt")
        val updatedAt: String,
    )
}
