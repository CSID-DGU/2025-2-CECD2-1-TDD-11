package com.tdd.bookshelf.feature.interview

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.bookshelf.core.designsystem.BackGround2
import com.tdd.bookshelf.core.designsystem.InterviewScreenTitle
import com.tdd.bookshelf.core.designsystem.White0
import com.tdd.bookshelf.core.ui.common.button.RectangleBtn
import com.tdd.bookshelf.core.ui.common.content.InterviewList
import com.tdd.bookshelf.core.ui.common.content.TopBarContent
import com.tdd.bookshelf.core.ui.util.rememberSpeechToText
import com.tdd.bookshelf.domain.entity.enums.ChatType
import com.tdd.bookshelf.domain.entity.response.interview.InterviewChatItem
import com.tdd.bookshelf.feature.interview.type.ConversationType
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun InterviewScreen() {
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
            iconVisible = false
        )

        InterviewList(
            interviewList = interviewChatList,
            modifier = Modifier.weight(1f)
        )

        RectangleBtn(
            btnContent = ConversationType.getConversationBtnImg(
                interviewProgressType,
            ),
            isBtnActivated = true,
            onClickAction = {
                if (isInterviewProgressIng) {
                    onFinishInterview()
                } else {
                    onStartInterview()
                }
            }
        )

        Spacer(modifier = Modifier.height(15.dp))
    }
}

@Preview
@Composable
fun PreviewInterview() {
    InterviewContent()
}
