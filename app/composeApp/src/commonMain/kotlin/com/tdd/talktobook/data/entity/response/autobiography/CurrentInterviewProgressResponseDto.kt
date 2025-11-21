package com.tdd.talktobook.data.entity.response.autobiography

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentInterviewProgressResponseDto (
    @SerialName("progressPercentage")
    val progressPercentage: Double = 0.0,
    @SerialName("status")
    val status: String = ""
)