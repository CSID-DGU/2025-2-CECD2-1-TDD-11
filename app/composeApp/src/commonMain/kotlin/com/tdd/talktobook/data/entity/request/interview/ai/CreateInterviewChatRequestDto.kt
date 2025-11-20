package com.tdd.talktobook.data.entity.request.interview.ai

import com.tdd.talktobook.data.entity.response.interview.InterviewConversationDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateInterviewChatRequestDto(
    @SerialName("user_info")
    val userInfo: UserInfoDto = UserInfoDto(),
    @SerialName("chapter_info")
    val chapterInfo: ChapterInfoDto = ChapterInfoDto(),
    @SerialName("sub_chapter_info")
    val subChapterInfo: ChapterInfoDto = ChapterInfoDto(),
    @SerialName("conversation_history")
    val conversationHistory: List<InterviewConversationDto> = emptyList(),
    @SerialName("current_answer")
    val currentAnswer: String = "",
    @SerialName("question_limit")
    val questionLimit: Int = 0,
)
