package com.tdd.talktobook.domain.entity.request.auth

data class EmailVerifyRequestModel(
    val email: String,
    val code: String,
)