package com.tdd.talktobook.domain.entity.response.autobiography

data class SelectedThemeModel(
    val name: String = "",
    val categories: List<Int> = emptyList(),
)