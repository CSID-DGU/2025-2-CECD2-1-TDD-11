package com.tdd.talktobook.domain.entity.response.autobiography

data class ChapterListModel(
    val currentChapterId: Int = 0,
    val results: List<ChapterItemModel> = emptyList(),
)
