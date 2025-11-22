package com.tdd.talktobook.domain.entity.response.member

data class MemberInfoResponseModel (
    val gender: String = "",
    val occupation: String = "",
    val ageGroup: String = "",
    val successed: Boolean = false
)