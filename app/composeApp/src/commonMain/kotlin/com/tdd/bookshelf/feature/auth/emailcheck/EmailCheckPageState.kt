package com.tdd.bookshelf.feature.auth.emailcheck

import com.tdd.bookshelf.core.ui.base.PageState

data class EmailCheckPageState(
    val email: String = "",
    val codeInput: String = "",
) : PageState
