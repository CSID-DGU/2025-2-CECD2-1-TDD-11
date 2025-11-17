package com.tdd.bookshelf.feature.home.interview

import com.tdd.bookshelf.core.ui.base.PageState
import com.tdd.bookshelf.domain.entity.response.interview.InterviewChatItem

data class PastInterviewPageState(
    val interviewList: List<InterviewChatItem> = emptyList(),
    val selectedDate: String = "",
) : PageState
