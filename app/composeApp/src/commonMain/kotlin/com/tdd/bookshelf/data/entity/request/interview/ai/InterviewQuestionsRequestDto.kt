package com.tdd.bookshelf.data.entity.request.interview.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InterviewQuestionsRequestDto(
    @SerialName("user_info")
    val userInfo: UserInfoDto = UserInfoDto(),
    @SerialName("chapter_info")
    val chapterInfo: ChapterInfoDto = ChapterInfoDto(),
    @SerialName("sub_chapter_info")
    val subChapterInfo: ChapterInfoDto = ChapterInfoDto(),
)
