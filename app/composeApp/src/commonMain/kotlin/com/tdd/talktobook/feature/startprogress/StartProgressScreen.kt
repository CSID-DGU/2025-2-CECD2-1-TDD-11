package com.tdd.talktobook.feature.startprogress

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tdd.talktobook.core.designsystem.BackGround2
import com.tdd.talktobook.core.designsystem.NickNameInputHint
import com.tdd.talktobook.core.designsystem.ReasonWriteHint
import com.tdd.talktobook.core.designsystem.StartProgressTitle
import com.tdd.talktobook.core.ui.common.button.RectangleBtn
import com.tdd.talktobook.core.ui.common.content.SeriesNumText
import com.tdd.talktobook.core.ui.common.content.SeriesTitleText
import com.tdd.talktobook.core.ui.common.content.TopBarContent
import com.tdd.talktobook.core.ui.common.item.SelectCircleListItem
import com.tdd.talktobook.core.ui.common.textfield.ExplainTextFieldBox
import com.tdd.talktobook.core.ui.common.textfield.TextFieldBox
import com.tdd.talktobook.domain.entity.enums.MaterialType
import com.tdd.talktobook.feature.startprogress.type.StartProgressPageType
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun StartProgressScreen(
    goToInterviewPage: (String) -> Unit,
    goBackToHome: () -> Unit,
    setUserNickName: (String) -> Unit,
) {
    val viewModel: StartProgressViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is StartProgressEvent.GoToInterviewPage -> {
                    goToInterviewPage(uiState.firstQuestion)
                }
            }
        }
    }

    StartProgressContent(
        pageType = uiState.pageType,
        onClickNext = { viewModel.setPageType(it) },
        onClickStartProgress = {
            setUserNickName(uiState.nickNameInput)
            viewModel.postStartProgress()
        },
        selectedMaterial = uiState.material,
        onSelectMaterial = { viewModel.setMaterial(it) },
        reasonInput = uiState.reasonInput,
        onReasonValueChange = { viewModel.onReasonValueChange(it) },
        isBtnActivated = uiState.isBtnActivated,
        interactionSource = interactionSource,
        onClickBack = { goBackToHome() },
        nickNameInput = uiState.nickNameInput,
        onNickNameValueChange = { viewModel.onNickNameValueChange(it) }
    )
}

@Composable
private fun StartProgressContent(
    pageType: StartProgressPageType = StartProgressPageType.BEGIN_PAGE,
    onClickNext: (StartProgressPageType) -> Unit,
    onClickStartProgress: () -> Unit,
    selectedMaterial: MaterialType,
    onSelectMaterial: (MaterialType) -> Unit,
    reasonInput: String,
    onReasonValueChange: (String) -> Unit,
    isBtnActivated: Boolean = false,
    interactionSource: MutableInteractionSource,
    onClickBack: () -> Unit,
    nickNameInput: String,
    onNickNameValueChange: (String) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(BackGround2),
    ) {
        TopBarContent(
            content = StartProgressTitle,
            onClickIcon = onClickBack,
            iconVisible = true,
            interactionSource = interactionSource,
        )

        Spacer(modifier = Modifier.padding(top = 15.dp))

        SeriesNumText(
            totalNum = 3,
            currentNum = pageType.page + 1,
        )

        SeriesTitleText(
            currentTitle = pageType.title,
            paddingTop = 10,
        )

        Column(
            modifier = Modifier.weight(1f),
        ) {
            when (pageType) {
                StartProgressPageType.BEGIN_PAGE -> {
                    Spacer(modifier = Modifier.padding(top = 50.dp))

                    TextFieldBox(
                        textInput = nickNameInput,
                        onValueChange = onNickNameValueChange,
                        hintText = NickNameInputHint
                    )
                }

                StartProgressPageType.FIRST_PAGE -> {
                    SelectMaterial(
                        selectedMaterial = selectedMaterial,
                        onSelectMaterial = onSelectMaterial,
                    )
                }

                StartProgressPageType.SECOND_PAGE -> {
                    Spacer(modifier = Modifier.padding(top = 50.dp))

                    ExplainTextFieldBox(
                        textInput = reasonInput,
                        onValueChange = onReasonValueChange,
                        hintText = ReasonWriteHint,
                        maxTextNum = 300,
                        isTextNumVisible = true,
                    )
                }
            }
        }

        RectangleBtn(
            btnContent = pageType.btnText,
            isBtnActivated = isBtnActivated,
            onClickAction = {
                when (pageType) {
                    StartProgressPageType.BEGIN_PAGE -> onClickNext(StartProgressPageType.FIRST_PAGE)
                    StartProgressPageType.FIRST_PAGE -> onClickNext(StartProgressPageType.SECOND_PAGE)
                    StartProgressPageType.SECOND_PAGE -> onClickStartProgress()
                }
            },
        )

        Spacer(modifier = Modifier.padding(bottom = 60.dp))
    }
}

@Composable
private fun WriteNickname() {
    //
}

@Composable
private fun SelectMaterial(
    selectedMaterial: MaterialType,
    onSelectMaterial: (MaterialType) -> Unit,
) {
    Spacer(modifier = Modifier.padding(top = 48.dp))

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
    ) {
        MaterialType.entries
            .dropLast(1)
            .chunked(3)
            .forEachIndexed { index, rowItems ->
                Row(
                    modifier =
                        Modifier
                            .padding(horizontal = 30.dp)
                            .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    rowItems.forEach { material ->
                        Box(
                            modifier =
                                Modifier
                                    .weight(1f),
                        ) {
                            SelectCircleListItem(
                                itemText = material.content,
                                isSelected = (material == selectedMaterial),
                                onSelect = { onSelectMaterial(material) },
                            )
                        }
                    }

                    if (rowItems.size < 3) {
                        repeat(3 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(top = 20.dp))
            }
    }
}
