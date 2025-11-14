package com.tdd.bookshelf.data.entity.response.interview

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InterviewConversationResponseDto(
    @SerialName("results")
    val results: List<InterviewConversationDto> = emptyList(),
    @SerialName("currentPage")
    val currentPage: Int = 0,
    @SerialName("totalElements")
    val totalElements: Int = 0,
    @SerialName("totalPages")
    val totalPages: Int = 0,
    @SerialName("hasNextPage")
    val hasNextPage: Boolean = false,
    @SerialName("asPreviousPage")
    val hasPreviousPage: Boolean = false,
)
