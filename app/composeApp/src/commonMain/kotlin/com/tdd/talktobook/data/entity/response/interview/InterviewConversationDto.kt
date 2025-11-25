package com.tdd.talktobook.data.entity.response.interview

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InterviewConversationDto(
    @SerialName("conversationId")
    val conversationId: Int = 0,
    @SerialName("content")
    val content: String = "",
    @SerialName("conversationType")
    val conversationType: String = "",
    @SerialName("createdAt")
    val createdAt: String = "",
)
