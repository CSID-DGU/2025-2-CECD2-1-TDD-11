package com.tdd.talktobook.data.entity.response.autobiography

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
        @SerialName("title")
        val title: String? = null,
        @SerialName("status")
        val status: String,
        @SerialName("contentPreview")
        val contentPreview: String? = null,
        @SerialName("coverImageUrl")
        val coverImageUrl: String? = null,
        @SerialName("createdAt")
        val createdAt: String,
        @SerialName("updatedAt")
        val updatedAt: String,
    )
}
