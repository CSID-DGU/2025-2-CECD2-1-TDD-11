package com.tdd.talktobook.feature.signup

import com.tdd.talktobook.core.ui.base.PageState

data class SignUpPageState(
    val emailInput: String = "",
    val passwordInput: String = "",
) : PageState
