package com.tdd.talktobook.data.entity.response.interview

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InterviewSummariesResponseDto (
    @SerialName("interviews")
    val interviews: List<Interview> = emptyList()
) {
    @Serializable
    data class Interview(
        @SerialName("id")
        val id: Int = 0,
        @SerialName("totalMessageCount")
        val totalMessageCount: Int = 0,
        @SerialName("summary")
        val summary: String = "",
        @SerialName("totalAnswerCount")
        val totalAnswerCount: Int = 0,
        @SerialName("date")
        val date: String = ""
    )
}