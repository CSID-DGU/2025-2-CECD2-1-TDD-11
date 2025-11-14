package com.tdd.onboarding

import androidx.lifecycle.viewModelScope
import com.tdd.domain.entity.request.CreateUserModel
import com.tdd.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(

): BaseViewModel<OnBoardingPageState>(
    OnBoardingPageState()
) {

    fun setUserModel(userModel: CreateUserModel) {
        updateState(
            uiState.value.copy(
                userModel = userModel
            )
        )

        Timber.d("[온보딩] 전달값 -> $userModel")
        createUser(userModel)
    }

    private fun createUser(user: CreateUserModel) {
        // TODO 유저 생성 서버통신

        viewModelScope.launch {
            delay(3000L)
            emitEventFlow(OnBoardingEvent.GoToInterViewPage)
        }
    }
}