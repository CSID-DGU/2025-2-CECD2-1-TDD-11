package com.tdd.talktobook.data.entity.response.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberInfoResponseDto(
    @SerialName("gender")
    val gender: String = "",
    @SerialName("occupation")
    val occupation: String? = null,
    @SerialName("ageGroup")
    val ageGroup: String? = null,
    @SerialName("succeessed")
    val success: Boolean = false
)
