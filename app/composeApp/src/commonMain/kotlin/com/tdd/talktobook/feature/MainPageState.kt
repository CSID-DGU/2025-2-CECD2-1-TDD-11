package com.tdd.talktobook.feature

import com.tdd.talktobook.core.ui.base.PageState
import com.tdd.talktobook.domain.entity.request.page.OneBtnDialogModel

data class MainPageState(
    val bottomNavType: BottomNavType = BottomNavType.DEFAULT,
    val oneBtnDialogModel: OneBtnDialogModel = OneBtnDialogModel(),
) : PageState
