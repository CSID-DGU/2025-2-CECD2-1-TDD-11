package com.tdd.bookshelf.feature.signup

import com.tdd.bookshelf.core.ui.base.Event

sealed class SignUpEvent : Event {
    data object GoToLogInPage : SignUpEvent()
}
