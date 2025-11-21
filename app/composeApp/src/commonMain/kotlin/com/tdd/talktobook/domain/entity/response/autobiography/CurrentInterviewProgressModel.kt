package com.tdd.talktobook.domain.entity.response.autobiography

import com.tdd.talktobook.domain.entity.enums.AutobiographyStatusType

data class CurrentInterviewProgressModel(
    val progressPercentage: Double = 0.0,
    val status: AutobiographyStatusType = AutobiographyStatusType.EMPTY,
)