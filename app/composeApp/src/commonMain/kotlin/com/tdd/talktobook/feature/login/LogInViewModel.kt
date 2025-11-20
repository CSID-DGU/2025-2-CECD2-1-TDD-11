package com.tdd.talktobook.feature.login

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.domain.entity.request.auth.EmailLogInRequestModel
import com.tdd.talktobook.domain.entity.request.autobiography.CreateAutobiographyChaptersRequestModel
import com.tdd.talktobook.domain.entity.request.autobiography.CreateChapterItemModel
import com.tdd.talktobook.domain.entity.request.autobiography.CreateSubChapterItemModel
import com.tdd.talktobook.domain.entity.request.member.EditMemberInfoModel
import com.tdd.talktobook.domain.entity.response.auth.AccessTokenModel
import com.tdd.talktobook.domain.usecase.auth.PostEmailLogInUseCase
import com.tdd.talktobook.domain.usecase.auth.SaveTokenUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.PostCreateAutobiographyChaptersUseCase
import com.tdd.talktobook.domain.usecase.member.PutEditMemberInfoUseCase
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class LogInViewModel(
    private val postEmailLogInUseCase: PostEmailLogInUseCase,
    private val saveTokenUseCase: SaveTokenUseCase,
    private val putEditMemberInfoUseCase: PutEditMemberInfoUseCase,
    private val postCreateAutobiographyChaptersUseCase: PostCreateAutobiographyChaptersUseCase,
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

    // Apple용 (제거)
    fun createMemberNoLogIn() {
        viewModelScope.launch {
            postEmailLogInUseCase(
                EmailLogInRequestModel(
                    email = "test105@gmail.com",
                    password = "pw",
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
                EditMemberInfoModel("홍길동", "2000-01-01", "MALE", false, "Software Engineer", "대학교 재학", "미혼"),
            ).collect { resultResponse(it, {}) }

            postCreateAutobiographyChapter()
        }
    }

    private fun postCreateAutobiographyChapter() {
        viewModelScope.launch {
            postCreateAutobiographyChaptersUseCase(
                CreateAutobiographyChaptersRequestModel(
                    listOf(
                        CreateChapterItemModel(
                            "1",
                            "유년기",
                            "출생부터 10세까지",
                            listOf(
                                CreateSubChapterItemModel("1.1", "첫 기억", "가장 오래된 추억, 가족과 함께한 행복한 순간"),
                            ),
                        ),
                    ),
                ),
            ).collect { resultResponse(it, {}) }

            emitEventFlow(LogInEvent.GoToOnBoardingPage)
        }
    }
}
