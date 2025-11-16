package com.tdd.bookshelf.domain.entity.response.interview

import com.tdd.bookshelf.domain.entity.enums.ChatType

data class InterviewChatItem(
    val content: String = "",
    val chatType: ChatType = ChatType.BOT,
)
