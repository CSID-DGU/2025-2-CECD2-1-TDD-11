package com.tdd.talktobook.domain.entity.request.member

data class EditMemberInfoModel(
    val gender: String = "",
    val occupation: String = "",
    val ageGroup: String = "",
)
