package com.tdd.bookshelf.feature.login

import com.tdd.bookshelf.core.ui.base.Event

sealed class LogInEvent : Event {
    data object GoToOnBoardingPage : LogInEvent()
}
