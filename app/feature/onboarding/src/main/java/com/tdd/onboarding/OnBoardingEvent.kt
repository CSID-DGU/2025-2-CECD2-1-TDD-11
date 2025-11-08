package com.tdd.onboarding

import com.tdd.ui.base.Event

sealed class OnBoardingEvent: Event {
    data object GoToInterViewPage: OnBoardingEvent()
}