package com.tdd.bookshelf.feature.interview

import com.tdd.bookshelf.core.ui.base.PageState
import com.tdd.bookshelf.domain.entity.response.interview.InterviewChatItem
import com.tdd.bookshelf.domain.entity.response.interview.InterviewConversationListModel
import com.tdd.bookshelf.domain.entity.response.interview.InterviewQuestionItemModel
import com.tdd.bookshelf.feature.interview.type.ConversationType

data class InterviewPageState(
    val interviewConversationModel: InterviewConversationListModel = InterviewConversationListModel(),
    val interviewChatList: List<InterviewChatItem> = emptyList(),
    val interviewId: Int = 0,
    val interviewQuestionList: List<InterviewQuestionItemModel> = emptyList(),
    val interviewCurrentQuestionId: Int = 0,
    val interviewProgressType: ConversationType = ConversationType.BEFORE,
) : PageState
