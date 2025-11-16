package com.tdd.bookshelf.data.entity.response.autobiography

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AutobiographyChapterResponseDto(
    @SerialName("currentChapterId")
    val currentChapterId: Int = 0,
    @SerialName("results")
    val results: List<ChapterListItem> = emptyList(),
) {
    @Serializable
    data class ChapterListItem(
        @SerialName("chapterId")
        val chapterId: Int = 0,
        @SerialName("chapterNumber")
        val chapterNumber: String = "",
        @SerialName("chapterName")
        val chapterName: String = "",
        @SerialName("chapterDescription")
        val chapterDescription: String = "",
        @SerialName("chapterCreatedAt")
        val chapterCreatedAt: String = "",
        @SerialName("subChapters")
        val subChapters: List<SubChapterItem> = emptyList(),
    ) {
        @Serializable
        data class SubChapterItem(
            @SerialName("chapterId")
            val chapterId: Int = 0,
            @SerialName("chapterNumber")
            val chapterNumber: String = "",
            @SerialName("chapterName")
            val chapterName: String = "",
            @SerialName("chapterDescription")
            val chapterDescription: String = "",
            @SerialName("chapterCreatedAt")
            val chapterCreatedAt: String = "",
        )
    }
}
