package com.tdd.bookshelf.data.entity.request.interview

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InterviewConversationRequestDto(
    @SerialName("conversations")
    val conversations: List<InterviewConversation> = emptyList(),
) {
    @Serializable
    data class InterviewConversation(
        @SerialName("content")
        val content: String = "",
        @SerialName("conversationType")
        val conversationType: String = "",
    )
}
