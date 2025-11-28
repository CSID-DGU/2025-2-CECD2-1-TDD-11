package com.tdd.talktobook.data.entity.response.interview

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoShowAnswerResponseDto(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("order")
    val order: Int = 0,
    @SerialName("question")
    val question: String = "",
    @SerialName("isLast")
    val isLast: Boolean = false,
)
