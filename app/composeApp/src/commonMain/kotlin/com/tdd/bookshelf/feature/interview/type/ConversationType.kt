package com.tdd.bookshelf.feature.interview.type

import com.tdd.bookshelf.core.designsystem.BeforeStart
import com.tdd.bookshelf.core.designsystem.InterviewFinishBtn
import com.tdd.bookshelf.core.designsystem.InterviewIng
import com.tdd.bookshelf.core.designsystem.InterviewStartBtn

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
