package com.tdd.bookshelf.domain.entity.enums

import com.tdd.bookshelf.core.designsystem.InPublishing
import com.tdd.bookshelf.core.designsystem.NotPublished
import com.tdd.bookshelf.core.designsystem.Published
import com.tdd.bookshelf.core.designsystem.Rejected
import com.tdd.bookshelf.core.designsystem.RequestConfirmed
import com.tdd.bookshelf.core.designsystem.Requested

enum class PublishStatusType(
    val type: String,
) {
    REQUEST(Requested),
    REQUEST_CONFIRM(RequestConfirmed),
    IN_PUBLISHING(InPublishing),
    PUBLISHED(Published),
    REJECTED(Rejected),
    NOT_PUBLISHED(NotPublished),
    ;

    companion object {
        fun getPublishStatus(type: String): PublishStatusType =
            entries.firstOrNull { it.type == type } ?: REJECTED
    }
}
