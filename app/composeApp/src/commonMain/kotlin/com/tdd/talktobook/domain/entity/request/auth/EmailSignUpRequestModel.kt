package com.tdd.talktobook.domain.entity.request.auth

data class EmailSignUpRequestModel(
    val email: String = "",
    val password: String = "",
)
