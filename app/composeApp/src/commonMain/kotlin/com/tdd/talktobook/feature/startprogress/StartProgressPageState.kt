package com.tdd.talktobook.feature.startprogress

import com.tdd.talktobook.core.ui.base.PageState
import com.tdd.talktobook.core.ui.common.type.FlowType
import com.tdd.talktobook.domain.entity.enums.MaterialType
import com.tdd.talktobook.feature.startprogress.type.StartProgressPageType

data class StartProgressPageState(
    val flowType: FlowType = FlowType.DEFAULT,
    val pageType: StartProgressPageType = StartProgressPageType.BEGIN_PAGE,
    val isBtnActivated: Boolean = false,
    val nickNameInput: String = "",
    val material: MaterialType = MaterialType.DEFAULT,
    val reasonInput: String = "",
    val interviewId: Int = 0,
    val autobiographyId: Int = 0,
    val firstQuestion: String = "",
) : PageState
