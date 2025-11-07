package com.tdd.domain.entity.request

data class CreateUserModel (
    val age: String = "",
    val gender: String = "",
    val education: String = "",
    val marry: String = ""
)