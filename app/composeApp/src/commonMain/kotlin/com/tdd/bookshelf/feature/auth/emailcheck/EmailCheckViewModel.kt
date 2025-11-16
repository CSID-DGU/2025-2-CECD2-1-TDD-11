package com.tdd.bookshelf.feature.auth.emailcheck

import com.tdd.bookshelf.core.ui.base.BaseViewModel
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class EmailCheckViewModel(): BaseViewModel<EmailCheckPageState>(
    EmailCheckPageState()
) {
    fun setEmail(email: String) {
        updateState(
            uiState.value.copy(
                email = email
            )
        )
    }

    fun onCodeValueChange(newValue: String) {
        updateState(
            uiState.value.copy(
                codeInput = newValue,
            ),
        )
    }

    fun postCheckEmail() {
        emitEventFlow(EmailCheckEvent.GoToLogInPage)
    }
}