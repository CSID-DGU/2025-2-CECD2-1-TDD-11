package com.tdd.data.entity.request.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LogInRequestDto (
    @SerialName("device_id")
    val deviceId: String
)