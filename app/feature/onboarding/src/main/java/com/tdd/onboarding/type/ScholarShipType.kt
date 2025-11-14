package com.tdd.onboarding.type

enum class ScholarShipType (
    val id: Int,
    val content: String,
    val api: String
) {
    PRIMARY(1, "초등학교 졸업", "초등학교"),
    JUNIORHIGH(2, "중학교 졸업", "중학교"),
    HIGH(3, "고등학교 졸업", "고등학교"),
    UNIVERSITY(4, "대학교 졸업", "대학교"),
    SCHOLAR(5, "석사", "석사"),
    DOCTOR(6, "박사 이상", "박사");
}