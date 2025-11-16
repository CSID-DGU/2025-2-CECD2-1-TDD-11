package com.tdd.bookshelf.data.entity.request.interview.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoDto(
    @SerialName("user_name")
    val userName: String = "",
    @SerialName("date_of_birth")
    val birthDay: String = "",
    @SerialName("gender")
    val gender: String = "",
    @SerialName("has_children")
    val hasChildren: Boolean = false,
    @SerialName("occupation")
    val occupation: String = "",
    @SerialName("education_level")
    val educationLevel: String = "",
    @SerialName("marital_status")
    val maritalStatus: String = "",
)
