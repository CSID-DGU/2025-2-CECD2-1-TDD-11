package com.tdd.talktobook.feature.auth.emailcheck

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.designsystem.ExpiredMinute
import com.tdd.talktobook.core.designsystem.FiveMinute
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.core.ui.util.setTimeSecondType
import com.tdd.talktobook.domain.entity.request.auth.EmailVerifyRequestModel
import com.tdd.talktobook.domain.usecase.auth.PostEmailVerifyUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class EmailCheckViewModel(
    private val postEmailVerifyUseCase: PostEmailVerifyUseCase,
) : BaseViewModel<EmailCheckPageState>(
        EmailCheckPageState(),
    ) {

    private val _codeExpiredTime = MutableStateFlow(FiveMinute)
    val codeExpiredTime: StateFlow<String> = _codeExpiredTime.asStateFlow()

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
                    d("[ktor] email verify response -> $data")
                })
            }
        }

        emitEventFlow(EmailCheckEvent.GoToLogInPage)
    }

    fun startCodeExpiredTimer(totalSeconds: Int = 5 * 60) {
        timerJob?.cancel()

        timerJob =
            viewModelScope.launch {
                var remain = totalSeconds
                _codeExpiredTime.value = setTimeSecondType(remain)

                while (remain > 0) {
                    delay(1000L)
                    remain -= 1
                    _codeExpiredTime.value = setTimeSecondType(remain)
                }

                _codeExpiredTime.value = ExpiredMinute
                // TODO 시간 완료 시 이벤트
            }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}
