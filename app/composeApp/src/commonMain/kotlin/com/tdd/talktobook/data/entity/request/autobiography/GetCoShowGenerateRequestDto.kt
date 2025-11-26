package com.tdd.talktobook.data.entity.request.autobiography

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetCoShowGenerateRequestDto (
    @SerialName("name")
    val name: String = ""
)