package com.tdd.bookshelf.data.entity.response.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberProfileResponseDto(
    @SerialName("memberId")
    val memberId: Int = 0,
    @SerialName("nickname")
    val nickname: String = "",
    @SerialName("profileImageUrl")
    val profileImageUrl: String? = null,
)
