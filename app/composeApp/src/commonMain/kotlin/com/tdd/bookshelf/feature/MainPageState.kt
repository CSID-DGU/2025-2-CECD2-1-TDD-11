package com.tdd.bookshelf.feature

import com.tdd.bookshelf.core.ui.base.PageState

data class MainPageState(
    val bottomNavType: BottomNavType = BottomNavType.DEFAULT,
) : PageState
