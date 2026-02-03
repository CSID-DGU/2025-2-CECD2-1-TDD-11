package com.tdd.talktobook.feature.auth.signup

import com.tdd.talktobook.core.ui.base.PageState

data class SignUpPageState(
    val emailInput: String = "",
    val passwordInput: String = "",
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true,
) : PageState
