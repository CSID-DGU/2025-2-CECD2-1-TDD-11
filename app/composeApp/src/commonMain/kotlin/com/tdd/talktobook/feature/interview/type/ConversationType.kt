package com.tdd.talktobook.feature.interview.type

import com.tdd.talktobook.core.designsystem.BeforeStart
import com.tdd.talktobook.core.designsystem.InterviewFinishBtn
import com.tdd.talktobook.core.designsystem.InterviewIng
import com.tdd.talktobook.core.designsystem.InterviewStartBtn

enum class ConversationType(
    val type: String,
    val btnText: String,
) {
    BEFORE(BeforeStart, InterviewStartBtn),
    ING(InterviewIng, InterviewFinishBtn),
    ;

    companion object {
        fun getConversationBtnImg(type: ConversationType): String =
            ConversationType.entries.firstOrNull { it == type }?.btnText ?: ""
    }
}
