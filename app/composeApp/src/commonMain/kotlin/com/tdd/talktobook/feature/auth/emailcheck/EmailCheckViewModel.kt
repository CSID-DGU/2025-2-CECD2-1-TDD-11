package com.tdd.talktobook.feature.auth.emailcheck

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.designsystem.ExpiredMinute
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.core.ui.util.setTimeSecondType
import com.tdd.talktobook.data.entity.response.api.ApiException
import com.tdd.talktobook.domain.entity.request.auth.EmailVerifyRequestModel
import com.tdd.talktobook.domain.usecase.auth.PostEmailVerifyUseCase
import com.tdd.talktobook.domain.usecase.auth.ResendCodeUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class EmailCheckViewModel(
    private val postEmailVerifyUseCase: PostEmailVerifyUseCase,
    private val resendCodeUseCase: ResendCodeUseCase,
) : BaseViewModel<EmailCheckPageState>(
        EmailCheckPageState(),
    ) {
    private var timerJob: Job? = null

    fun setEmail(email: String) {
        updateState { state ->
            state.copy(
                email = email,
            )
        }
    }

    fun onCodeValueChange(newValue: String) {
        updateState { state ->
            state.copy(
                codeInput = newValue,
            )
        }
    }

    fun postCheckEmail() {
        viewModelScope.launch {
            postEmailVerifyUseCase(
                EmailVerifyRequestModel(
                    email = uiState.value.email,
                    code = uiState.value.codeInput,
                ),
            ).collect {
                resultResponse(it, { data ->
                    d("[ktor] email verify success")
                    emitEventFlow(EmailCheckEvent.GoToLogInPage)
                }, { error ->
                    when (error) {
                        is ApiException -> {
                            d("[ktor] email verify exception -> code=${error.status}, msg=${error.msg}")
                            setEmailCheckExceptionMessage(error.msg)
                            emitEventFlow(EmailCheckEvent.ShowServerExceptionToast)
                        }

                        else -> {
                            d("[ktor] unknown error -> ${error.message}")
                            emitEventFlow(EmailCheckEvent.ShowServerErrorToast)
                        }
                    }
                })
            }
        }
    }

    fun startCodeExpiredTimer(totalSeconds: Int = 5 * 60) {
        timerJob?.cancel()

        timerJob =
            viewModelScope.launch {
                var remain = totalSeconds
                updateState { state ->
                    state.copy(
                        codeExpiredTime = setTimeSecondType(remain),
                        isCodeExpired = false,
                    )
                }

                while (remain > 0) {
                    delay(1000L)
                    remain -= 1
                    updateState { state ->
                        state.copy(
                            codeExpiredTime = setTimeSecondType(remain),
                        )
                    }
                }

                updateState { state ->
                    state.copy(
                        codeExpiredTime = ExpiredMinute,
                        isCodeExpired = true,
                    )
                }
            }
    }

    fun resendCode() {
        viewModelScope.launch {
            resendCodeUseCase(uiState.value.email).collect {
                resultResponse(it, {
                    d("[ktor] resend code success")
                    startCodeExpiredTimer(5 * 60)
                }, { error ->
                    when (error) {
                        is ApiException -> {
                            d("[ktor] resend code exception -> code=${error.status}, msg=${error.msg}")
                            setEmailCheckExceptionMessage(error.msg)
                            emitEventFlow(EmailCheckEvent.ShowServerExceptionToast)
                        }

                        else -> {
                            d("[ktor] unknown error -> ${error.message}")
                            emitEventFlow(EmailCheckEvent.ShowServerErrorToast)
                        }
                    }
                })
            }
        }
    }

    private fun setEmailCheckExceptionMessage(message: String) {
        updateState { state ->
            state.copy(
                serverExceptionMessage = message,
            )
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}
