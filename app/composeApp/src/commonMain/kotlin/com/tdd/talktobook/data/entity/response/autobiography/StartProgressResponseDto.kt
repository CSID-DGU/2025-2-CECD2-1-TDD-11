package com.tdd.talktobook.data.entity.response.autobiography

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StartProgressResponseDto (
    @SerialName("autobiographyId")
    val autobiographyId: Int = 0,
    @SerialName("interviewId")
    val interviewId: Int = 0
)