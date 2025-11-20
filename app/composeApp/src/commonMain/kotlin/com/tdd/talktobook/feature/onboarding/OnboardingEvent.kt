package com.tdd.talktobook.feature.onboarding

import com.tdd.talktobook.core.ui.base.Event

sealed class OnboardingEvent: Event {
    data object GoToHomePage: OnboardingEvent()
}