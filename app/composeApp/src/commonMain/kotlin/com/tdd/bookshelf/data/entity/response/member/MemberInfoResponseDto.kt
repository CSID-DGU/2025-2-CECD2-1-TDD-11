package com.tdd.bookshelf.data.entity.response.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberInfoResponseDto(
    @SerialName("name")
    val name: String = "",
    @SerialName("bornedAt")
    val bornedAt: String = "",
    @SerialName("gender")
    val gender: String = "",
    @SerialName("hasChildren")
    val hasChildren: Boolean = false,
    @SerialName("occupation")
    val occupation: String? = null,
    @SerialName("educationLevel")
    val educationLevel: String? = null,
    @SerialName("maritalStatus")
    val maritalStatus: String? = null,
)
