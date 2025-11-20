package com.tdd.talktobook.feature.onboarding

import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.feature.onboarding.type.AgeGroupType
import com.tdd.talktobook.feature.onboarding.type.GenderType
import com.tdd.talktobook.feature.onboarding.type.OnboardingPageType
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class OnboardingViewModel(

) : BaseViewModel<OnboardingPageState>(
    OnboardingPageState()
) {
    fun setPageType(type: OnboardingPageType) {
        updateState(
            uiState.value.copy(
                pageType = type,
                isBtnActivated = false
            )
        )
    }

    fun setSelectedGender(gender: GenderType) {
        updateState(
            uiState.value.copy(
                gender = gender,
                isBtnActivated = true
            )
        )
    }

    fun setSelectedAgeGroup(age: AgeGroupType) {
        updateState(
            uiState.value.copy(
                ageGroup = age,
                isBtnActivated = true
            )
        )
    }

    fun onOccupationValueChange(newValue: String) {
        updateState(
            uiState.value.copy(
                occupationInput = newValue,
                isBtnActivated = newValue.isNotEmpty()
            )
        )
    }
}