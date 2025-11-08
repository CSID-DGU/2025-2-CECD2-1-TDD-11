package com.tdd.domain.entity.response.interview

data class InterviewChapterModel (
    val currentChapterId: Int = 0,
    val chapters: List<InterviewChapterItem> = emptyList()
)