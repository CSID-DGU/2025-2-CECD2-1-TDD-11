package com.tdd.onboarding.type

enum class UserAgeType(
    val id: Int,
    val content: String,
    val api: String
) {
    TWENTY(1, "20대", "20"),
    THIRTY(2, "30대", "30"),
    FORTY(3, "40대", "40"),
    FIFTY(4, "50대", "50"),
    SIXTY(5, "60대", "60"),
    SEVENTY(6, "70대 이상", "70");
}