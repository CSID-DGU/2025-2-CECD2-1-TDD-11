package com.tdd.bookshelf.feature.interview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.bookshelf.core.designsystem.Blue300
import com.tdd.bookshelf.core.designsystem.BookShelfTypo
import com.tdd.bookshelf.core.designsystem.Gray50
import com.tdd.bookshelf.core.designsystem.Gray600
import com.tdd.bookshelf.core.designsystem.InterviewScreenTitle
import com.tdd.bookshelf.core.designsystem.White0
import com.tdd.bookshelf.core.ui.common.content.TopBarContent
import com.tdd.bookshelf.core.ui.util.rememberSpeechToText
import com.tdd.bookshelf.domain.entity.enums.ChatType
import com.tdd.bookshelf.domain.entity.response.interview.InterviewChatItem
import com.tdd.bookshelf.feature.interview.type.ConversationType
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun InterviewScreen(
    interviewId: Int,
    goBackPage: () -> Unit,
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
        viewModel.setInterview(interviewId)
    }

    InterviewContent(
        interviewChatList = mergedChat,
        interactionSource = interactionSource,
        onClickBack = { goBackPage() },
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
    onClickBack: () -> Unit = {},
    interviewProgressType: ConversationType = ConversationType.BEFORE,
    isInterviewProgressIng: Boolean = false,
    onStartInterview: () -> Unit = {},
    onFinishInterview: () -> Unit = {},
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(White0),
    ) {
        TopBarContent(
            content = InterviewScreenTitle,
            interactionSource = interactionSource,
            onClickIcon = onClickBack,
        )

        InterviewChat(
            modifier = Modifier.weight(1f),
            interviewChatList = interviewChatList,
        )

        Box(
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 35.dp)
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Blue300)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = {
                            if (isInterviewProgressIng) {
                                onFinishInterview()
                            } else {
                                onStartInterview()
                            }
                        },
                    ),
        ) {
            Image(
                painter =
                    painterResource(
                        ConversationType.getConversationBtnImg(
                            interviewProgressType,
                        ),
                    ),
                contentDescription = "interview btn",
                modifier =
                    Modifier
                        .width(45.dp)
                        .align(Alignment.Center),
            )
        }
    }
}

@Composable
private fun InterviewChat(
    modifier: Modifier,
    interviewChatList: List<InterviewChatItem>,
) {
    BoxWithConstraints(
        modifier =
            modifier
                .padding(bottom = 120.dp)
                .fillMaxWidth(),
    ) {
        val chatMaxWidth = maxWidth * 0.7f

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            interviewChatList.forEachIndexed { index, chatItem ->
                when (chatItem.chatType) {
                    ChatType.BOT -> {
                        InterviewBotChatItem(
                            content = chatItem.content,
                            modifier =
                                Modifier
                                    .align(Alignment.Start)
                                    .padding(start = 20.dp)
                                    .widthIn(max = chatMaxWidth),
                        )
                    }

                    ChatType.HUMAN -> {
                        InterviewHumanChatItem(
                            content = chatItem.content,
                            modifier =
                                Modifier
                                    .align(Alignment.End)
                                    .padding(end = 20.dp)
                                    .widthIn(max = chatMaxWidth),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InterviewBotChatItem(
    content: String,
    modifier: Modifier,
) {
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomEnd = 12.dp))
                .background(Gray50),
    ) {
        Text(
            text = content,
            color = Gray600,
            style = BookShelfTypo.Regular,
            fontSize = 14.sp,
            modifier =
                Modifier
                    .padding(top = 20.dp, bottom = 20.dp, start = 16.dp, end = 20.dp),
        )
    }
}

@Composable
private fun InterviewHumanChatItem(
    content: String,
    modifier: Modifier,
) {
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 12.dp))
                .background(Blue300),
    ) {
        Text(
            text = content,
            color = White0,
            style = BookShelfTypo.Regular,
            fontSize = 14.sp,
            modifier =
                Modifier
                    .padding(top = 20.dp, bottom = 20.dp, end = 16.dp, start = 20.dp),
        )
    }
}

@Preview
@Composable
fun PreviewInterview() {
    InterviewContent()
}
