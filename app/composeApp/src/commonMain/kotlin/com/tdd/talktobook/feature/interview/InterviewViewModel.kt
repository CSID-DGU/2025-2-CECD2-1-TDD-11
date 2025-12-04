package com.tdd.talktobook.feature.interview

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.designsystem.SkipQuestionReason
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.core.ui.common.type.FlowType
import com.tdd.talktobook.domain.entity.enums.AutobiographyStatusType
import com.tdd.talktobook.domain.entity.enums.ChatType
import com.tdd.talktobook.domain.entity.request.autobiography.CreateAutobiographyRequestModel
import com.tdd.talktobook.domain.entity.request.autobiography.GetCoShowGenerateRequestModel
import com.tdd.talktobook.domain.entity.request.interview.CoShowAnswerRequestModel
import com.tdd.talktobook.domain.entity.request.interview.ai.ChatInterviewRequestModel
import com.tdd.talktobook.domain.entity.response.interview.CoShowAnswerModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewChatItem
import com.tdd.talktobook.domain.entity.response.interview.InterviewConversationListModel
import com.tdd.talktobook.domain.entity.response.interview.ai.ChatInterviewResponseModel
import com.tdd.talktobook.domain.usecase.auth.DeleteLocalAllDataUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetAutobiographyIdUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetAutobiographyStatusUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetCoShowGenerateUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.PatchCreateAutobiographyUseCase
import com.tdd.talktobook.domain.usecase.interview.GetCoShowInterviewConversationUseCase
import com.tdd.talktobook.domain.usecase.interview.GetInterviewConversationUseCase
import com.tdd.talktobook.domain.usecase.interview.GetInterviewIdUseCase
import com.tdd.talktobook.domain.usecase.interview.PostCoShowAnswerUseCase
import com.tdd.talktobook.domain.usecase.interview.ai.PostChatInterviewUseCase
import com.tdd.talktobook.feature.interview.type.ConversationType
import com.tdd.talktobook.feature.interview.type.SkipQuestionType
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class InterviewViewModel(
    private val getAutobiographyIdUseCase: GetAutobiographyIdUseCase,
    private val postChatInterviewUseCase: PostChatInterviewUseCase,
    private val getAutobiographyStatusUseCase: GetAutobiographyStatusUseCase,
    private val getInterviewConversationUseCase: GetInterviewConversationUseCase,
    private val getInterviewIdUseCase: GetInterviewIdUseCase,
    private val createAutobiographyUseCase: PatchCreateAutobiographyUseCase,
    private val deleteLocalAllDataUseCase: DeleteLocalAllDataUseCase,
    private val getCoShowInterviewConversationUseCase: GetCoShowInterviewConversationUseCase,
    private val postCoShowAnswerUseCase: PostCoShowAnswerUseCase,
    private val getCoShowGenerateUseCase: GetCoShowGenerateUseCase,
) : BaseViewModel<InterviewPageState>(
        InterviewPageState(),
    ) {
    init {
        initGetAutobiographyStatus()
    }

    fun setFlowType(type: FlowType) {
        updateState { state ->
            state.copy(
                flowType = type,
            )
        }
    }

    fun setUserNickName(name: String) {
        d("[test] interview -> name: $name")
        updateState { state ->
            state.copy(
                nickName = name,
            )
        }
    }

    fun getFirstQuestion(question: String) {
        d("[test] interview -> 4 get first question")
        if (question.isNotEmpty()) {
            addInterviewConversation(question, ChatType.BOT)
        } else {
            getInterviewConversation()
        }
    }

    private fun initGetAutobiographyStatus() {
        d("[test] interview -> 1 get auto status")
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
        d("[test] interview -> 2 get auto id")
        updateState { state ->
            state.copy(
                autobiographyId = id,
            )
        }
    }

    private fun initGetInterviewId() {
        viewModelScope.launch {
            getInterviewIdUseCase(Unit).collect { resultResponse(it, ::onSuccessGetInterviewId) }
        }
    }

    private fun onSuccessGetInterviewId(id: Int) {
        d("[test] interview -> 3 get interview id")
        updateState { state ->
            state.copy(
                interviewId = id,
            )
        }
    }

    private fun getInterviewConversation() {
        when (uiState.value.flowType) {
            FlowType.DEFAULT -> {
                viewModelScope.launch {
                    getInterviewConversationUseCase(uiState.value.interviewId).collect { resultResponse(it, ::onSuccessGetConversation) }
                }
            }

            FlowType.COSHOW -> {
                viewModelScope.launch {
                    getCoShowInterviewConversationUseCase(uiState.value.interviewId).collect { resultResponse(it, ::onSuccessGetConversation) }
                }
            }
        }

        d("[test] interview -> 5 get conversation (test)")
    }

    private fun onSuccessGetConversation(data: InterviewConversationListModel) {
        d("[ktor] interview -> ${data.results}")
        d("[test] interview -> 5 get conversation")

        updateState { state ->
            state.copy(
                interviewChatList = data.results,
                answerInputs = emptyList(),
            )
        }
    }

    private fun addInterviewConversation(
        chatContent: String,
        chatType: ChatType,
    ) {
        d("[ktor] interview (add) -> $chatContent")

        val currentConversation = InterviewChatItem(content = chatContent, chatType = chatType)
        val updatedChatList = uiState.value.interviewChatList + currentConversation

        updateState { state ->
            state.copy(
                interviewChatList = updatedChatList,
            )
        }

        when (uiState.value.flowType) {
            FlowType.DEFAULT -> {
                checkIsAutobiographyEnough()
            }

            FlowType.COSHOW -> {
                checkIsAutobiographyEnoughInCoShow(chatType)
            }
        }
    }

    fun beginInterview() {
        updateState { state ->
            state.copy(
                interviewProgressType = ConversationType.ING,
            )
        }
    }

    fun setInterviewAnswer(chat: String) {
        val originalAnswer = uiState.value.answerInputs
        addInterviewConversation(chat, ChatType.HUMAN)

        updateState { state ->
            state.copy(
                interviewProgressType = ConversationType.FINISH,
                answerInputs = originalAnswer + chat,
            )
        }
    }

    fun setInterviewReAnswer() {
        val originalAnswers = uiState.value.answerInputs
        val currentList = uiState.value.interviewChatList

        val updatedAnswers = originalAnswers.dropLast(1)
        val updatedList = currentList.dropLast(1)

        updateState { state ->
            state.copy(
                answerInputs = updatedAnswers,
                interviewChatList = updatedList,
                interviewProgressType = ConversationType.BEFORE,
            )
        }
    }

    fun setInterviewContinuous() {
        updateState { state ->
            state.copy(
                interviewProgressType = ConversationType.BEFORE,
            )
        }
    }

    fun setInterviewRequestNextQuestion() {
        val answers = uiState.value.answerInputs
        val finalAnswer = answers.joinToString(separator = " ")
        val updatedList = deleteTemporaryLastAnswers() + InterviewChatItem(content = finalAnswer, chatType = ChatType.HUMAN)

        updateState { state ->
            state.copy(
                interviewProgressType = ConversationType.BEFORE,
                interviewChatList = updatedList,
                isStartAnswerBtnActivated = false,
            )
        }

        postInterviewAnswer(finalAnswer)
    }

    fun deleteTemporaryLastAnswers(): List<InterviewChatItem> {
        val currentList = uiState.value.interviewChatList
        if (currentList.isEmpty()) return currentList

        var index = currentList.lastIndex

        while (index >= 0 && currentList[index].chatType == ChatType.HUMAN) {
            index--
        }

        val newList =
            if (index == -1) {
                emptyList()
            } else {
                currentList.subList(0, index + 1)
            }

        return newList
    }

    private fun postInterviewAnswer(chat: String) {
        when (uiState.value.flowType) {
            FlowType.DEFAULT -> {
                defaultInterviewAnswer(chat)
            }

            FlowType.COSHOW -> {
                coShowInterviewAnswer(chat)
            }
        }
    }

    private fun defaultInterviewAnswer(chat: String) {
        viewModelScope.launch {
            postChatInterviewUseCase(ChatInterviewRequestModel(uiState.value.autobiographyId, chat))
                .collect { resultResponse(it, ::onSuccessInterviewAnswer) }
        }
    }

    private fun onSuccessInterviewAnswer(data: ChatInterviewResponseModel) {
        addInterviewConversation(data.text, ChatType.BOT)

        updateState { state ->
            state.copy(
                answerInputs = emptyList(),
                isStartAnswerBtnActivated = true,
            )
        }
    }

    private fun coShowInterviewAnswer(chat: String) {
        viewModelScope.launch {
            postCoShowAnswerUseCase(CoShowAnswerRequestModel(uiState.value.interviewId, chat))
                .collect { resultResponse(it, ::onSuccessCoShowInterviewAnswer) }
        }
    }

    private fun onSuccessCoShowInterviewAnswer(data: CoShowAnswerModel) {
        d("[ktor] interview answer -> $data")

        updateState { state ->
            state.copy(
                answerInputs = emptyList(),
                isLast = data.isLast,
                isStartAnswerBtnActivated = true,
            )
        }

        addInterviewConversation(data.question, ChatType.BOT)
    }

    private fun checkIsAutobiographyEnough() {
        viewModelScope.launch {
            getAutobiographyStatusUseCase(Unit).collect {
                resultResponse(it, ::onSuccessCheckAutobiographyStatus)
            }
        }
    }

    private fun onSuccessCheckAutobiographyStatus(data: AutobiographyStatusType) {
        if (data == AutobiographyStatusType.ENOUGH) {
            emitEventFlow(InterviewEvent.ShowCreateAutobiographyDialog)
        }
    }

    private fun checkIsAutobiographyEnoughInCoShow(type: ChatType) {
        d("[ktor] interview -> check auto enough: ${uiState.value.isLast}, $type")
        if (uiState.value.isLast && type == ChatType.HUMAN) {
            emitEventFlow(InterviewEvent.ShowCreateAutobiographyDialog)
        }
    }

    fun createCurrentAutobiography() {
        when (uiState.value.flowType) {
            FlowType.DEFAULT -> {
                createAutobiographyDefault()
            }

            FlowType.COSHOW -> {
                createAutobiographyInCoShow()
            }
        }
    }

    private fun createAutobiographyDefault() {
        d("[ktor] interview -> 자서전 생성 요청 name: ${uiState.value.nickName}")
        viewModelScope.launch {
            createAutobiographyUseCase(CreateAutobiographyRequestModel(uiState.value.autobiographyId, uiState.value.nickName)).collect { resultResponse(it, {}) }
        }

        initClearLocalData()
    }

    private fun createAutobiographyInCoShow() {
        d("[ktor] interview -> 자서전 생성 요청 name: ${uiState.value.nickName}")
        viewModelScope.launch {
            getCoShowGenerateUseCase(GetCoShowGenerateRequestModel(uiState.value.autobiographyId, uiState.value.nickName)).collect { resultResponse(it, {}) }
        }

        initClearLocalData()
        emitEventFlow(InterviewEvent.GoBackToLogIn)
    }

    private fun initClearLocalData() {
        viewModelScope.launch {
            deleteLocalAllDataUseCase(Unit).collect { resultResponse(it, {}) }
        }
    }

    fun setSkipQuestion(skipType: SkipQuestionType) {
        // TODO 질문 넘기는 이유 이벤트 설정
        d("[ktor] interview -> 질문 넘기기: $skipType")

        updateState { state ->
            state.copy(
                interviewProgressType = ConversationType.BEFORE,
            )
        }

        addInterviewConversation(SkipQuestionReason, ChatType.HUMAN)
        postInterviewAnswer(SkipQuestionReason)
    }
}
