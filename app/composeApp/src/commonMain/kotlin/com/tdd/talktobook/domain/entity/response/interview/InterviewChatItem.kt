package com.tdd.talktobook.domain.entity.response.interview

import com.tdd.talktobook.domain.entity.enums.ChatType

data class InterviewChatItem(
    val conversationId: Int = 0,
    val content: String = "",
    val chatType: ChatType = ChatType.BOT,
    val createdAt: String = "",
)
