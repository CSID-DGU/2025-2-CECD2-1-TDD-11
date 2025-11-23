package com.tdd.talktobook.feature.interview.type

import talktobook.composeapp.generated.resources.Res
import com.tdd.talktobook.core.designsystem.BeforeStart
import com.tdd.talktobook.core.designsystem.InterviewIng
import org.jetbrains.compose.resources.DrawableResource
import talktobook.composeapp.generated.resources.img_interview_before
import talktobook.composeapp.generated.resources.img_interview_ing

enum class ConversationType(
    val type: String,
    val btnImg: DrawableResource,
) {
    BEFORE(BeforeStart, Res.drawable.img_interview_before),
    ING(InterviewIng, Res.drawable.img_interview_ing),
    ;

    companion object {
        fun getConversationBtnImg(type: ConversationType): DrawableResource =
            ConversationType.entries.firstOrNull { it == type }?.btnImg ?: Res.drawable.img_interview_before
    }
}
