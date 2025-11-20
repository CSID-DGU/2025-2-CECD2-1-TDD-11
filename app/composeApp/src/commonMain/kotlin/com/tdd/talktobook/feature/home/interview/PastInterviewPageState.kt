package com.tdd.talktobook.feature.home.interview

import com.tdd.talktobook.core.ui.base.PageState
import com.tdd.talktobook.domain.entity.response.interview.InterviewChatItem

data class PastInterviewPageState(
    val interviewList: List<InterviewChatItem> = emptyList(),
    val selectedDate: String = "",
) : PageState
