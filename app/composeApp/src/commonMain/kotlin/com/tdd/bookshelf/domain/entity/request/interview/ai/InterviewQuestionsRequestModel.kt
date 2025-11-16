package com.tdd.bookshelf.domain.entity.request.interview.ai

import com.tdd.bookshelf.domain.entity.response.autobiography.ChapterInfoModel
import com.tdd.bookshelf.domain.entity.response.member.MemberInfoModel

data class InterviewQuestionsRequestModel(
    val userInfo: MemberInfoModel = MemberInfoModel(),
    val chapterInfo: ChapterInfoModel = ChapterInfoModel(),
    val subChapterInfo: ChapterInfoModel = ChapterInfoModel(),
)
