package com.tdd.talktobook.feature.signup

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.domain.entity.request.auth.EmailSignUpRequestModel
import com.tdd.talktobook.domain.entity.response.auth.AccessTokenModel
import com.tdd.talktobook.domain.usecase.auth.PostEmailSignUpUseCase
import com.tdd.talktobook.domain.usecase.auth.SaveTokenUseCase
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SignUpViewModel(
    private val saveTokenUseCase: SaveTokenUseCase,
    private val postEmailSignUpUseCase: PostEmailSignUpUseCase,
) : BaseViewModel<SignUpPageState>(
        SignUpPageState(),
    ) {
    fun onEmailValueChange(newValue: String) {
        updateState(
            uiState.value.copy(
                emailInput = newValue,
            ),
        )
    }

    fun onPasswordValueChange(newValue: String) {
        updateState(
            uiState.value.copy(
                passwordInput = newValue,
            ),
        )
    }

    fun postEmailSignUp() {
        viewModelScope.launch {
            postEmailSignUpUseCase(
                EmailSignUpRequestModel(
                    email = uiState.value.emailInput,
                    password = uiState.value.passwordInput,
                ),
            ).collect {
                resultResponse(it, ::onSuccessPostEmailSignUp)
            }

            emitEventFlow(SignUpEvent.GoToLogInPage)
        }
    }

    private fun onSuccessPostEmailSignUp(data: AccessTokenModel) {
        d("[ktor] sign up response -> $data")
        if (data.accessToken.isNotEmpty()) {
            saveAccessToken(data.accessToken)
        }
    }

    private fun saveAccessToken(data: String) {
        viewModelScope.launch {
            saveTokenUseCase(data).collect { }
        }
    }
}
