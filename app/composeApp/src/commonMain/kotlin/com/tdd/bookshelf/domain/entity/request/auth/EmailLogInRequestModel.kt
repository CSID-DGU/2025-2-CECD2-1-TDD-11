package com.tdd.bookshelf.domain.entity.request.auth

data class EmailLogInRequestModel(
    val email: String = "",
    val password: String = "",
    val deviceToken: String = "",
)
