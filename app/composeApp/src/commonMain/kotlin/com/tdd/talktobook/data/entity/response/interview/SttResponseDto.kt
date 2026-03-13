package com.tdd.talktobook.data.entity.response.interview

import kotlinx.serialization.Serializable

@Serializable
data class SttResponseDto(
    val type: String = "",
    val text: String = "",
)
