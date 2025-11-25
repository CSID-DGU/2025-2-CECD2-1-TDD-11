package com.tdd.talktobook.domain.entity.response.interview

data class InterviewSummariesItemModel(
    val id: Int = 0,
    val totalMessageCount: Int = 0,
    val summary: String = "",
    val totalAnswerCount: Int = 0,
    val date: String = "",
)
