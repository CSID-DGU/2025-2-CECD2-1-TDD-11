package com.tdd.talktobook.feature

import com.tdd.talktobook.core.ui.base.PageState

data class MainPageState(
    val bottomNavType: BottomNavType = BottomNavType.DEFAULT,
) : PageState
