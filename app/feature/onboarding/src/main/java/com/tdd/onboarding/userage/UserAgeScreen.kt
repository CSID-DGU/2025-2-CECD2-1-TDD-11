package com.tdd.onboarding.userage

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tdd.design_system.BackGround
import com.tdd.design_system.Next
import com.tdd.design_system.UserAgeSemiTitle
import com.tdd.design_system.UserAgeTitle
import com.tdd.domain.entity.request.CreateUserModel
import com.tdd.onboarding.type.UserAgeType
import com.tdd.ui.common.button.BottomRectangleBtn
import com.tdd.ui.common.content.TopTitleContent
import com.tdd.ui.common.item.SelectItem

@Composable
fun UserAgeScreen(
    goToUserGenderPage: (CreateUserModel) -> Unit,
) {

    val viewModel: UserAgeViewModel = hiltViewModel()
    val uiState: UserAgePageState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.postLogIn(context)
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UserAgeEvent.GoToMainPage -> {
                    //
                }
            }
        }
    }

    UserAgeContent(
        onClickBtnAction = { goToUserGenderPage(viewModel.updateUserModel()) },
        selectedAgeType = uiState.selectedUserAge,
        onSelectAgeType = { viewModel.setSelectedAge(it) }
    )
}

@Composable
fun UserAgeContent(
    onClickBtnAction: () -> Unit = {},
    selectedAgeType: String = "",
    onSelectAgeType: (String) -> Unit = {},
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
                title = UserAgeTitle,
                semiTitle = UserAgeSemiTitle
            )

            Row(
                modifier = Modifier
                    .padding(horizontal = 35.dp)
                    .padding(top = 40.dp, bottom = 15.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                UserAgeType.entries.take(2).forEach { age ->
                    SelectItem(
                        itemText = age.content,
                        modifier = Modifier.weight(1f),
                        onSelectItem = { onSelectAgeType(age.api) },
                        isItemSelected = (age.api == selectedAgeType)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 35.dp)
                    .padding(bottom = 15.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                UserAgeType.entries.drop(2).take(2).forEach { age ->
                    SelectItem(
                        itemText = age.content,
                        modifier = Modifier.weight(1f),
                        onSelectItem = { onSelectAgeType(age.api) },
                        isItemSelected = (age.api == selectedAgeType)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 35.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                UserAgeType.entries.drop(4).take(2).forEach { age ->
                    SelectItem(
                        itemText = age.content,
                        modifier = Modifier.weight(1f),
                        onSelectItem = { onSelectAgeType(age.api) },
                        isItemSelected = (age.api == selectedAgeType)
                    )
                }
            }
        }

        BottomRectangleBtn(
            btnTextContent = Next,
            isBtnActivated = (selectedAgeType.isNotEmpty()),
            onClickAction = onClickBtnAction
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewUserAge() {
    UserAgeContent()
}