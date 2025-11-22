package com.tdd.talktobook.feature.onboarding

import androidx.lifecycle.viewModelScope
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.domain.entity.request.member.MemberInfoModel
import com.tdd.talktobook.domain.usecase.member.PutEditMemberInfoUseCase
import com.tdd.talktobook.feature.onboarding.type.AgeGroupType
import com.tdd.talktobook.feature.onboarding.type.GenderType
import com.tdd.talktobook.feature.onboarding.type.OnboardingPageType
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class OnboardingViewModel(
    private val putEditMemberInfoUseCase: PutEditMemberInfoUseCase
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

    fun putEditMemberInfo() {
        viewModelScope.launch {
            putEditMemberInfoUseCase(
                MemberInfoModel(gender = uiState.value.gender.type, occupation = uiState.value.occupationInput, ageGroup = uiState.value.ageGroup.type)
            ).collect { resultResponse(it, {}) }

            emitEventFlow(OnboardingEvent.GoToHomePage)
        }
    }
}