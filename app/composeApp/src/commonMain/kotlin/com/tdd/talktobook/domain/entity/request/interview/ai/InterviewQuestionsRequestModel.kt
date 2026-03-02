package com.tdd.talktobook.domain.entity.request.interview.ai

import com.tdd.talktobook.domain.entity.response.autobiography.ChapterInfoModel
import com.tdd.talktobook.domain.entity.request.member.MemberInfoModel

data class InterviewQuestionsRequestModel(
    val userInfo: MemberInfoModel = MemberInfoModel(),
    val chapterInfo: ChapterInfoModel = ChapterInfoModel(),
    val subChapterInfo: ChapterInfoModel = ChapterInfoModel(),
)
