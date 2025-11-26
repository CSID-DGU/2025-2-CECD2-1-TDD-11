package com.tdd.talktobook.feature

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.tdd.talktobook.core.designsystem.White0
import com.tdd.talktobook.core.navigation.NavRoutes
import com.tdd.talktobook.core.navigation.emailCheckNavGraph
import com.tdd.talktobook.core.navigation.homeNavGraph
import com.tdd.talktobook.core.navigation.interviewNavGraph
import com.tdd.talktobook.core.navigation.loginNavGraph
import com.tdd.talktobook.core.navigation.onboardingNavGraph
import com.tdd.talktobook.core.navigation.pastInterviewNavGraph
import com.tdd.talktobook.core.navigation.publicationNavGraph
import com.tdd.talktobook.core.navigation.settingNavGraph
import com.tdd.talktobook.core.navigation.signupNavGraph
import com.tdd.talktobook.core.navigation.startProgressNavGraph
import com.tdd.talktobook.core.ui.common.dialog.OneBtnDialog
import com.tdd.talktobook.core.ui.common.dialog.TwoBtnDialog
import com.tdd.talktobook.core.ui.common.type.FlowType
import com.tdd.talktobook.domain.entity.request.page.OneBtnDialogModel
import com.tdd.talktobook.domain.entity.request.page.TwoBtnDialogModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen() {
    val viewModel: MainViewModel = koinViewModel()
    val uiState: MainPageState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val interactionSource = remember { MutableInteractionSource() }

    val isShowDialog = remember { mutableStateOf(false) }
    val isShowTwoBtnDialog = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val showOneBtnDialog: (OneBtnDialogModel) -> Unit = {
        viewModel.onSetOneBtnDialog(it)
        isShowDialog.value = true
    }
    val showTwoBtnDialog: (TwoBtnDialogModel) -> Unit = {
        viewModel.onSetTwoBtnDialog(it)
        isShowTwoBtnDialog.value = true
    }
    val settingFlowType: (FlowType) -> Unit = {
        scope.launch {
            viewModel.screenFlowType.value = it
        }
    }
    val settingUserNickName: (String) -> Unit = {
        scope.launch {
            viewModel.userNickName.value = it
        }
    }

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow
            .distinctUntilChanged()
            .collect { backStackEntry ->
                viewModel.setBottomNavType(backStackEntry.destination.route)
            }
    }

    if (isShowDialog.value) {
        OneBtnDialog(
            title = uiState.oneBtnDialogModel.title,
            semiTitle = uiState.oneBtnDialogModel.semiTitle,
            btnText = uiState.oneBtnDialogModel.btnText,
            isBottomTextVisible = uiState.oneBtnDialogModel.isBottomTextVisible,
            bottomText = uiState.oneBtnDialogModel.bottomText,
            onClickBtn = {
                isShowDialog.value = false
                uiState.oneBtnDialogModel.onClickBtn()
            },
            onClickBottomText = {
                isShowDialog.value = false
                uiState.oneBtnDialogModel.onClickBottomText()
            },
            onDismiss = { isShowDialog.value = false },
        )
    }
    if (isShowTwoBtnDialog.value) {
        TwoBtnDialog(
            title = uiState.twoBtnDialogModel.title,
            semiTitle = uiState.twoBtnDialogModel.semiTitle,
            firstBtnText = uiState.twoBtnDialogModel.firstBtnText,
            onClickFirstBtn = {
                isShowTwoBtnDialog.value = false
                uiState.twoBtnDialogModel.onClickBtnFirst()
            },
            secondBtnText = uiState.twoBtnDialogModel.secondBtnText,
            onClickSecondBtn = {
                isShowTwoBtnDialog.value = false
                uiState.twoBtnDialogModel.onClickBtnSecond()
            },
            bottomText = uiState.twoBtnDialogModel.bottomBtnText,
            onClickBottomText = {
                isShowTwoBtnDialog.value = false
                uiState.twoBtnDialogModel.onClickBottomText()
            },
            isBottomTextVisible = true,
            onDismiss = { isShowTwoBtnDialog.value = false }
        )
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = uiState.bottomNavType != BottomNavType.DEFAULT,
                modifier = Modifier.background(White0),
                enter = fadeIn() + slideIn { IntOffset(0, 0) },
                exit = fadeOut() + slideOut { IntOffset(0, 0) },
            ) {
                BottomNavBar(
                    modifier = Modifier.navigationBarsPadding(),
                    interactionSource = interactionSource,
                    type = uiState.bottomNavType,
                    onClick = { route: String ->
                        if (navController.currentDestination?.route != route) {
                            navController.navigate(route) {
                                popUpTo(navController.currentDestination?.route!!) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    },
                )
            }
        },
        snackbarHost = {},
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            NavHost(
                navController = navController,
                startDestination = NavRoutes.LogInGraph.route,
            ) {
                loginNavGraph(
                    navController = navController,
                    setScreenFlow = settingFlowType
                )
                signupNavGraph(
                    navController = navController,
                )
                emailCheckNavGraph(
                    navController = navController,
                )
                onboardingNavGraph(
                    navController = navController,
                )
                homeNavGraph(
                    navController = navController,
                )
                pastInterviewNavGraph(
                    navController = navController,
                )
                interviewNavGraph(
                    navController = navController,
                    showOneBtnDialogModel = showOneBtnDialog,
                    userNickName = viewModel.userNickName,
                    showTwoBtnDialogModel = showTwoBtnDialog,
                    flowType = viewModel.screenFlowType
                )
                startProgressNavGraph(
                    navController = navController,
                    setUserNickName = settingUserNickName,
                    flowType = viewModel.screenFlowType
                )
                publicationNavGraph(
                    navController = navController,
                )
                settingNavGraph(
                    navController = navController,
                )
            }
        }
    }
}
