package com.tdd.talktobook.feature.startprogress.type

import com.tdd.talktobook.core.designsystem.Next
import com.tdd.talktobook.core.designsystem.StartInterviewBtn
import com.tdd.talktobook.core.designsystem.StartProgressFirstPage
import com.tdd.talktobook.core.designsystem.StartProgressSecondPage

enum class StartProgressPageType(
    val page: Int,
    val title: String,
    val btnText: String,
) {
    FIRST_PAGE(1, StartProgressFirstPage, Next),
    SECOND_PAGE(2, StartProgressSecondPage, StartInterviewBtn),
}
