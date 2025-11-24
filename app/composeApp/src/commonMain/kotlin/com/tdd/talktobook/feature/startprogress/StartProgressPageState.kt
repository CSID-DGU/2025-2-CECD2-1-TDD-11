package com.tdd.talktobook.feature.startprogress

import com.tdd.talktobook.core.ui.base.PageState
import com.tdd.talktobook.domain.entity.enums.MaterialType
import com.tdd.talktobook.feature.startprogress.type.StartProgressPageType

data class StartProgressPageState(
    val pageType: StartProgressPageType = StartProgressPageType.FIRST_PAGE,
    val isBtnActivated: Boolean = false,
    val material: MaterialType = MaterialType.DEFAULT,
    val reasonInput: String = "",
    val interviewId: Int = 0,
    val autobiographyId: Int = 0,
    val firstQuestion: String = "",
) : PageState
