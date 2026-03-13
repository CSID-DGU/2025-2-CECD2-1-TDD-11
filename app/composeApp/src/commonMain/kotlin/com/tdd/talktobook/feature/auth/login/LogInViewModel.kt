package com.tdd.talktobook.feature.auth.login

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.data.entity.response.api.ApiException
import com.tdd.talktobook.domain.entity.request.auth.EmailLogInRequestModel
import com.tdd.talktobook.domain.entity.response.auth.TokenModel
import com.tdd.talktobook.domain.usecase.auth.DeleteLocalAllDataUseCase
import com.tdd.talktobook.domain.usecase.auth.GetRefreshTokenUseCase
import com.tdd.talktobook.domain.usecase.auth.PostEmailLogInUseCase
import com.tdd.talktobook.domain.usecase.auth.ReissueTokenUseCase
import com.tdd.talktobook.domain.usecase.auth.SaveTokenUseCase
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class LogInViewModel(
    private val postEmailLogInUseCase: PostEmailLogInUseCase,
    private val saveTokenUseCase: SaveTokenUseCase,
    private val deleteLocalAllDataUseCase: DeleteLocalAllDataUseCase,
    private val getRefreshTokenUseCase: GetRefreshTokenUseCase,
    private val reissueTokenUseCase: ReissueTokenUseCase,
) : BaseViewModel<LogInPageState>(
        LogInPageState(),
    ) {
    init {
        initGetRefreshToken()
    }

    private fun initGetRefreshToken() {
        viewModelScope.launch {
            getRefreshTokenUseCase(Unit).collect {
                resultResponse(it, ::initReissueToken)
            }
        }
    }

    private fun initReissueToken(token: String) {
        viewModelScope.launch {
            reissueTokenUseCase(token).collect { resultResponse(it, ::onSuccessPostEmailLogIn) }
        }
    }

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

    fun changePasswordVisible() {
        updateState { state ->
            state.copy(
                isPasswordVisible = !uiState.value.isPasswordVisible,
            )
        }
    }

    fun postEmailLogIn() {
        viewModelScope.launch {
            postEmailLogInUseCase(
                EmailLogInRequestModel(
                    email = uiState.value.emailInput,
                    password = uiState.value.passwordInput,
                ),
            ).collect {
                resultResponse(it, ::onSuccessPostEmailLogIn, { error ->
                    when (error) {
                        is ApiException -> {
                            d("[ktor] login error -> code=${error.status}, msg=${error.msg}")
                            setExceptionCase(error.status)
                        }
                        else -> {
                            d("[ktor] unknown error -> ${error.message}")
                            emitEventFlow(LogInEvent.ShowServerErrorToast)
                        }
                    }
                })
            }
        }
    }

    private fun setExceptionCase(code: Int) {
        when (code) {
            400 -> {
                emitEventFlow(LogInEvent.ShowCheckEmailValidToast)
            }
            401 -> {
                emitEventFlow(LogInEvent.ShowWrongPWToast)
            }
            404 -> {
                emitEventFlow(LogInEvent.ShowNoExistToast)
            }
            409, 410 -> {
                emitEventFlow(LogInEvent.ShowDeleteUserToast)
            }
        }
    }

    private fun onSuccessPostEmailLogIn(data: TokenModel) {
        d("[ktor] email response -> $data")
        if (data.accessToken.isNotEmpty()) {
            saveAccessToken(data)
        }
    }

    private fun saveAccessToken(data: TokenModel) {
        viewModelScope.launch {
            saveTokenUseCase(data).collect { }
        }

        setNextPage(data.metadataSuccess)
    }

    private fun setNextPage(data: Boolean) {
        when (data) {
            true -> emitEventFlow(LogInEvent.GoToHomePage)
            false -> emitEventFlow(LogInEvent.GoToOnboardingPage)
        }
    }

    fun clearLocalData() {
        viewModelScope.launch {
            deleteLocalAllDataUseCase(Unit).collect {
                resultResponse(it, {})
            }
        }

        emitEventFlow(LogInEvent.GoToStartProgressPage)
    }
}
