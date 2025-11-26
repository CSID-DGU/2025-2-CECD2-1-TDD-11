package com.tdd.talktobook.feature.interview

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.designsystem.SkipQuestionReason
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.domain.entity.enums.AutobiographyStatusType
import com.tdd.talktobook.domain.entity.enums.ChatType
import com.tdd.talktobook.domain.entity.request.autobiography.ChangeAutobiographyStatusRequestModel
import com.tdd.talktobook.domain.entity.request.autobiography.CreateAutobiographyRequestModel
import com.tdd.talktobook.domain.entity.request.interview.ai.ChatInterviewRequestModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewChatItem
import com.tdd.talktobook.domain.entity.response.interview.InterviewConversationListModel
import com.tdd.talktobook.domain.usecase.auth.DeleteLocalAllDataUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.ChangeAutobiographyStatusUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetAutobiographyIdUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetAutobiographyStatusUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.PatchCreateAutobiographyUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.SaveCurrentAutobiographyStatusUseCase
import com.tdd.talktobook.domain.usecase.interview.GetInterviewConversationUseCase
import com.tdd.talktobook.domain.usecase.interview.GetInterviewIdUseCase
import com.tdd.talktobook.domain.usecase.interview.ai.PostChatInterviewUseCase
import com.tdd.talktobook.feature.interview.type.ConversationType
import com.tdd.talktobook.feature.interview.type.SkipQuestionType
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class InterviewViewModel(
    private val getAutobiographyIdUseCase: GetAutobiographyIdUseCase,
    private val postChatInterviewUseCase: PostChatInterviewUseCase,
    private val getAutobiographyStatusUseCase: GetAutobiographyStatusUseCase,
    private val getInterviewConversationUseCase: GetInterviewConversationUseCase,
    private val getInterviewIdUseCase: GetInterviewIdUseCase,
    private val changeAutobiographyStatusUseCase: ChangeAutobiographyStatusUseCase,
    private val saveAutobiographyStatusUseCase: SaveCurrentAutobiographyStatusUseCase,
    private val createAutobiographyUseCase: PatchCreateAutobiographyUseCase,
    private val deleteLocalAllDataUseCase: DeleteLocalAllDataUseCase
) : BaseViewModel<InterviewPageState>(
    InterviewPageState(),
) {
    private val viewModelCreatedAt: Long = Clock.System.now().toEpochMilliseconds()

    init {
        initGetAutobiographyStatus()
    }


    fun setUserNickName(name: String) {
        updateState(
            uiState.value.copy(
                nickName = name
            )
        )
    }

    fun getFirstQuestion(question: String) {
        d("[test] interview -> 4 get first question")
        if (question.isNotEmpty()) {
            addInterviewConversation(question, ChatType.BOT)
        } else {
//            initGetAutobiographyStatus()
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
        updateState(
            uiState.value.copy(
                autobiographyId = id,
            ),
        )
    }

    private fun initGetInterviewId() {
        viewModelScope.launch {
            getInterviewIdUseCase(Unit).collect { resultResponse(it, ::onSuccessGetInterviewId) }
        }
    }

    private fun onSuccessGetInterviewId(id: Int) {
        d("[test] interview -> 3 get interview id")
        updateState(
            uiState.value.copy(
                interviewId = id,
            ),
        )

//        initGetInterviewConversation(id)
    }

    private fun getInterviewConversation() {
        viewModelScope.launch {
            getInterviewConversationUseCase(uiState.value.interviewId).collect { resultResponse(it, ::onSuccessGetConversation) }
        }

        d("[test] interview -> 5 get conversation (test)")
    }

    private fun onSuccessGetConversation(data: InterviewConversationListModel) {
        d("[ktor] interview -> ${data.results}")
        d("[test] interview -> 5 get conversation")

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

        checkIsAutobiographyEnough()
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


        val nowMillis = Clock.System.now().toEpochMilliseconds()
        val elapsedMillis = nowMillis - viewModelCreatedAt
        val threeMinutesMillis = 3 * 60 * 1000L

        if (elapsedMillis >= threeMinutesMillis) {
            d("[ktor] interview -> 3분 경과, 자서전 상태 변경")

            changeAutobiographyStatus()
        }
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
            saveAutobiographyStatusUseCase(AutobiographyStatusType.ENOUGH).collect { resultResponse(it, {}) }
        }
    }

    fun createCurrentAutobiography() {
        d("[ktor] interview -> 자서전 생성 요청 name: ${uiState.value.nickName}")
        viewModelScope.launch {
            createAutobiographyUseCase(CreateAutobiographyRequestModel(uiState.value.autobiographyId, uiState.value.nickName)).collect { resultResponse(it, {}) }
        }

        initClearLocalData()
    }

    private fun initClearLocalData() {
        viewModelScope.launch {
            deleteLocalAllDataUseCase(Unit).collect { resultResponse(it, {}) }
        }
    }

    fun setSkipQuestion(skipType: SkipQuestionType) {
        //TODO 질문 넘기는 이유 이벤트 설정
        d("[ktor] interview -> 질문 넘기기: $skipType")

        updateState(
            uiState.value.copy(
                interviewProgressType = ConversationType.BEFORE,
            ),
        )

        postInterviewAnswer(SkipQuestionReason)
    }
}
