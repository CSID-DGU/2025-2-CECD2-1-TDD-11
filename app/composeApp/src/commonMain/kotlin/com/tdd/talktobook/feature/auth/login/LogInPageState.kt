package com.tdd.talktobook.feature.auth.login

import com.tdd.talktobook.core.ui.base.PageState

data class LogInPageState(
    val emailInput: String = "",
    val passwordInput: String = "",
) : PageState
