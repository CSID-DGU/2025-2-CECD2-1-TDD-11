package com.tdd.onboarding.education

import com.tdd.domain.entity.request.CreateUserModel
import com.tdd.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScholarShipViewModel @Inject constructor(

): BaseViewModel<ScholarShipPageState>(
    ScholarShipPageState()
) {

    fun setUserModel(userModel: CreateUserModel) {
        updateState(
            uiState.value.copy(
                userModel = userModel
            )
        )
    }

    fun updateUserModel() = uiState.value.userModel.copy(education = uiState.value.scholarShip)

    fun setSelectedScholarShip(scholarShip: String) {
        updateState(
            uiState.value.copy(
                scholarShip = scholarShip
            )
        )
    }
}