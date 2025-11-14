package com.tdd.bookshelf.domain.entity.request.member

data class EditMemberInfoModel(
    val name: String = "",
    val bornedAt: String = "",
    val gender: String = "",
    val hasChildren: Boolean = false,
    val occupation: String = "",
    val educationLevel: String = "",
    val maritalStatus: String = "",
)
