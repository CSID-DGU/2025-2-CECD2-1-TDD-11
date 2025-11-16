package com.tdd.bookshelf.feature.auth.login

import com.tdd.bookshelf.core.ui.base.PageState

data class LogInPageState(
    val emailInput: String = "",
    val passwordInput: String = "",
) : PageState
