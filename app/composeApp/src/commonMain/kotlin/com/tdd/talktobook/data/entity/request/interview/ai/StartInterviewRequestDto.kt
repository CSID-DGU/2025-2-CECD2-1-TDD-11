package com.tdd.talktobook.data.entity.request.interview.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StartInterviewRequestDto(
    @SerialName("preferred_categories")
    val preferredCategories: List<Int> = emptyList(),
)
