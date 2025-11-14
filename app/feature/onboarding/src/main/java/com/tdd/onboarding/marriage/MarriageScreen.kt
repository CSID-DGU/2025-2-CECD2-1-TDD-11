package com.tdd.onboarding.marriage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tdd.design_system.BackGround
import com.tdd.design_system.Next
import com.tdd.design_system.SelectedMarriageSemiTitle
import com.tdd.design_system.SelectedMarriageTitle
import com.tdd.domain.entity.request.CreateUserModel
import com.tdd.ui.common.button.BottomRectangleBtn
import com.tdd.ui.common.content.TopTitleContent
import com.tdd.ui.common.item.SelectItem
import com.tdd.ui.common.type.YesNoType
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun MarriageScreen(
    userModel: SharedFlow<CreateUserModel>,
    goToCreateChapterPage: (CreateUserModel) -> Unit,
) {
    val viewModel: MarriageViewModel = hiltViewModel()
    val uiState: MarriagePageState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userModel) {
        userModel.collect {
            viewModel.setUserModel(it)
        }
    }

    MarriageContent(
        selectedMarriage = uiState.marriage,
        onSelectMarriage = { viewModel.setSelectedMarriage(it) },
        onClickBtnAction = { goToCreateChapterPage(viewModel.updateUserModel()) }
    )
}

@Composable
fun MarriageContent(
    selectedMarriage: String = "",
    onSelectMarriage: (String) -> Unit = {},
    onClickBtnAction: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackGround)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopTitleContent(
                title = SelectedMarriageTitle,
                semiTitle = SelectedMarriageSemiTitle
            )

            Row(
                modifier = Modifier
                    .padding(horizontal = 35.dp)
                    .padding(top = 40.dp, bottom = 15.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                YesNoType.entries.forEach { answer ->
                    SelectItem(
                        itemText = answer.content,
                        modifier = Modifier.weight(1f),
                        onSelectItem = { onSelectMarriage(answer.api) },
                        isItemSelected = (answer.api == selectedMarriage)
                    )
                }
            }
        }

        BottomRectangleBtn(
            btnTextContent = Next,
            isBtnActivated = (selectedMarriage.isNotEmpty()),
            onClickAction = onClickBtnAction
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMarriage() {
    MarriageContent()
}