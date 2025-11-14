package com.tdd.interview.start

import com.tdd.domain.entity.response.interview.InterviewChattingModel
import com.tdd.ui.base.PageState

data class InterviewPageState (
    val chattingList: List<InterviewChattingModel.Chatting> = emptyList()
): PageState