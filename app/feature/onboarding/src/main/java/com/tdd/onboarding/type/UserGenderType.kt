package com.tdd.onboarding.type

enum class UserGenderType (
    val id: Int,
    val content: String,
    val api: String
) {
    FEMALE(1, "여자", "FEMALE"),
    MALE(2, "남자", "MALE");
}