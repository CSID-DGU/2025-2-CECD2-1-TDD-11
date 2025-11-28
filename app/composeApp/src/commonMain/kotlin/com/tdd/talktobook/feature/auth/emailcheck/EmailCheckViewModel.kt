package com.tdd.talktobook.feature.auth.emailcheck

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.domain.entity.request.auth.EmailVerifyRequestModel
import com.tdd.talktobook.domain.usecase.auth.PostEmailVerifyUseCase
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class EmailCheckViewModel(
    private val postEmailVerifyUseCase: PostEmailVerifyUseCase,
) : BaseViewModel<EmailCheckPageState>(
        EmailCheckPageState(),
    ) {
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
}
