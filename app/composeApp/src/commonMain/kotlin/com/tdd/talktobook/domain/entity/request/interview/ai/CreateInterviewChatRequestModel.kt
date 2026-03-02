package com.tdd.talktobook.domain.entity.request.interview.ai

import com.tdd.talktobook.domain.entity.response.autobiography.ChapterInfoModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewChatItem
import com.tdd.talktobook.domain.entity.response.member.MemberInfoModel

data class CreateInterviewChatRequestModel(
    val userInfo: MemberInfoModel = MemberInfoModel(),
    val chapterInfo: ChapterInfoModel = ChapterInfoModel(),
    val subChapterInfo: ChapterInfoModel = ChapterInfoModel(),
    val conversationHistory: List<InterviewChatItem> = emptyList(),
    val currentAnswer: String = "",
    val questionLimit: Int = 0,
)
