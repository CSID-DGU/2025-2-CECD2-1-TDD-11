package com.tdd.domain.entity.response.progress

data class ProgressStepItem (
    val step: Int = 0,
    val title: String = "",
    val content: String = "",
    val isFinish: Boolean = false,
    val isProgress: Boolean = false
)