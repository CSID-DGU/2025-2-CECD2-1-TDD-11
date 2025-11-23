package com.tdd.talktobook.data.entity.response.autobiography

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SelectedThemeResponseDto (
    @SerialName("name")
    val name: String = "",
    @SerialName("categories")
    val categories: List<Int> = emptyList()
)