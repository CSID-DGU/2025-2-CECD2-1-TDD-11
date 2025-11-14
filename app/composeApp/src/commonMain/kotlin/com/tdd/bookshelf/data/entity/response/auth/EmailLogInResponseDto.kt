package com.tdd.bookshelf.data.entity.response.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmailLogInResponseDto(
    @SerialName("accessToken")
    val accessToken: String = "",
)
