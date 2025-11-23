package com.tdd.talktobook.feature.interview

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.domain.entity.enums.ChatType
import com.tdd.talktobook.domain.entity.request.interview.ai.ChatInterviewRequestModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewChatItem
import com.tdd.talktobook.domain.usecase.autobiograph.GetLastQuestionUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.SaveLastQuestionUseCase
import com.tdd.talktobook.domain.usecase.interview.ai.PostChatInterviewUseCase
import com.tdd.talktobook.feature.interview.type.ConversationType
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class InterviewViewModel(
    private val getLastQuestionUseCase: GetLastQuestionUseCase,
    private val saveLastQuestionUseCase: SaveLastQuestionUseCase,
    private val postChatInterviewUseCase: PostChatInterviewUseCase,
) : BaseViewModel<InterviewPageState>(
    InterviewPageState(),
) {
    init {
        initGetLastQuestion()
    }

    private fun initGetLastQuestion() {
        viewModelScope.launch {
            getLastQuestionUseCase(Unit).collect {
                resultResponse(it, { data -> addInterviewConversation(data, ChatType.BOT) })
            }
        }
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

        saveLastQuestion(chatContent, chatType)
    }

    private fun saveLastQuestion(chat: String, chatType: ChatType) {
        if (chatType == ChatType.BOT) {
            viewModelScope.launch {
                saveLastQuestionUseCase(chat).collect { resultResponse(it, {}) }
            }
        }
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
                .collect { resultResponse(it, { data -> addInterviewConversation(chat, ChatType.BOT) }) }
        }
    }
}
