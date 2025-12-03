package com.tdd.talktobook.data.entity.response.interview.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatInterviewResponseDto(
    @SerialName("last_answer_materials_id")
    val lastAnswerMaterialsId: List<List<Int>> = emptyList(),
    @SerialName("next_question")
    val nextQuestion: NextQuestion = NextQuestion(),
) {
    @Serializable
    data class NextQuestion(
        @SerialName("id")
        val id: String = "",
        @SerialName("material")
        val material: String = "",
        @SerialName("text")
        val text: String = "",
        @SerialName("type")
        val type: String = "",
        @SerialName("material_id")
        val materialId: List<Int> = emptyList(),
    ) {
        @Serializable
        data class Material(
            @SerialName("name")
            val materialName: String = "",
            @SerialName("order")
            val materialOrder: Int = 0,
        )
    }
}
