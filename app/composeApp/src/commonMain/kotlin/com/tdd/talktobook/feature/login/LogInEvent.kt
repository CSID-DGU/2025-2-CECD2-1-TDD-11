package com.tdd.talktobook.feature.login

import com.tdd.talktobook.core.ui.base.Event

sealed class LogInEvent : Event {
    data object GoToOnBoardingPage : LogInEvent()
}
