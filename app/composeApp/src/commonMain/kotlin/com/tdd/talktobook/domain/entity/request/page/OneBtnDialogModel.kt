package com.tdd.talktobook.domain.entity.request.page

data class OneBtnDialogModel(
    val title: String = "",
    val semiTitle: String = "",
    val btnText: String = "",
    val onClickBtn: () -> Unit = {},
    val isBottomTextVisible: Boolean = false,
    val bottomText: String = "",
    val onClickBottomText: () -> Unit = {},
)