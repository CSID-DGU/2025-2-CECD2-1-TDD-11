package com.tdd.bookshelf.data.entity.response.interview

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InterviewQuestionListResponseDto(
    @SerialName("currentQuestionId")
    val currentQuestionId: Int = 0,
    @SerialName("results")
    val results: List<InterviewQuestion> = emptyList(),
) {
    @Serializable
    data class InterviewQuestion(
        @SerialName("questionId")
        val questionId: Int = 0,
        @SerialName("order")
        val order: Int = 0,
        @SerialName("questionText")
        val questionText: String = "",
    )
}
