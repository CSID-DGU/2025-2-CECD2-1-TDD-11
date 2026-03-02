package com.tdd.talktobook.feature.auth.emailcheck

import com.tdd.talktobook.core.ui.base.PageState

data class EmailCheckPageState(
    val email: String = "",
    val codeInput: String = "",
) : PageState
