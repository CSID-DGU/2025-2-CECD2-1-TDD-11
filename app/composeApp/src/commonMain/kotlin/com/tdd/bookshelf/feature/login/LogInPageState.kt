package com.tdd.bookshelf.feature.login

import com.tdd.bookshelf.core.ui.base.PageState

data class LogInPageState(
    val emailInput: String = "",
    val passwordInput: String = "",
) : PageState
