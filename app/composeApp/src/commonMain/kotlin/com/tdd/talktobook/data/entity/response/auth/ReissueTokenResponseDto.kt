package com.tdd.talktobook.data.entity.response.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReissueTokenResponseDto (
    @SerialName("accessToken")
    val accessToken: String = "",
    @SerialName("refreshToken")
    val refreshToken: String = "",
)