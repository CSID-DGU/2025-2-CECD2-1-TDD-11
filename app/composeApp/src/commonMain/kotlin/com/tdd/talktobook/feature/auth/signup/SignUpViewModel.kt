package com.tdd.talktobook.feature.auth.signup

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.domain.entity.request.auth.EmailSignUpRequestModel
import com.tdd.talktobook.domain.usecase.auth.PostEmailSignUpUseCase
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SignUpViewModel(
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
                resultResponse(it, { data ->
                    d("[ktor] sign up response -> $data")
                })
            }

            emitEventFlow(SignUpEvent.GoToEmailCheckPage)
        }
    }
}
