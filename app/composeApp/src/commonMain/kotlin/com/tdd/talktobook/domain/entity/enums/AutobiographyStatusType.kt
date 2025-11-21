package com.tdd.talktobook.domain.entity.enums

import com.tdd.talktobook.core.designsystem.CreatingStatus
import com.tdd.talktobook.core.designsystem.EmptyStatus
import com.tdd.talktobook.core.designsystem.EnoughStatus
import com.tdd.talktobook.core.designsystem.FinishStatus
import com.tdd.talktobook.core.designsystem.ProgressingStatus

enum class AutobiographyStatusType(
    val type: String,
) {
    EMPTY(EmptyStatus),
    PROGRESS(ProgressingStatus),
    ENOUGH(EnoughStatus),
    CREATING(CreatingStatus),
    FINISH(FinishStatus);

    companion object {
        fun getType(type: String): AutobiographyStatusType =
            entries.firstOrNull { it.type == type } ?: EMPTY
    }
}