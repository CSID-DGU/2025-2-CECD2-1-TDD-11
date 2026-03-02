package com.tdd.talktobook.domain.entity.response.interview.ai

data class ChatInterviewResponseModel(
    val lastAnswerMaterialsId: List<List<Int>> = emptyList(),
    val id: String = "",
    val text: String = "",
    val type: String = "",
    val materialId: List<Int> = emptyList(),
    val material: String = "",
)
