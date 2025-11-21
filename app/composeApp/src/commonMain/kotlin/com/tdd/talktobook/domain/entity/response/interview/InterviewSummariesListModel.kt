package com.tdd.talktobook.domain.entity.response.interview

data class InterviewSummariesListModel (
    val interviews: List<InterviewSummariesItemModel> = emptyList()
)