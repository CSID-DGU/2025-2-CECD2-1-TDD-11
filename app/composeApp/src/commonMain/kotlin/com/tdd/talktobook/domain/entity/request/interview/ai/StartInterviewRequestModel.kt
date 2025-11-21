package com.tdd.talktobook.domain.entity.request.interview.ai

data class StartInterviewRequestModel (
    val autobiographyId: Int = 0,
    val preferredCategories: List<Int> = emptyList()
)
