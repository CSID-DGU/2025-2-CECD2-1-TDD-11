package com.tdd.talktobook.domain.entity.response.interview.ai

data class StartInterviewResponseModel(
    val id: String = "",
    val material: String = "",
    val materialId: List<Int> = emptyList(),
    val text: String = "",
    val type: String = "",
)