package com.tdd.talktobook.data.entity.response.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthErrorResponse(
    val statusCode: Int? = null,
    val code: String? = null,
    val message: String? = null,
)
