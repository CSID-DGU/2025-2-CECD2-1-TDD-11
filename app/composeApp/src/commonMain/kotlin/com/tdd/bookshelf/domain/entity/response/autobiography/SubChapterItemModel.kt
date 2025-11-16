package com.tdd.bookshelf.domain.entity.response.autobiography

data class SubChapterItemModel(
    val chapterId: Int = 0,
    val chapterNumber: String = "",
    val chapterName: String = "",
    val chapterDescription: String = "",
    val chapterCreatedAt: String = "",
)
