package com.tdd.talktobook.data.entity.response.autobiography

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AutobiographyIdResponseDto(
    @SerialName("autobiographyId")
    val autobiographyId: Int = 0,
)
