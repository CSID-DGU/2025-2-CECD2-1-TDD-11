package com.tdd.talktobook.domain.entity.response.interview

import com.tdd.talktobook.domain.entity.enums.ChatType

data class InterviewChatItem(
    val content: String = "",
    val chatType: ChatType = ChatType.BOT,
)
