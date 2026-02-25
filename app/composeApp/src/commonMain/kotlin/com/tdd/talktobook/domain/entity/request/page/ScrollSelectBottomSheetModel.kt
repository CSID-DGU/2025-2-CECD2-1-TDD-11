package com.tdd.talktobook.domain.entity.request.page

data class ScrollSelectBottomSheetModel(
    val firstStateVisibleIndex: Int = 0,
    val secondStateVisibleIndex: Int = 0,
    val thirdStateVisibleIndex: Int = 0,
    val firstList: List<String> = emptyList(),
    val secondList: List<String> = emptyList(),
    val thirdList: List<String> = emptyList(),
)