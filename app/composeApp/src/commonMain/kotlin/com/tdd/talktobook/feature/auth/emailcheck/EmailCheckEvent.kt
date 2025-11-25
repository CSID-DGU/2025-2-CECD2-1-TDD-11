package com.tdd.talktobook.feature.auth.emailcheck

import com.tdd.talktobook.core.ui.base.Event

sealed class EmailCheckEvent : Event {
    data object GoToLogInPage : EmailCheckEvent()
}
