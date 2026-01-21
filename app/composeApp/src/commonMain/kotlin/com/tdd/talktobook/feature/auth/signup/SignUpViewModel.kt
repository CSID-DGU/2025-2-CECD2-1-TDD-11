package com.tdd.talktobook.feature.auth.signup

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.data.entity.response.api.ApiException
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
        updateState { state ->
            state.copy(
                emailInput = newValue,
            )
        }
    }

    fun onPasswordValueChange(newValue: String) {
        updateState { state ->
            state.copy(
                passwordInput = newValue,
            )
        }
    }

    fun checkEmailPWValid() {
        val email = uiState.value.emailInput.trim()
        val password = uiState.value.passwordInput.trim()

        val isEmailValid = email.isNotEmpty() && EMAIL_REGEX.matches(email)
        val isPasswordValid = password.isNotEmpty() && PASSWORD_REGEX.matches(password)

        d("[test] email: $email, pw: $password")

        if (isEmailValid && isPasswordValid) { postEmailSignUp() }
        else {
            updateState { state ->
                state.copy(
                    isEmailValid = isEmailValid,
                    isPasswordValid = isPasswordValid
                )
            }
        }
    }

    private fun postEmailSignUp() {
        viewModelScope.launch {
            postEmailSignUpUseCase(
                EmailSignUpRequestModel(
                    email = uiState.value.emailInput,
                    password = uiState.value.passwordInput,
                ),
            ).collect {
                resultResponse(it, { data ->
                    d("[ktor] sign up response -> $data")
                    emitEventFlow(SignUpEvent.GoToEmailCheckPage)
                }, { error ->
                    when (error) {
                        is ApiException -> {
                            d("[ktor] sign up error -> code=${error.status}, msg=${error.msg}")
                            emitEventFlow(SignUpEvent.ShowMemberExistToast)
                        }
                        else -> {
                            d("[ktor] unknown error -> ${error.message}")
                            emitEventFlow(SignUpEvent.ShowServerErrorToast)
                        }
                    }
                })
            }
        }
    }

    companion object {
        private const val EMAIL_PATTERN = "^[A-Za-z0-9._%+-]{1,64}@[A-Za-z0-9.-]{2,253}\\.[A-Za-z]{2,}$"
        private const val PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#\\\\\$%^&])(?!.*(.)\\\\1\\\\1)[A-Za-z0-9!@#\\\\\$%^&]{8,64}\$"
        private val EMAIL_REGEX = Regex(pattern = EMAIL_PATTERN)
        private val PASSWORD_REGEX = Regex(pattern = PASSWORD_PATTERN)
    }
}
