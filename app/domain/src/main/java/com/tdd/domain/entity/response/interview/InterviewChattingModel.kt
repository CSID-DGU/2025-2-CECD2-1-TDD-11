package com.tdd.domain.entity.response.interview

import com.tdd.domain.entity.enum.ChattingType

data class InterviewChattingModel (
    val chattingList: List<Chatting> = emptyList()
) {
    data class Chatting(
        val content: String = "",
        val type: ChattingType = ChattingType.MIRROR
    )
}