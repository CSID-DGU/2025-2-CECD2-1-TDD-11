package com.tdd.bookshelf.domain.entity.enums

import com.tdd.bookshelf.core.designsystem.Bottom
import com.tdd.bookshelf.core.designsystem.Left
import com.tdd.bookshelf.core.designsystem.Middle
import com.tdd.bookshelf.core.designsystem.Top

enum class BookTitlePositionType(
    val value: String,
) {
    TOP(Top),
    MIDDLE(Middle),
    BOTTOM(Bottom),
    LEFT(Left),
}
