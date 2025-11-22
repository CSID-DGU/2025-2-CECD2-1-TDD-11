package com.tdd.talktobook.feature.setting

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.domain.entity.response.member.MemberInfoResponseModel
import com.tdd.talktobook.domain.usecase.member.GetMemberInfoUseCase
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SettingViewModel(
    private val getMemberInfoUseCase: GetMemberInfoUseCase,
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
        updateState(
            uiState.value.copy(
                memberInfo = data,
            ),
        )
    }
}
