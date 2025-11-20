package com.tdd.talktobook.feature.signup

import com.tdd.talktobook.core.ui.base.Event

sealed class SignUpEvent : Event {
    data object GoToLogInPage : SignUpEvent()
}
