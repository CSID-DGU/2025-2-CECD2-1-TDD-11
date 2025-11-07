package com.tdd.onboarding.gender

import com.tdd.domain.entity.request.CreateUserModel
import com.tdd.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserGenderViewModel @Inject constructor(

) : BaseViewModel<UserGenderPageState>(
    UserGenderPageState()
) {

    fun setUserModel(userModel: CreateUserModel) {
        updateState(
            uiState.value.copy(
                userModel = userModel
            )
        )
    }

    fun updateUserModel() = uiState.value.userModel.copy(gender = uiState.value.selectedGender)

    fun setSelectedGender(gender: String) {
        updateState(
            uiState.value.copy(
                selectedGender = gender
            )
        )
    }
}