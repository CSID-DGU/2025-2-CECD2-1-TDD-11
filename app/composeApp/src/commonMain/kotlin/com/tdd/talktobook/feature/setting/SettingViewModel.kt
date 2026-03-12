package com.tdd.talktobook.feature.setting

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.core.ui.util.setDateStringType
import com.tdd.talktobook.core.ui.util.setTimeStringType
import com.tdd.talktobook.domain.entity.request.firestore.FireStoreRequestModel
import com.tdd.talktobook.domain.entity.response.member.MemberInfoResponseModel
import com.tdd.talktobook.domain.usecase.auth.DeleteLocalAllDataUseCase
import com.tdd.talktobook.domain.usecase.auth.DeleteUserUseCase
import com.tdd.talktobook.domain.usecase.auth.GetUserEmailUseCase
import com.tdd.talktobook.domain.usecase.auth.LogOutUseCase
import com.tdd.talktobook.domain.usecase.firestore.PostFeedbackUseCase
import com.tdd.talktobook.domain.usecase.firestore.PostInquiryUseCase
import com.tdd.talktobook.domain.usecase.member.GetMemberInfoUseCase
import com.tdd.talktobook.getPlatform
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SettingViewModel(
    private val getMemberInfoUseCase: GetMemberInfoUseCase,
    private val getUserEmailUseCase: GetUserEmailUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val logOutUseCase: LogOutUseCase,
    private val deleteLocalAllDataUseCase: DeleteLocalAllDataUseCase,
    private val postInquiryUseCase: PostInquiryUseCase,
    private val postFeedbackUseCase: PostFeedbackUseCase,
) : BaseViewModel<SettingPageState>(
        SettingPageState(),
    ) {
    init {
        initSetMemberInfo()
        initGetUserEmail()
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

    private fun initGetUserEmail() {
        viewModelScope.launch {
            getUserEmailUseCase(Unit).collect { resultResponse(it, ::onSuccessGetUserEmail) }
        }
    }

    private fun onSuccessGetUserEmail(data: String) {
        updateState { state ->
            state.copy(
                userEmail = data,
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
        val userEmail = uiState.value.userEmail
        val platform = getPlatform().name

        val current =
            Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
        val date = setDateStringType(current.year.toString(), current.monthNumber.toString(), current.dayOfMonth.toString())
        val time = setTimeStringType(current.hour.toString(), current.minute.toString(), current.second.toString())

        val inquiryData = FireStoreRequestModel(userEmail, inquiry, platform, "$date $time")

        viewModelScope.launch {
            postInquiryUseCase(inquiryData).collect {
                resultResponse(it, { id ->
                    d("[fireStore] inquiry $id")
                    emitEventFlow(SettingEvent.ShowInquiryToast)
                })
            }
        }
    }

    fun setFeedbackInput(feedback: String) {
        val userEmail = uiState.value.userEmail
        val platform = getPlatform().name

        val current =
            Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
        val date = setDateStringType(current.year.toString(), current.monthNumber.toString(), current.dayOfMonth.toString())
        val time = setTimeStringType(current.hour.toString(), current.minute.toString(), current.second.toString())

        val feedbackData = FireStoreRequestModel(userEmail, feedback, platform, "$date $time")

        viewModelScope.launch {
            postFeedbackUseCase(feedbackData).collect {
                resultResponse(it, { id ->
                    d("[fireStore] feedback $id")
                    emitEventFlow(SettingEvent.ShowFeedbackToast)
                })
            }
        }
    }
}
