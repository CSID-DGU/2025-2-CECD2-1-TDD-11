package com.tdd.bookshelf.domain.entity.request.interview

import com.tdd.bookshelf.domain.entity.response.interview.InterviewChatItem

data class InterviewConversationRequestModel(
    val interviewId: Int = 0,
    val conversation: List<InterviewChatItem> = emptyList(),
)
