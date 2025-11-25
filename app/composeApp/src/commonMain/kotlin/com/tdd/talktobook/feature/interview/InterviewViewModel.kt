package com.tdd.talktobook.feature.interview

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.domain.entity.enums.AutobiographyStatusType
import com.tdd.talktobook.domain.entity.enums.ChatType
import com.tdd.talktobook.domain.entity.request.autobiography.ChangeAutobiographyStatusRequestModel
import com.tdd.talktobook.domain.entity.request.interview.ai.ChatInterviewRequestModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewChatItem
import com.tdd.talktobook.domain.entity.response.interview.InterviewConversationListModel
import com.tdd.talktobook.domain.usecase.autobiograph.ChangeAutobiographyStatusUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetAutobiographyIdUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetAutobiographyStatusUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.SaveCurrentAutobiographyStatusUseCase
import com.tdd.talktobook.domain.usecase.interview.GetInterviewConversationUseCase
import com.tdd.talktobook.domain.usecase.interview.GetInterviewIdUseCase
import com.tdd.talktobook.domain.usecase.interview.ai.PostChatInterviewUseCase
import com.tdd.talktobook.feature.interview.type.ConversationType
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class InterviewViewModel(
    private val getAutobiographyIdUseCase: GetAutobiographyIdUseCase,
    private val postChatInterviewUseCase: PostChatInterviewUseCase,
    private val getAutobiographyStatusUseCase: GetAutobiographyStatusUseCase,
    private val getInterviewConversationUseCase: GetInterviewConversationUseCase,
    private val getInterviewIdUseCase: GetInterviewIdUseCase,
    private val changeAutobiographyStatusUseCase: ChangeAutobiographyStatusUseCase,
    private val saveAutobiographyStatusUseCase: SaveCurrentAutobiographyStatusUseCase
) : BaseViewModel<InterviewPageState>(
    InterviewPageState(),
) {
    fun getFirstQuestion(question: String) {
        if (question.isNotEmpty()) {
            addInterviewConversation(question, ChatType.BOT)
        } else {
            initGetAutobiographyStatus()
        }
    }

    private fun initGetAutobiographyStatus() {
        viewModelScope.launch {
            getAutobiographyStatusUseCase(Unit).collect { resultResponse(it, ::onSuccessGetStatus) }
        }
    }

    private fun onSuccessGetStatus(data: AutobiographyStatusType) {
        d("[ktor] interview -> ${data.type}")

        if (data == AutobiographyStatusType.EMPTY) {
            emitEventFlow(InterviewEvent.ShowStartAutobiographyDialog)
        } else {
            startInterview()
        }
    }

    private fun startInterview() {
        initGetAutobiographyId()
        initGetInterviewId()
    }

    private fun initGetAutobiographyId() {
        viewModelScope.launch {
            getAutobiographyIdUseCase(Unit).collect { resultResponse(it, ::onSuccessGetAutobiographyId) }
        }
    }

    private fun onSuccessGetAutobiographyId(id: Int) {
        updateState(
            uiState.value.copy(
                autobiographyId = id,
            ),
        )

        changeAutobiographyStatus() // TODO 삭제
    }

    private fun initGetInterviewId() {
        viewModelScope.launch {
            getInterviewIdUseCase(Unit).collect { resultResponse(it, ::onSuccessGetInterviewId) }
        }
    }

    private fun onSuccessGetInterviewId(id: Int) {
        updateState(
            uiState.value.copy(
                interviewId = id,
            ),
        )

        initGetInterviewConversation(id)
    }

    private fun initGetInterviewConversation(id: Int) {
        viewModelScope.launch {
            getInterviewConversationUseCase(id).collect { resultResponse(it, ::onSuccessGetConversation) }
        }
    }

    private fun onSuccessGetConversation(data: InterviewConversationListModel) {
        d("[ktor] interview -> ${data.results}")

        updateState(
            uiState.value.copy(
                interviewChatList = data.results,
            ),
        )
    }

    private fun addInterviewConversation(
        chatContent: String,
        chatType: ChatType,
    ) {
        d("[ktor] interview -> $chatContent")

        val currentConversation = InterviewChatItem(content = chatContent, chatType = chatType)
        val updatedChatList = uiState.value.interviewChatList + currentConversation

        updateState(
            uiState.value.copy(
                interviewChatList = updatedChatList,
            ),
        )
    }

    fun beginInterview() {
        updateState(
            uiState.value.copy(
                interviewProgressType = ConversationType.ING,
            ),
        )
    }

    fun setInterviewAnswer(chat: String) {
        addInterviewConversation(chat, ChatType.HUMAN)

        updateState(
            uiState.value.copy(
                interviewProgressType = ConversationType.BEFORE,
            ),
        )

        postInterviewAnswer(chat)
    }

    private fun postInterviewAnswer(chat: String) {
        viewModelScope.launch {
            postChatInterviewUseCase(ChatInterviewRequestModel(uiState.value.autobiographyId, chat))
                .collect { resultResponse(it, { data -> addInterviewConversation(data.text, ChatType.BOT) }) }
        }
    }

    private fun checkIsAutobiographyEnough() {
        // TODO 인터뷰 3분 경과 시

//        changeAutobiographyStatus()
    }

    private fun changeAutobiographyStatus() {
        viewModelScope.launch {
            changeAutobiographyStatusUseCase(ChangeAutobiographyStatusRequestModel(uiState.value.autobiographyId, AutobiographyStatusType.ENOUGH)).collect {
                resultResponse(it, {})
            }
        }

        saveAutobiographyStatus()
        emitEventFlow(InterviewEvent.ShowCreateAutobiographyDialog)
    }

    private fun saveAutobiographyStatus() {
        viewModelScope.launch {
            saveAutobiographyStatusUseCase(AutobiographyStatusType.ENOUGH).collect { resultResponse(it, {})}
        }
    }

    fun createCurrentAutobiography() {
        d("[test] interview -> 자서전 생성 요청")
    }
}
