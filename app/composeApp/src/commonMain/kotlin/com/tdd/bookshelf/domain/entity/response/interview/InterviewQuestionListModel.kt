package com.tdd.bookshelf.domain.entity.response.interview

data class InterviewQuestionListModel(
    val currentQuestionId: Int = 0,
    val results: List<InterviewQuestionItemModel> = emptyList(),
)
