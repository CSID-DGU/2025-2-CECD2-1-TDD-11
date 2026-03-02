package com.tdd.talktobook.domain.entity.enums

import com.tdd.talktobook.core.designsystem.Bottom
import com.tdd.talktobook.core.designsystem.Left
import com.tdd.talktobook.core.designsystem.Middle
import com.tdd.talktobook.core.designsystem.Top

enum class BookTitlePositionType(
    val value: String,
) {
    TOP(Top),
    MIDDLE(Middle),
    BOTTOM(Bottom),
    LEFT(Left),
}
