package com.tdd.bookshelf.data.entity.request.autobiography

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostCreateAutobiographyChapterRequestDto(
    @SerialName("chapters")
    val chapters: List<ChapterItem> = emptyList(),
) {
    @Serializable
    data class ChapterItem(
        @SerialName("number")
        val number: String = "",
        @SerialName("name")
        val name: String = "",
        @SerialName("description")
        val description: String = "",
        @SerialName("subchapters")
        val subchapters: List<SubChapterItem> = emptyList(),
    ) {
        @Serializable
        data class SubChapterItem(
            @SerialName("number")
            val number: String = "",
            @SerialName("name")
            val name: String = "",
            @SerialName("description")
            val description: String = "",
        )
    }
}
