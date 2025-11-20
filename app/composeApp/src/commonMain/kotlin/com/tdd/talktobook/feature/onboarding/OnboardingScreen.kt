package com.tdd.talktobook.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tdd.talktobook.core.designsystem.BackGround2
import com.tdd.talktobook.core.designsystem.Next
import com.tdd.talktobook.core.designsystem.OccupationWriteHint
import com.tdd.talktobook.core.ui.common.button.RectangleBtn
import com.tdd.talktobook.core.ui.common.content.SeriesNumText
import com.tdd.talktobook.core.ui.common.content.SeriesTitleText
import com.tdd.talktobook.core.ui.common.item.SelectRectangleItem
import com.tdd.talktobook.core.ui.common.textfield.TextFieldBox
import com.tdd.talktobook.feature.onboarding.type.AgeGroupType
import com.tdd.talktobook.feature.onboarding.type.GenderType
import com.tdd.talktobook.feature.onboarding.type.OnboardingPageType
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun OnboardingScreen() {

    val viewModel: OnboardingViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    OnboardingContent(
        pageType = uiState.pageType,
        onClickNext = { viewModel.setPageType(it) },
        onClickEditMember = {},
        selectedGender = uiState.gender,
        onSelectGender = { viewModel.setSelectedGender(it) },
        selectedAgeGroup = uiState.ageGroup,
        onSelectAgeGroup = { viewModel.setSelectedAgeGroup(it) },
        occupationInput = uiState.occupationInput,
        onOccupationValueChange = { viewModel.onOccupationValueChange(it) },
        isBtnActivated = uiState.isBtnActivated
    )
}

@Composable
private fun OnboardingContent(
    pageType: OnboardingPageType = OnboardingPageType.FIRST_PAGE,
    onClickNext: (OnboardingPageType) -> Unit,
    onClickEditMember: () -> Unit,
    selectedGender: GenderType,
    onSelectGender: (GenderType) -> Unit,
    selectedAgeGroup: AgeGroupType,
    onSelectAgeGroup: (AgeGroupType) -> Unit,
    occupationInput: String,
    onOccupationValueChange: (String) -> Unit,
    isBtnActivated: Boolean = false,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(BackGround2),
    ) {
        Spacer(modifier = Modifier.padding(top = 80.dp))

        SeriesNumText(
            totalNum = 3,
            currentNum = pageType.page
        )

        SeriesTitleText(
            currentTitle = pageType.title,
            paddingTop = 10
        )

        Spacer(modifier = Modifier.padding(top = 100.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            when (pageType) {
                OnboardingPageType.FIRST_PAGE -> {
                    OnboardingAgeGroup(selectedAgeGroup = selectedAgeGroup, onSelectAgeGroup = onSelectAgeGroup)
                }

                OnboardingPageType.SECOND_PAGE -> {
                    OnboardingGender(selectedGender = selectedGender, onSelectGender = onSelectGender)
                }

                OnboardingPageType.THIRD_PAGE -> {
                    TextFieldBox(textInput = occupationInput, onValueChange = onOccupationValueChange, hintText = OccupationWriteHint)
                }
            }
        }

        RectangleBtn(
            btnContent = Next,
            isBtnActivated = isBtnActivated,
            onClickAction = {
                when (pageType) {
                    OnboardingPageType.FIRST_PAGE -> onClickNext(OnboardingPageType.SECOND_PAGE)
                    OnboardingPageType.SECOND_PAGE -> onClickNext(OnboardingPageType.THIRD_PAGE)
                    OnboardingPageType.THIRD_PAGE -> onClickEditMember()
                }
            },
        )

        Spacer(modifier = Modifier.padding(bottom = 80.dp))
    }
}

@Composable
private fun OnboardingAgeGroup(
    selectedAgeGroup: AgeGroupType,
    onSelectAgeGroup: (AgeGroupType) -> Unit,
) {
    AgeGroupType.entries
        .dropLast(1)
        .chunked(2)
        .forEachIndexed { index, rowItems ->
            Row(
                modifier = Modifier
                    .padding(horizontal = 25.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowItems.forEach { age ->
                    SelectRectangleItem(
                        itemText = age.content,
                        modifier = Modifier.weight(1f),
                        onSelectItem = { onSelectAgeGroup(age) },
                        isItemSelected = (age == selectedAgeGroup)
                    )
                }
            }

            Spacer(modifier = Modifier.padding(top = 16.dp))
        }
}

@Composable
private fun OnboardingGender(
    selectedGender: GenderType,
    onSelectGender: (GenderType) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 25.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GenderType.entries.dropLast(1) .forEach { gender ->
            SelectRectangleItem(
                itemText = gender.content,
                modifier = Modifier.weight(1f),
                onSelectItem = { onSelectGender(gender) },
                isItemSelected = (gender == selectedGender)
            )
        }
    }
}