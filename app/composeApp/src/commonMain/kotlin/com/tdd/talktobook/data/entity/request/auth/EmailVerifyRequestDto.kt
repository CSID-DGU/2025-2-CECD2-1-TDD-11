package com.tdd.talktobook.data.entity.request.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmailVerifyRequestDto(
    @SerialName("email")
    val email: String,
    @SerialName("verificationCode")
    val code: String,
)
