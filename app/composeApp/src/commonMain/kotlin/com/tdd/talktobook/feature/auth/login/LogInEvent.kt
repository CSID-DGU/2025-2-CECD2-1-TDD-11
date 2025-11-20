package com.tdd.talktobook.feature.auth.login

import com.tdd.talktobook.core.ui.base.Event

sealed class LogInEvent : Event {
    data object GoToOnBoardingPage : LogInEvent()

    data object GoToHomePage : LogInEvent()
}
