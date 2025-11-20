package com.tdd.talktobook.domain.entity.request.interview

import com.tdd.talktobook.domain.entity.response.interview.InterviewChatItem

data class InterviewConversationRequestModel(
    val interviewId: Int = 0,
    val conversation: List<InterviewChatItem> = emptyList(),
)
