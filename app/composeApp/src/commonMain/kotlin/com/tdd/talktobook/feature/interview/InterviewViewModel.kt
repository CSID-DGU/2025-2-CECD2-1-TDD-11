package com.tdd.talktobook.feature.interview

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.domain.entity.enums.ChatType
import com.tdd.talktobook.domain.entity.request.interview.ai.ChatInterviewRequestModel
import com.tdd.talktobook.domain.entity.request.interview.ai.StartInterviewRequestModel
import com.tdd.talktobook.domain.entity.response.autobiography.SelectedThemeModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewChatItem
import com.tdd.talktobook.domain.usecase.autobiograph.GetAutobiographyIdUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetAutobiographyStatusUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetSelectedThemeUseCase
import com.tdd.talktobook.domain.usecase.interview.GetInterviewConversationUseCase
import com.tdd.talktobook.domain.usecase.interview.GetInterviewQuestionListUseCase
import com.tdd.talktobook.domain.usecase.interview.ai.PostChatInterviewUseCase
import com.tdd.talktobook.domain.usecase.interview.ai.PostStartInterviewUseCase
import com.tdd.talktobook.feature.interview.type.ConversationType
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class InterviewViewModel(
    private val getInterviewConversationUseCase: GetInterviewConversationUseCase,
    private val getInterviewQuestionListUseCase: GetInterviewQuestionListUseCase,

    private val getAutobiographyStatusUseCase: GetAutobiographyStatusUseCase,
    private val getAutobiographyIdUseCase: GetAutobiographyIdUseCase,
    private val getSelectedThemeUseCase: GetSelectedThemeUseCase,
    private val postStartInterviewUseCase: PostStartInterviewUseCase,
    private val postChatInterviewUseCase: PostChatInterviewUseCase,
) : BaseViewModel<InterviewPageState>(
    InterviewPageState(),
) {
    init {
        initSetAutobiographyId()
    }

    private fun initSetAutobiographyId() {
        viewModelScope.launch {
            getAutobiographyIdUseCase(Unit).collect { resultResponse(it, ::onSuccessSetAutobiographyId) }
        }
    }

    private fun onSuccessSetAutobiographyId(id: Int) {
        updateState(
            uiState.value.copy(
                autobiographyId = id
            )
        )

        initGetSelectedTheme(id)
    }

    private fun initGetSelectedTheme(autobiographyId: Int) {
        viewModelScope.launch {
            getSelectedThemeUseCase(autobiographyId).collect {
                resultResponse(it, { selectedThemes -> initStartInterview(autobiographyId, selectedThemes) })
            }
        }
    }

    private fun initStartInterview(autobiographyId: Int, selectedThemes: SelectedThemeModel) {
        d("[ktor] interview -> autoId: $autobiographyId, categories -> ${selectedThemes.categories}")

        viewModelScope.launch {
            postStartInterviewUseCase(
                StartInterviewRequestModel(autobiographyId, selectedThemes.categories)
            ).collect { resultResponse(it, { data -> addInterviewConversation(data.text, ChatType.BOT) }) }
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
    }

    private fun saveLastQuestion(chat: String) {
        //
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
