package com.tdd.talktobook.domain.entity.request.page

data class TwoBtnDialogModel (
    val title: String = "",
    val semiTitle: String = "",
    val firstBtnText: String = "",
    val onClickBtnFirst: () -> Unit = {},
    val secondBtnText: String = "",
    val onClickBtnSecond: () -> Unit = {},
    val bottomBtnText: String = "",
    val onClickBottomText: () -> Unit = {}
)