package com.tdd.talktobook.domain.entity.response.interview.ai

data class ChatInterviewResponseModel(
    val lastAnswerMaterialsId: List<List<Int>> = emptyList(),
    val id: String = "",
    val material: String = "",
    val materialId: List<Int> = emptyList(),
    val text: String = "",
    val type: String = "",
)
