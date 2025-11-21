package com.tdd.talktobook.domain.entity.response.autobiography

import com.tdd.talktobook.domain.entity.enums.AutobiographyStatusType

data class CurrentInterviewProgressModel(
    val progressPercentage: Float = 0f,
    val status: AutobiographyStatusType = AutobiographyStatusType.EMPTY,
)