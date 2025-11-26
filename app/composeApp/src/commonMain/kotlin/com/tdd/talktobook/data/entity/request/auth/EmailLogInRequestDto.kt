package com.tdd.talktobook.data.entity.request.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmailLogInRequestDto(
    @SerialName("email")
    val email: String = "",
    @SerialName("password")
    val password: String = "",
    @SerialName("deviceToken")
    val deviceToken: String = "",
)
