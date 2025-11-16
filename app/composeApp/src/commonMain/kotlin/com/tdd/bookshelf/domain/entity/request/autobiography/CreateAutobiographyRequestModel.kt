package com.tdd.bookshelf.domain.entity.request.autobiography

data class CreateAutobiographyRequestModel(
    val title: String = "",
    val content: String = "",
    val preSignedCoverImageUrl: String = "",
    val interviewQuestions: List<InterviewQuestionModel> = emptyList(),
)
