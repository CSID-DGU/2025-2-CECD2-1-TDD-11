package com.tdd.bookshelf.domain.entity.response.member

data class MemberProfileModel(
    val memberId: Int = 0,
    val nickname: String = "",
    val profileImageUrl: String? = null,
)
