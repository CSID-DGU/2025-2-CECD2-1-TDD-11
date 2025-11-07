package com.tdd.onboarding.userage

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.lifecycle.viewModelScope
import com.tdd.domain.entity.request.auth.AuthRequestModel
import com.tdd.domain.entity.response.auth.AuthResponseModel
import com.tdd.domain.usecase.auth.PostAuthUseCase
import com.tdd.domain.usecase.auth.SaveTokenUseCase
import com.tdd.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserAgeViewModel @Inject constructor(
    private val postAuthUseCase: PostAuthUseCase,
    private val saveTokenUseCase: SaveTokenUseCase,
) : BaseViewModel<UserAgePageState>(
    UserAgePageState()
) {

    @SuppressLint("HardwareIds")
    fun postLogIn(context: Context) {
        val deviceId =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        viewModelScope.launch {
            postAuthUseCase(AuthRequestModel(deviceId)).collect {
                resultResponse(
                    it,
                    ::onSuccessAuth
                )
            }
        }
    }

    private fun onSuccessAuth(data: AuthResponseModel) {
        Timber.d("[테스트] -> $data")
        viewModelScope.launch {
            saveTokenUseCase(data.userId).collect { resultResponse(it, {}) }
            checkProfileCompleted(data.profileCompleted)
        }
    }

    private fun checkProfileCompleted(data: Boolean) {
        when (data) {
            true -> emitEventFlow(UserAgeEvent.GoToMainPage)
            false -> {}
        }
    }

    fun updateUserModel() = uiState.value.userModel.copy(age = uiState.value.selectedUserAge)

    fun setSelectedAge(age: String) {
        updateState(
            uiState.value.copy(
                selectedUserAge = age
            )
        )
    }
}