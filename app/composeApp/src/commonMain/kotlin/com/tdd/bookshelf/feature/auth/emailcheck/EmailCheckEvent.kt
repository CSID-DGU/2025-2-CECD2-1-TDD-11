package com.tdd.bookshelf.feature.auth.emailcheck

import com.tdd.bookshelf.core.ui.base.Event

sealed class EmailCheckEvent : Event {
    data object GoToLogInPage : EmailCheckEvent()
}
