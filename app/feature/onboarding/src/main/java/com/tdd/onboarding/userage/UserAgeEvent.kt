package com.tdd.onboarding.userage

import com.tdd.ui.base.Event

sealed class UserAgeEvent: Event {
    data object GoToMainPage: UserAgeEvent()
}