package com.tdd.talktobook.feature.auth.login

import com.tdd.talktobook.core.ui.base.Event

sealed class LogInEvent : Event {
    data object GoToHomePage : LogInEvent()

    data object GoToOnboardingPage : LogInEvent()

    data object GoToStartProgressPage : LogInEvent()

    data object ShowCheckEmailValidToast : LogInEvent()

    data object ShowWrongPWToast : LogInEvent()

    data object ShowNoExistToast : LogInEvent()

    data object ShowDeleteUserToast : LogInEvent()

    data object ShowServerErrorToast : LogInEvent()
}
