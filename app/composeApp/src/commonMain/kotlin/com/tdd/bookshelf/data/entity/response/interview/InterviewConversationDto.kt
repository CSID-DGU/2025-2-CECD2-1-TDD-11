package com.tdd.bookshelf.data.entity.response.interview

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InterviewConversationDto(
    @SerialName("content")
    val content: String = "",
    @SerialName("conversationType")
    val conversationType: String = "",
)
