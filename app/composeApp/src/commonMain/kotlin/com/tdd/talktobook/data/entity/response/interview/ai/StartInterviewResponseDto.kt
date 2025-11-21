package com.tdd.talktobook.data.entity.response.interview.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StartInterviewResponseDto (
    @SerialName("first_question")
    val firstQuestion: FirstQuestion = FirstQuestion()
) {
    @Serializable
    data class FirstQuestion(
        @SerialName("id")
        val id: String = "",
        @SerialName("material")
        val material: String = "",
        @SerialName("material_id")
        val materialId: List<Int> = emptyList(),
        @SerialName("text")
        val text: String = "",
        @SerialName("type")
        val type: String = ""
    )
}