package com.tdd.domain.entity.response.interview

data class InterviewChapterItem (
    val chapterId: Int = 0,
    val chapterNumber: String = "",
    val chapterName: String = "",
    val chapterDescription: String = "",
    val subChapters: List<InterviewSubChapterItem> = emptyList()
)