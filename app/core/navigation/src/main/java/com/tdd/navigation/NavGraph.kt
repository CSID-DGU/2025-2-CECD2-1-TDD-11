package com.tdd.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.tdd.domain.entity.request.CreateUserModel
import com.tdd.domain.entity.response.interview.InterviewChapterItem
import com.tdd.domain.entity.response.progress.ProgressBookInfoModel
import com.tdd.interview.main.InterviewMainScreen
import com.tdd.interview.start.InterviewScreen
import com.tdd.interview.start.ready.InterviewStartScreen
import com.tdd.interviewchapter.InterviewChapterContent
import com.tdd.interviewchapter.InterviewChapterScreen
import com.tdd.onboarding.OnBoardingScreen
import com.tdd.onboarding.education.ScholarShipScreen
import com.tdd.onboarding.gender.UserGenderScreen
import com.tdd.onboarding.marriage.MarriageScreen
import com.tdd.onboarding.userage.UserAgeScreen
import com.tdd.progress.ProgressScreen
import com.tdd.progress.resultbook.BookResultScreen
import kotlinx.coroutines.flow.SharedFlow

fun NavGraphBuilder.onBoardingNavGraph(
    navController: NavController,
    setUserModel: (CreateUserModel) -> Unit,
    userModel: SharedFlow<CreateUserModel>,
) {
    navigation(
        startDestination = NavRoutes.UserAgeScreen.route,
        route = NavRoutes.OnBoardingGraph.route
    ) {
        composable(NavRoutes.UserAgeScreen.route) {
            UserAgeScreen(
                goToUserGenderPage = {
                    setUserModel(it)
                    navController.navigate(NavRoutes.UserGenderScreen.route)
                }
            )
        }

        composable(NavRoutes.UserGenderScreen.route) {
            UserGenderScreen(
                userModel = userModel,
                goToEducationPage = {
                    setUserModel(it)
                    navController.navigate(NavRoutes.ScholarShipScreen.route)
                }
            )
        }

        composable(NavRoutes.ScholarShipScreen.route) {
            ScholarShipScreen(
                userModel = userModel,
                goToSelectMarryPage = {
                    setUserModel(it)
                    navController.navigate(NavRoutes.MarriageScreen.route)
                }
            )
        }

        composable(NavRoutes.MarriageScreen.route) {
            MarriageScreen(
                userModel = userModel,
                goToCreateChapterPage = {
                    setUserModel(it)
                    navController.navigate(NavRoutes.OnBoardingScreen.route)
                }
            )
        }

        composable(NavRoutes.OnBoardingScreen.route) {
            OnBoardingScreen(
                userModel = userModel,
                goToInterviewPage = {
                    navController.navigate(NavRoutes.StartInterViewScreen.route) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}

fun NavGraphBuilder.interviewNavGraph(
    navController: NavController,
    showDialog: () -> Unit
) {
    navigation(
        startDestination = NavRoutes.StartInterViewScreen.route,
        route = NavRoutes.InterViewGraph.route
    ) {
        composable(NavRoutes.StartInterViewScreen.route) {
            InterviewStartScreen(
                showInterviewDialog = showDialog,
                goHomePage = {
                    navController.navigate(NavRoutes.InterviewChapterScreen.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(NavRoutes.InterviewScreen.route) {
            InterviewScreen(
                goHomePage = {
                    navController.navigate(NavRoutes.InterviewChapterScreen.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(NavRoutes.InterviewMainScreen.route) {
            InterviewMainScreen()
        }
    }
}

fun NavGraphBuilder.interviewChapterNavGraph(
    navController: NavController,
    showChapterBottomSheet: (Int, InterviewChapterItem) -> Unit
) {
    navigation(
        startDestination = NavRoutes.InterviewChapterScreen.route,
        route = NavRoutes.InterviewChapterGraph.route
    ) {
        composable(NavRoutes.InterviewChapterScreen.route) {
            InterviewChapterScreen(
                showChapterBottomSheet = showChapterBottomSheet
            )
        }
    }
}

fun NavGraphBuilder.progressNavGraph(
    navController: NavController,
    showCreateBookBottomSheet: (ProgressBookInfoModel) -> Unit,
    isBookCreatedEnabled: SharedFlow<Boolean>
) {
    navigation(
        startDestination = NavRoutes.ProgressScreen.route,
        route = NavRoutes.ProgressGraph.route
    ) {
        composable(NavRoutes.ProgressScreen.route) {
            ProgressScreen(
                goToInterviewPage = {
                    navController.navigate(NavRoutes.InterviewMainScreen.route) {
                        popUpTo(0)
                    }
                },
                showCreateBookBottomSheet = showCreateBookBottomSheet,
                isBookCreatedEnabled = isBookCreatedEnabled,
                goToBookResultPage = {
                    navController.navigate(NavRoutes.BookResultScreen.route)
                }
            )
        }

        composable(NavRoutes.BookResultScreen.route) {
            BookResultScreen(
                goBack = {
                    navController.navigate(NavRoutes.ProgressScreen.route) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}