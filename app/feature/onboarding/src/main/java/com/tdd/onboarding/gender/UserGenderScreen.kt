package com.tdd.onboarding.gender

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
import com.tdd.design_system.UserGenderSemiTitle
import com.tdd.design_system.UserGenderTitle
import com.tdd.domain.entity.request.CreateUserModel
import com.tdd.onboarding.type.UserGenderType
import com.tdd.ui.common.button.BottomRectangleBtn
import com.tdd.ui.common.content.TopTitleContent
import com.tdd.ui.common.item.SelectItem
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun UserGenderScreen(
    userModel: SharedFlow<CreateUserModel>,
    goToEducationPage: (CreateUserModel) -> Unit,
) {

    val viewModel: UserGenderViewModel = hiltViewModel()
    val uiState: UserGenderPageState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userModel) {
        userModel.collect {
            viewModel.setUserModel(it)
        }
    }

    UserGenderContent(
        selectedGender = uiState.selectedGender,
        onSelectGender = { viewModel.setSelectedGender(it) },
        onClickBtnAction = { goToEducationPage(viewModel.updateUserModel()) }
    )
}

@Composable
fun UserGenderContent(
    selectedGender: String = "",
    onSelectGender: (String) -> Unit = {},
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
                title = UserGenderTitle,
                semiTitle = UserGenderSemiTitle
            )

            Row(
                modifier = Modifier
                    .padding(horizontal = 35.dp)
                    .padding(top = 40.dp, bottom = 15.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                UserGenderType.entries.forEach { gender ->
                    SelectItem(
                        itemText = gender.content,
                        modifier = Modifier.weight(1f),
                        onSelectItem = { onSelectGender(gender.api) },
                        isItemSelected = (gender.api == selectedGender)
                    )
                }
            }
        }

        BottomRectangleBtn(
            btnTextContent = Next,
            isBtnActivated = (selectedGender.isNotEmpty()),
            onClickAction = onClickBtnAction
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewUserGender() {
    UserGenderContent()
}