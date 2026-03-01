package com.tdd.talktobook.feature.setting

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.Platform
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.core.ui.util.setDateStringType
import com.tdd.talktobook.core.ui.util.setTimeStringType
import com.tdd.talktobook.domain.entity.request.firestore.InquiryRequestModel
import com.tdd.talktobook.domain.entity.response.member.MemberInfoResponseModel
import com.tdd.talktobook.domain.usecase.auth.DeleteLocalAllDataUseCase
import com.tdd.talktobook.domain.usecase.auth.DeleteUserUseCase
import com.tdd.talktobook.domain.usecase.auth.LogOutUseCase
import com.tdd.talktobook.domain.usecase.firestore.PostInquiryUseCase
import com.tdd.talktobook.domain.usecase.member.GetMemberInfoUseCase
import com.tdd.talktobook.getPlatform
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SettingViewModel(
    private val getMemberInfoUseCase: GetMemberInfoUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val logOutUseCase: LogOutUseCase,
    private val deleteLocalAllDataUseCase: DeleteLocalAllDataUseCase,
    private val postInquiryUseCase: PostInquiryUseCase
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

    fun setInquiryInput(inquiry: String) {
        val platform = getPlatform().name
        val current = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
        val date = setDateStringType(current.year.toString(), current.monthNumber.toString(), current.dayOfMonth.toString())
        val time = setTimeStringType(current.hour.toString(), current.minute.toString(), current.second.toString())

        val inquiryData = InquiryRequestModel("", inquiry, platform, "$date $time")
        d("[테스트] $inquiryData")

//        viewModelScope.launch {
//            postInquiryUseCase(inquiry).collect { resultResponse(it, { id ->
//                d("[테스트] $id")
//            }) }
//        }
    }
}
