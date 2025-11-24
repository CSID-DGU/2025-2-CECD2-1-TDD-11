package com.tdd.talktobook.data.entity.request.interview.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatInterviewRequestDto(
    @SerialName("answer_text")
    val answerText: String = "",
)
