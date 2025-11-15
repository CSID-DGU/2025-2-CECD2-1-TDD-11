package com.tdd.bookshelf.feature.auth.signup

import com.tdd.bookshelf.core.ui.base.PageState

data class SignUpPageState(
    val emailInput: String = "",
    val passwordInput: String = "",
) : PageState
