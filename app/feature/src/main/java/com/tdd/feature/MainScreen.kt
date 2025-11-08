package com.tdd.feature

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.tdd.design_system.White4
import com.tdd.domain.entity.request.CreateUserModel
import com.tdd.domain.entity.response.interview.InterviewChapterItem
import com.tdd.domain.entity.response.progress.ProgressBookInfoModel
import com.tdd.feature.component.BottomNavBar
import com.tdd.navigation.NavRoutes
import com.tdd.navigation.interviewChapterNavGraph
import com.tdd.navigation.interviewNavGraph
import com.tdd.navigation.onBoardingNavGraph
import com.tdd.navigation.progressNavGraph
import com.tdd.ui.common.bottomsheet.ChapterBottomSheet
import com.tdd.ui.common.bottomsheet.CreateBookInfoBottomSheet
import com.tdd.ui.common.dialog.InterviewTypeDialog
import com.tdd.ui.common.type.BottomSheetType
import com.tdd.ui.util.DismissKeyboardOnClick
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {

    val viewModel: MainViewModel = hiltViewModel()
    val uiState: MainPageState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val isShowDialog = remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    val settingUserModel: (CreateUserModel) -> Unit = {
        scope.launch {
            viewModel.userModel.emit(it)
        }
    }
    val showDialog: () -> Unit = {
        isShowDialog.value = true
    }
    val showChapterBottomSheet: (Int, InterviewChapterItem) -> Unit = { id, item ->
        viewModel.setChapterBottomSheet(id, item)
        scope.launch { sheetState.show() }
    }
    val showCreateBookBottomSheet: (ProgressBookInfoModel) -> Unit = {
        viewModel.setCreateBookInfoBottomSheet(it)
        scope.launch { sheetState.show() }
    }

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow
            .distinctUntilChanged()
            .collect { backStackEntry ->
                viewModel.setBottomNavType(backStackEntry.destination.route)
            }
    }

    DismissKeyboardOnClick {
        if (isShowDialog.value) {
            InterviewTypeDialog(
                onSelectType = { type ->
                    viewModel.setInterviewType(type)
                    isShowDialog.value = false
                    navController.navigate(NavRoutes.InterviewScreen.route)
                },
                onDismiss = { isShowDialog.value = false },
            )
        }

        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetContent = {
                AnimatedContent(
                    targetState = uiState.bottomSheetType,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(500)) togetherWith fadeOut(
                            animationSpec = tween(
                                500
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(650.dp)
                        .navigationBarsPadding(),
                    label = ""
                ) { currentSheet ->
                    when (currentSheet) {
                        BottomSheetType.CHAPTER -> {
                            ChapterBottomSheet(
                                selectedChapter = uiState.selectedChapter,
                                currentId = uiState.currentChapterId,
                                onClickClose = {
                                    scope.launch {
                                        sheetState.hide()
                                    }
                                }
                            )
                        }

                        BottomSheetType.CREATEBOOK -> {
                            CreateBookInfoBottomSheet(
                                onClickClose = {
                                    scope.launch {
                                        sheetState.hide()
                                    }
                                },
                                bookInfo = uiState.createBookInfo,
                                onClickCreateBtn = {
                                    scope.launch {
                                        sheetState.hide()
                                        viewModel.isBookCreateEnabled.emit(true)
                                    }
                                    navController.navigate(NavRoutes.ProgressScreen.route)
                                }
                            )
                        }

                        BottomSheetType.DEFAULT -> {}
                    }
                }
            },
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            scrimColor = Color(0, 0, 0, 128)
        ) {
            Scaffold(
                bottomBar = {
                    AnimatedVisibility(
                        visible = uiState.bottomNavType != BottomNavType.DEFAULT,
                        modifier = Modifier.background(White4),
                        enter = fadeIn() + slideIn { IntOffset(0, 0) },
                        exit = fadeOut() + slideOut { IntOffset(0, 0) }
                    ) {
                        BottomNavBar(
                            interactionSource = interactionSource,
                            modifier = Modifier.navigationBarsPadding(),
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
                            }
                        )
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = NavRoutes.InterviewChapterGraph.route,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    onBoardingNavGraph(
                        navController = navController,
                        setUserModel = settingUserModel,
                        userModel = viewModel.userModel
                    )
                    interviewNavGraph(
                        navController = navController,
                        showDialog = showDialog
                    )
                    interviewChapterNavGraph(
                        navController = navController,
                        showChapterBottomSheet = showChapterBottomSheet
                    )
                    progressNavGraph(
                        navController = navController,
                        showCreateBookBottomSheet = showCreateBookBottomSheet,
                        isBookCreatedEnabled = viewModel.isBookCreateEnabled
                    )
                }
            }
        }
    }
}