package com.tdd.bookshelf.feature.login

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.bookshelf.core.ui.base.BaseViewModel
import com.tdd.bookshelf.domain.entity.request.auth.EmailLogInRequestModel
import com.tdd.bookshelf.domain.entity.request.member.EditMemberInfoModel
import com.tdd.bookshelf.domain.entity.response.auth.AccessTokenModel
import com.tdd.bookshelf.domain.usecase.auth.PostEmailLogInUseCase
import com.tdd.bookshelf.domain.usecase.auth.SaveTokenUseCase
import com.tdd.bookshelf.domain.usecase.member.PutEditMemberInfoUseCase
import com.tdd.bookshelf.feature.signup.SignUpEvent
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class LogInViewModel(
    private val postEmailLogInUseCase: PostEmailLogInUseCase,
    private val saveTokenUseCase: SaveTokenUseCase,
    private val putEditMemberInfoUseCase: PutEditMemberInfoUseCase
) : BaseViewModel<LogInPageState>(
        LogInPageState(),
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

    fun postEmailLogIn() {
        viewModelScope.launch {
            postEmailLogInUseCase(
                EmailLogInRequestModel(
                    email = uiState.value.emailInput,
                    password = uiState.value.passwordInput,
                ),
            ).collect {
                resultResponse(it, ::onSuccessPostEmailLogIn)
            }
        }
    }

    private fun onSuccessPostEmailLogIn(data: AccessTokenModel) {
        d("[ktor] email response -> $data")
        if (data.accessToken.isNotEmpty()) {
            saveAccessToken(data.accessToken)
        }
    }

    private fun saveAccessToken(data: String) {
        viewModelScope.launch {
            saveTokenUseCase(data).collect { }

            putEditMemberInfo()
        }
    }

    private fun putEditMemberInfo() {
        viewModelScope.launch {
            putEditMemberInfoUseCase(
                EditMemberInfoModel("홍길동", "2000-01-01", "MALE", false, "Software Engineer", "대학교 재학", "미혼")
            ).collect { resultResponse(it, {}) }

            emitEventFlow(LogInEvent.GoToOnBoardingPage)
        }
    }
}
