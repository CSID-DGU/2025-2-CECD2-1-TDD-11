package com.tdd.talktobook.domain.entity.response.auth

data class TokenModel (
    val accessToken: String = "",
    val refreshToken: String = "",
    val metadataSuccess: Boolean = false
)
