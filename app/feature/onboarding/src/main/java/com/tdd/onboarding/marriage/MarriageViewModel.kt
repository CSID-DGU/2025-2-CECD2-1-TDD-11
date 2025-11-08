package com.tdd.onboarding.marriage

import com.tdd.domain.entity.request.CreateUserModel
import com.tdd.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MarriageViewModel @Inject constructor(

) : BaseViewModel<MarriagePageState>(
    MarriagePageState()
) {

    fun setUserModel(userModel: CreateUserModel) {
        updateState(
            uiState.value.copy(
                userModel = userModel
            )
        )
    }

    fun updateUserModel() = uiState.value.userModel.copy(marry = uiState.value.marriage)

    fun setSelectedMarriage(marriage: String) {
        updateState(
            uiState.value.copy(
                marriage = marriage
            )
        )
    }
}