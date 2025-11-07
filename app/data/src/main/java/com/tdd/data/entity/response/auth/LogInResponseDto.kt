package com.tdd.data.entity.response.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LogInResponseDto (
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("profile_completed")
    val profileCompleted: Boolean = false
)