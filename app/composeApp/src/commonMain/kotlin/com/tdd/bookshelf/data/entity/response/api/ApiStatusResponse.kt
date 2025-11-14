package com.tdd.bookshelf.data.entity.response.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiStatusResponse(
    @SerialName("statusCode")
    val statusCode: Int? = null,
    @SerialName("code")
    val code: String? = null,
    @SerialName("message")
    val message: String? = null,
)
