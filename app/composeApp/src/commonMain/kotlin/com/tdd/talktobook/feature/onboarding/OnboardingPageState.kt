package com.tdd.talktobook.feature.onboarding

import com.tdd.talktobook.core.ui.base.PageState
import com.tdd.talktobook.feature.onboarding.type.AgeGroupType
import com.tdd.talktobook.feature.onboarding.type.GenderType
import com.tdd.talktobook.feature.onboarding.type.OnboardingPageType

data class OnboardingPageState (
    val pageType: OnboardingPageType = OnboardingPageType.FIRST_PAGE,
    val isBtnActivated: Boolean = false,
    val gender: GenderType = GenderType.DEFAULT,
    val occupationInput: String = "",
    val ageGroup: AgeGroupType = AgeGroupType.DEFAULT,
    val
): PageState