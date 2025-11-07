package com.tdd.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.tdd.design_system.BackGround
import com.tdd.design_system.CreateChapterSemiTitle
import com.tdd.design_system.CreateChapterTitle
import com.tdd.design_system.R
import com.tdd.domain.entity.request.CreateUserModel
import com.tdd.ui.common.content.TopTitleContent
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun OnBoardingScreen(
    userModel: SharedFlow<CreateUserModel>,
    goToInterviewPage: () -> Unit,
) {

    val viewModel: OnBoardingViewModel = hiltViewModel()
    val uiState: OnBoardingPageState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userModel) {
        userModel.collect {
            viewModel.setUserModel(it)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is OnBoardingEvent.GoToInterViewPage -> {
                    goToInterviewPage()
                }
            }
        }
    }

    OnBoardingContent()
}

@Composable
fun OnBoardingContent() {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.raw_airplane_loading))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackGround),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopTitleContent(
            title = CreateChapterTitle,
            semiTitle = CreateChapterSemiTitle
        )

        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .size(200.dp)
                .padding(top = 100.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewOnBoarding() {
    OnBoardingContent()
}