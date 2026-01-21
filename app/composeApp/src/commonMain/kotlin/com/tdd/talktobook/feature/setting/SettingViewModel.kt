package com.tdd.talktobook.feature.setting

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.domain.entity.request.page.OneBtnDialogModel
import com.tdd.talktobook.domain.entity.response.member.MemberInfoResponseModel
import com.tdd.talktobook.domain.usecase.auth.DeleteLocalAllDataUseCase
import com.tdd.talktobook.domain.usecase.auth.DeleteUserUseCase
import com.tdd.talktobook.domain.usecase.auth.LogOutUseCase
import com.tdd.talktobook.domain.usecase.member.GetMemberInfoUseCase
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SettingViewModel(
    private val getMemberInfoUseCase: GetMemberInfoUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val logOutUseCase: LogOutUseCase,
    private val deleteLocalAllDataUseCase: DeleteLocalAllDataUseCase,
) : BaseViewModel<SettingPageState>(
        SettingPageState(),
    ) {
    init {
        initSetMemberInfo()
    }

    private fun initSetMemberInfo() {
        viewModelScope.launch {
            getMemberInfoUseCase(Unit).collect { resultResponse(it, ::onSuccessGetMemberInfo) }
        }
    }

    private fun onSuccessGetMemberInfo(data: MemberInfoResponseModel) {
        d("[ktor] settingViewmodel -> $data")
        updateState { state ->
            state.copy(
                memberInfo = data,
            )
        }
    }

    fun logOut() {
        viewModelScope.launch {
            d("[ktor] setting -> logout")
            logOutUseCase(Unit).collect {
                resultResponse(it, {})
            }
        }

        clearAllData()
    }

    fun deleteUser() {
        viewModelScope.launch {
            deleteUserUseCase(Unit).collect {
                resultResponse(it, {})
            }
        }

        clearAllData()
    }

    private fun clearAllData() {
        viewModelScope.launch {
            d("[ktor] setting -> clear data")
            deleteLocalAllDataUseCase(Unit).collect {
                resultResponse(it, {})
            }
        }

        emitEventFlow(SettingEvent.GoToLogInPage)
    }
}
