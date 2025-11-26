package com.tdd.talktobook.feature.interview.type

import com.tdd.talktobook.core.designsystem.BeforeStart
import com.tdd.talktobook.core.designsystem.FinishRequest
import com.tdd.talktobook.core.designsystem.InterviewContinuous
import com.tdd.talktobook.core.designsystem.InterviewFinishBtn
import com.tdd.talktobook.core.designsystem.InterviewIng
import com.tdd.talktobook.core.designsystem.InterviewNextQuestion
import com.tdd.talktobook.core.designsystem.InterviewReAnswer
import com.tdd.talktobook.core.designsystem.InterviewStartBtn

enum class ConversationType(
    val type: String,
    val btnText: String,
    val plusFirstBtn: String? = null,
    val plusSecondBtn: String? = null,
) {
    BEFORE(BeforeStart, InterviewStartBtn),
    ING(InterviewIng, InterviewFinishBtn),
    FINISH(FinishRequest, InterviewNextQuestion, InterviewReAnswer, InterviewContinuous)
    ;

    companion object {
        fun getConversationBtnText(type: ConversationType): String =
            ConversationType.entries.firstOrNull { it == type }?.btnText ?: ""
    }
}
