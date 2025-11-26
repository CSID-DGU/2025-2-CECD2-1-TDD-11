package com.tdd.talktobook.domain.entity.response.interview

import kotlinx.serialization.SerialName

data class CoShowAnswerModel (
    val id: Int = 0,
    val order: Int = 0,
    val question: String = "",
    val isLast: Boolean = false
)