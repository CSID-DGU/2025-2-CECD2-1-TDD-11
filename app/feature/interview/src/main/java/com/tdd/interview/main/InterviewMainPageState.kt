package com.tdd.interview.main

import com.tdd.domain.entity.response.interview.InterviewChattingModel
import com.tdd.ui.base.PageState

data class InterviewMainPageState (
    val chattingList: List<InterviewChattingModel.Chatting> = emptyList()
): PageState