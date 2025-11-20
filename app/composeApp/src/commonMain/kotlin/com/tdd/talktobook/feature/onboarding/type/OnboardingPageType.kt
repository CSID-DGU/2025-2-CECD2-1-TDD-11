package com.tdd.talktobook.feature.onboarding.type

import com.tdd.talktobook.core.designsystem.OnBoardingFirstPage
import com.tdd.talktobook.core.designsystem.OnBoardingSecondPage
import com.tdd.talktobook.core.designsystem.OnBoardingThirdPage

enum class OnboardingPageType(
    val page: Int,
    val title: String
) {
    FIRST_PAGE(1, OnBoardingFirstPage),
    SECOND_PAGE(2, OnBoardingSecondPage),
    THIRD_PAGE(3, OnBoardingThirdPage)
}