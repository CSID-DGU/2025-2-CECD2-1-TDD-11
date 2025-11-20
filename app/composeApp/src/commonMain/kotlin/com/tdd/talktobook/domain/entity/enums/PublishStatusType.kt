package com.tdd.talktobook.domain.entity.enums

import com.tdd.talktobook.core.designsystem.InPublishing
import com.tdd.talktobook.core.designsystem.NotPublished
import com.tdd.talktobook.core.designsystem.Published
import com.tdd.talktobook.core.designsystem.Rejected
import com.tdd.talktobook.core.designsystem.RequestConfirmed
import com.tdd.talktobook.core.designsystem.Requested

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
