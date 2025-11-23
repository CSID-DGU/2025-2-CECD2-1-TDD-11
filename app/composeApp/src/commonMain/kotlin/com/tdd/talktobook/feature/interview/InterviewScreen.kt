package com.tdd.talktobook.feature.interview

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.designsystem.BackGround2
import com.tdd.talktobook.core.designsystem.Empty
import com.tdd.talktobook.core.designsystem.InterviewScreenTitle
import com.tdd.talktobook.core.designsystem.NextTime
import com.tdd.talktobook.core.designsystem.StartAutobiographyDialogBtn
import com.tdd.talktobook.core.designsystem.StartAutobiographyDialogContent
import com.tdd.talktobook.core.designsystem.StartAutobiographyDialogTitle
import com.tdd.talktobook.core.ui.common.button.RectangleBtn
import com.tdd.talktobook.core.ui.common.content.InterviewList
import com.tdd.talktobook.core.ui.common.content.TopBarContent
import com.tdd.talktobook.core.ui.common.dialog.OneBtnDialog
import com.tdd.talktobook.core.ui.util.rememberSpeechToText
import com.tdd.talktobook.domain.entity.enums.ChatType
import com.tdd.talktobook.domain.entity.request.page.OneBtnDialogModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewChatItem
import com.tdd.talktobook.feature.interview.type.ConversationType
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun InterviewScreen(
    showStartAutobiographyDialog: (OneBtnDialogModel) -> Unit
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
                d("[인터뷰] 대화 -> $partial")
                uiState.interviewChatList +
                    InterviewChatItem(
                        content = partial,
                        chatType = ChatType.HUMAN,
                    )
            } else {
                uiState.interviewChatList
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
                            bottomText = NextTime
                        )
                    )
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
            scope.launch {
                partial = ""
                viewModel.beginInterview()
                stt.start { p -> partial = p }
            }
        },
        onFinishInterview = {
            scope.launch {
                val finalText = stt.stop()
                val text = finalText.ifBlank { partial }
                viewModel.setInterviewAnswer(text)
                partial = ""
            }
        },
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
    onFinishInterview: () -> Unit = {},
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
        )

        RectangleBtn(
            btnContent =
                ConversationType.getConversationBtnImg(
                    interviewProgressType,
                ),
            isBtnActivated = true,
            onClickAction = {
                if (isInterviewProgressIng) {
                    onFinishInterview()
                } else {
                    onStartInterview()
                }
            },
        )

        Spacer(modifier = Modifier.height(15.dp))
    }
}

@Preview
@Composable
fun PreviewInterview() {
    InterviewContent()
}
