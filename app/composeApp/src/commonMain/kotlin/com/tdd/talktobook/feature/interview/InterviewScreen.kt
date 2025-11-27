package com.tdd.talktobook.feature.interview

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.designsystem.BackGround2
import com.tdd.talktobook.core.designsystem.CreateAutobiographyDialogBtn
import com.tdd.talktobook.core.designsystem.CreateAutobiographyDialogContent
import com.tdd.talktobook.core.designsystem.CreateAutobiographyDialogTitle
import com.tdd.talktobook.core.designsystem.InterviewContinuous
import com.tdd.talktobook.core.designsystem.InterviewReAnswer
import com.tdd.talktobook.core.designsystem.InterviewScreenTitle
import com.tdd.talktobook.core.designsystem.NextTime
import com.tdd.talktobook.core.designsystem.SkipQuestionBottomHint
import com.tdd.talktobook.core.designsystem.SkipQuestionContent
import com.tdd.talktobook.core.designsystem.SkipQuestionFirstBtn
import com.tdd.talktobook.core.designsystem.SkipQuestionSecondBtn
import com.tdd.talktobook.core.designsystem.SkipQuestionTitle
import com.tdd.talktobook.core.designsystem.StartAutobiographyDialogBtn
import com.tdd.talktobook.core.designsystem.StartAutobiographyDialogContent
import com.tdd.talktobook.core.designsystem.StartAutobiographyDialogTitle
import com.tdd.talktobook.core.navigation.NavRoutes
import com.tdd.talktobook.core.ui.common.button.RectangleBtn
import com.tdd.talktobook.core.ui.common.content.InterviewList
import com.tdd.talktobook.core.ui.common.content.TopBarContent
import com.tdd.talktobook.core.ui.common.type.FlowType
import com.tdd.talktobook.core.ui.util.rememberMicPermissionRequester
import com.tdd.talktobook.core.ui.util.rememberSpeechToText
import com.tdd.talktobook.domain.entity.enums.ChatType
import com.tdd.talktobook.domain.entity.request.page.OneBtnDialogModel
import com.tdd.talktobook.domain.entity.request.page.TwoBtnDialogModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewChatItem
import com.tdd.talktobook.feature.interview.type.ConversationType
import com.tdd.talktobook.feature.interview.type.SkipQuestionType
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun InterviewScreen(
    showStartAutobiographyDialog: (OneBtnDialogModel) -> Unit,
    showCreateAutobiographyDialog: (OneBtnDialogModel) -> Unit,
    showSkipQuestionDialog: (TwoBtnDialogModel) -> Unit,
    goBackToLogIn: () -> Unit,
    startQuestion: String = "",
    nickName: StateFlow<String>,
    navController: NavController,
    flowType: StateFlow<FlowType>,
) {
    val viewModel: InterviewViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val interactionSource = remember { MutableInteractionSource() }

    val stt = rememberSpeechToText()
    val scope = rememberCoroutineScope()
    var partial by remember { mutableStateOf("") }
    val mergedChat =
        remember(uiState.interviewChatList, uiState.interviewProgressType, partial) {
            if (uiState.interviewProgressType == ConversationType.ING && partial.isNotBlank()) {
                d("[stt] 대화 -> $partial")
                uiState.interviewChatList +
                        InterviewChatItem(
                            content = partial,
                            chatType = ChatType.HUMAN,
                        )
            } else {
                uiState.interviewChatList
            }
        }

    val requestMicPermission = rememberMicPermissionRequester(
        onPermissionGranted = {
            scope.launch {
                partial = ""
                viewModel.beginInterview()
                stt.start { p -> partial = p }
            }
        },
        onPermissionDeniedPermanently = {
            d("[test] interview 권한 거부")
        }
    )

    LaunchedEffect(flowType) {
        flowType.collect {
            viewModel.setFlowType(it)
        }
    }

    LaunchedEffect(uiState.interviewId) {
        if (uiState.interviewId != 0) {
            viewModel.getFirstQuestion(startQuestion)
        }
    }

    LaunchedEffect(nickName) {
        nickName.collect {
            viewModel.setUserNickName(it)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is InterviewEvent.ShowStartAutobiographyDialog -> {
                    showStartAutobiographyDialog(
                        OneBtnDialogModel(
                            title = StartAutobiographyDialogTitle,
                            semiTitle = StartAutobiographyDialogContent,
                            btnText = StartAutobiographyDialogBtn,
                            isBottomTextVisible = true,
                            bottomText = NextTime,
                            onClickBtn = { navController.navigate(NavRoutes.StartProgressScreen.route) },
                            onClickBottomText = { navController.navigate(NavRoutes.HomeScreen.route) }
                        ),
                    )
                }

                is InterviewEvent.ShowCreateAutobiographyDialog -> {
                    showCreateAutobiographyDialog(
                        OneBtnDialogModel(
                            title = CreateAutobiographyDialogTitle,
                            semiTitle = CreateAutobiographyDialogContent,
                            btnText = CreateAutobiographyDialogBtn,
                            isBottomTextVisible = true,
                            bottomText = NextTime,
                            onClickBtn = { viewModel.createCurrentAutobiography() },
                            onClickBottomText = {}
                        )
                    )
                }

                is InterviewEvent.GoBackToLogIn -> {
                    goBackToLogIn()
                }
            }
        }
    }

    InterviewContent(
        interviewChatList = mergedChat,
        interactionSource = interactionSource,
        interviewProgressType = uiState.interviewProgressType,
        isInterviewProgressIng = (uiState.interviewProgressType == ConversationType.ING),
        onStartInterview = {
            requestMicPermission()
        },
        onSetInterview = {
            scope.launch {
                val finalText = stt.stop()
                val text = finalText.ifBlank { partial }
                viewModel.setInterviewAnswer(text)
                partial = ""
            }
        },
        onSetInterviewReAnswer = { viewModel.setInterviewReAnswer() },
        onSetInterviewContinuous = { viewModel.setInterviewContinuous() },
        onSetInterviewRequestNextQuestion = { viewModel.setInterviewRequestNextQuestion() },
        onItemLongClick = {
            if (uiState.flowType == FlowType.DEFAULT) {
                showSkipQuestionDialog(
                    TwoBtnDialogModel(
                        title = SkipQuestionTitle,
                        semiTitle = SkipQuestionContent,
                        firstBtnText = SkipQuestionFirstBtn,
                        onClickBtnFirst = { viewModel.setSkipQuestion(SkipQuestionType.A) },
                        secondBtnText = SkipQuestionSecondBtn,
                        onClickBtnSecond = { viewModel.setSkipQuestion(SkipQuestionType.B) },
                        bottomBtnText = SkipQuestionBottomHint,
                        onClickBottomText = { viewModel.setSkipQuestion(SkipQuestionType.DEFAULT) }
                    )
                )
            }
        },
        isStartAnswerBtnActivated = uiState.isStartAnswerBtnActivated
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun InterviewContent(
    interviewChatList: List<InterviewChatItem> = emptyList(),
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    interviewProgressType: ConversationType = ConversationType.BEFORE,
    isInterviewProgressIng: Boolean = false,
    onStartInterview: () -> Unit = {},
    onSetInterview: () -> Unit = {},
    onSetInterviewReAnswer: () -> Unit = {},
    onSetInterviewContinuous: () -> Unit = {},
    onSetInterviewRequestNextQuestion: () -> Unit = {},
    onItemLongClick: (InterviewChatItem) -> Unit = {},
    isStartAnswerBtnActivated: Boolean = false
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(BackGround2),
    ) {
        TopBarContent(
            content = InterviewScreenTitle,
            interactionSource = interactionSource,
            iconVisible = false,
        )

        InterviewList(
            interviewList = interviewChatList,
            modifier = Modifier.weight(1f),
            interactionSource = interactionSource,
            onItemLongClick = onItemLongClick
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (interviewProgressType.plusFirstBtn != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                RectangleBtn(
                    btnContent = InterviewReAnswer,
                    isBtnActivated = true,
                    onClickAction = onSetInterviewReAnswer,
                    modifier = Modifier.weight(1f)
                )

                RectangleBtn(
                    btnContent = InterviewContinuous,
                    isBtnActivated = true,
                    onClickAction = onSetInterviewContinuous,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        RectangleBtn(
            btnContent =
                ConversationType.getConversationBtnText(
                    interviewProgressType,
                ),
            isBtnActivated = isStartAnswerBtnActivated,
            onClickAction = {
                when (interviewProgressType) {
                    ConversationType.BEFORE -> { onStartInterview() }

                    ConversationType.ING -> { onSetInterview() }

                    ConversationType.FINISH -> { onSetInterviewRequestNextQuestion() }
                }
            },
        )

        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Preview
@Composable
fun PreviewInterview() {
    InterviewContent()
}
