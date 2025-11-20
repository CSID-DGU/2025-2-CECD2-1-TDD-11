package com.tdd.bookshelf.feature.auth.login

import com.tdd.bookshelf.core.ui.base.Event

sealed class LogInEvent : Event {
    data object GoToOnBoardingPage : LogInEvent()

    data object GoToHomePage : LogInEvent()
}
