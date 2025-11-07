package com.tdd.onboarding.education

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
import com.tdd.design_system.ScholarShipSemiTitle
import com.tdd.design_system.ScholarShipTitle
import com.tdd.domain.entity.request.CreateUserModel
import com.tdd.onboarding.type.ScholarShipType
import com.tdd.ui.common.button.BottomRectangleBtn
import com.tdd.ui.common.content.TopTitleContent
import com.tdd.ui.common.item.SelectItem
import kotlinx.coroutines.flow.SharedFlow


@Composable
fun ScholarShipScreen(
    userModel: SharedFlow<CreateUserModel>,
    goToSelectMarryPage: (CreateUserModel) -> Unit,
) {

    val viewModel: ScholarShipViewModel = hiltViewModel()
    val uiState: ScholarShipPageState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userModel) {
        userModel.collect {
            viewModel.setUserModel(it)
        }
    }

    ScholarShipContent(
        selectedScholarShip = uiState.scholarShip,
        onSelectScholarShip = { viewModel.setSelectedScholarShip(it) },
        onClickBtnAction = { goToSelectMarryPage(viewModel.updateUserModel()) }
    )
}

@Composable
fun ScholarShipContent(
    selectedScholarShip: String = "",
    onSelectScholarShip: (String) -> Unit = {},
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
                title = ScholarShipTitle,
                semiTitle = ScholarShipSemiTitle
            )

            Row(
                modifier = Modifier
                    .padding(horizontal = 35.dp)
                    .padding(top = 40.dp, bottom = 15.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ScholarShipType.entries.take(2).forEach { scholarShip ->
                    SelectItem(
                        itemText = scholarShip.content,
                        modifier = Modifier.weight(1f),
                        onSelectItem = { onSelectScholarShip(scholarShip.api) },
                        isItemSelected = (scholarShip.api == selectedScholarShip)
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
                ScholarShipType.entries.drop(2).take(2).forEach { scholarShip ->
                    SelectItem(
                        itemText = scholarShip.content,
                        modifier = Modifier.weight(1f),
                        onSelectItem = { onSelectScholarShip(scholarShip.api) },
                        isItemSelected = (scholarShip.api == selectedScholarShip)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 35.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ScholarShipType.entries.drop(4).take(2).forEach { scholarShip ->
                    SelectItem(
                        itemText = scholarShip.content,
                        modifier = Modifier.weight(1f),
                        onSelectItem = { onSelectScholarShip(scholarShip.api) },
                        isItemSelected = (scholarShip.api == selectedScholarShip)
                    )
                }
            }
        }

        BottomRectangleBtn(
            btnTextContent = Next,
            isBtnActivated = (selectedScholarShip.isNotEmpty()),
            onClickAction = onClickBtnAction
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewScholarShip() {
    ScholarShipContent()
}