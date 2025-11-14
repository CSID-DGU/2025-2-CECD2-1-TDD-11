package com.tdd.bookshelf.feature.interview.type

import bookshelf.composeapp.generated.resources.Res
import bookshelf.composeapp.generated.resources.img_interview_before
import bookshelf.composeapp.generated.resources.img_interview_ing
import com.tdd.bookshelf.core.designsystem.BeforeStart
import com.tdd.bookshelf.core.designsystem.InterviewIng
import org.jetbrains.compose.resources.DrawableResource

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
