package com.tdd.bookshelf.feature.auth.signup

import com.tdd.bookshelf.core.ui.base.Event

sealed class SignUpEvent : Event {
    data object GoToEmailCheckPage : SignUpEvent()
}
