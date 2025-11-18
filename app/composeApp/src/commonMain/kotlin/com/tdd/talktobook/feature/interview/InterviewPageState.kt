package com.tdd.talktobook.feature.interview

import com.tdd.talktobook.core.ui.base.PageState
import com.tdd.talktobook.domain.entity.response.interview.InterviewChatItem
import com.tdd.talktobook.domain.entity.response.interview.InterviewConversationListModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewQuestionItemModel
import com.tdd.talktobook.feature.interview.type.ConversationType

data class InterviewPageState(
    val interviewConversationModel: InterviewConversationListModel = InterviewConversationListModel(),
    val interviewChatList: List<InterviewChatItem> = emptyList(),
    val interviewId: Int = 0,
    val interviewQuestionList: List<InterviewQuestionItemModel> = emptyList(),
    val interviewCurrentQuestionId: Int = 0,
    val interviewProgressType: ConversationType = ConversationType.BEFORE,
) : PageState
