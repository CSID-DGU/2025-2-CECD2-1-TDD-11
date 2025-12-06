package com.tdd.talktobook.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.tdd.talktobook.core.ui.common.type.FlowType
import com.tdd.talktobook.domain.entity.request.page.OneBtnDialogModel
import com.tdd.talktobook.domain.entity.request.page.TwoBtnDialogModel
import com.tdd.talktobook.feature.auth.emailcheck.EmailCheckScreen
import com.tdd.talktobook.feature.auth.login.LogInScreen
import com.tdd.talktobook.feature.auth.signup.SignUpScreen
import com.tdd.talktobook.feature.autobiographyrequest.AutobiographyRequestScreen
import com.tdd.talktobook.feature.home.HomeScreen
import com.tdd.talktobook.feature.home.interview.PastInterviewScreen
import com.tdd.talktobook.feature.interview.InterviewScreen
import com.tdd.talktobook.feature.onboarding.OnboardingScreen
import com.tdd.talktobook.feature.publication.PublicationScreen
import com.tdd.talktobook.feature.setting.SettingScreen
import com.tdd.talktobook.feature.startprogress.StartProgressScreen
import kotlinx.coroutines.flow.StateFlow

fun NavGraphBuilder.loginNavGraph(
    navController: NavController,
    setScreenFlow: (FlowType) -> Unit,
) {
    navigation(
        startDestination = NavRoutes.LogInScreen.route,
        route = NavRoutes.LogInGraph.route,
    ) {
        composable(NavRoutes.LogInScreen.route) {
            LogInScreen(
                goToSignUp = { navController.navigate(NavRoutes.SignUpScreen.route) },
                goToHome = { navController.navigate(NavRoutes.HomeScreen.route) { popUpTo(0) } },
                goToOnboarding = { navController.navigate(NavRoutes.OnboardingScreen.route) },
                goToStartProgress = {
                    setScreenFlow(FlowType.COSHOW)
                    navController.navigate(NavRoutes.StartProgressScreen.route)
                },
            )
        }
    }
}

fun NavGraphBuilder.signupNavGraph(
    navController: NavController,
) {
    navigation(
        startDestination = NavRoutes.SignUpScreen.route,
        route = NavRoutes.SignUpGraph.route,
    ) {
        composable(NavRoutes.SignUpScreen.route) {
            SignUpScreen(
                goToEmailCheckPage = { email -> navController.navigate(NavRoutes.EmailCheckScreen.setRouteModel(email)) },
                goToPasswordChangePage = {},
            )
        }
    }
}

fun NavGraphBuilder.emailCheckNavGraph(
    navController: NavController,
) {
    navigation(
        startDestination = NavRoutes.EmailCheckScreen.route,
        route = NavRoutes.EmailCheckGraph.route,
    ) {
        composable(
            NavRoutes.EmailCheckScreen.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType }),
        ) {
            val email = it.arguments?.getString("email") ?: ""

            EmailCheckScreen(
                email = email,
                goToLogInPage = { navController.navigate(NavRoutes.LogInScreen.route) },
            )
        }
    }
}

fun NavGraphBuilder.onboardingNavGraph(
    navController: NavController,
) {
    navigation(
        startDestination = NavRoutes.OnboardingScreen.route,
        route = NavRoutes.OnboardingGraph.route,
    ) {
        composable(NavRoutes.OnboardingScreen.route) {
            OnboardingScreen(
                goToHome = { navController.navigate(NavRoutes.HomeScreen.route) { popUpTo(0) } },
            )
        }
    }
}

fun NavGraphBuilder.homeNavGraph(
    navController: NavController,
) {
    navigation(
        startDestination = NavRoutes.HomeScreen.route,
        route = NavRoutes.HomeGraph.route,
    ) {
        composable(NavRoutes.HomeScreen.route) {
            HomeScreen(
                goToPastInterviewPage = { date, id -> navController.navigate(NavRoutes.PastInterviewScreen.setRouteModel(date, id)) },
                goToProgressStartPage = { navController.navigate(NavRoutes.StartProgressScreen.route) },
                goToSettingPage = { navController.navigate(NavRoutes.SettingPageScreen.route) },
            )
        }
    }
}

fun NavGraphBuilder.pastInterviewNavGraph(
    navController: NavController,
) {
    navigation(
        startDestination = NavRoutes.PastInterviewScreen.route,
        route = NavRoutes.PastInterviewGraph.route,
    ) {
        composable(
            route = NavRoutes.PastInterviewScreen.route,
            arguments =
                listOf(
                    navArgument("date") { type = NavType.StringType },
                    navArgument("interviewId") { type = NavType.IntType },
                ),
        ) {
            val date = it.arguments?.getString("date") ?: ""
            val interviewId = it.arguments?.getInt("interviewId") ?: 0

            PastInterviewScreen(
                goBackToHome = { navController.popBackStack() },
                selectedDate = date,
                interviewId = interviewId,
            )
        }
    }
}

fun NavGraphBuilder.interviewNavGraph(
    navController: NavController,
    showOneBtnDialogModel: (OneBtnDialogModel) -> Unit,
    userNickName: StateFlow<String>,
    showTwoBtnDialogModel: (TwoBtnDialogModel) -> Unit,
    flowType: StateFlow<FlowType>,
) {
    navigation(
        startDestination = NavRoutes.InterviewScreen.route,
        route = NavRoutes.InterviewGraph.route,
    ) {
        composable(
            route = NavRoutes.InterviewScreen.route,
            arguments =
                listOf(
                    navArgument("question") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = ""
                    },
                ),
        ) {
            val question = it.arguments?.getString("question") ?: ""

            InterviewScreen(
                showStartAutobiographyDialog = showOneBtnDialogModel,
                startQuestion = question,
                showCreateAutobiographyDialog = showOneBtnDialogModel,
                nickName = userNickName,
                navController = navController,
                showSkipQuestionDialog = showTwoBtnDialogModel,
                flowType = flowType,
                goToSuccessPage = { id -> navController.navigate(NavRoutes.AutobiographyRequestScreen.setRouteModel(id)) { popUpTo(0) } },
            )
        }
    }
}

fun NavGraphBuilder.startProgressNavGraph(
    navController: NavController,
    setUserNickName: (String) -> Unit,
    flowType: StateFlow<FlowType>,
) {
    navigation(
        startDestination = NavRoutes.StartProgressScreen.route,
        route = NavRoutes.StartProgressGraph.route,
    ) {
        composable(route = NavRoutes.StartProgressScreen.route) {
            StartProgressScreen(
                goToInterviewPage = { navController.navigate(NavRoutes.InterviewScreen.setRouteModel(it)) { popUpTo(0) } },
                goToCoShowInterviewPage = { navController.navigate(NavRoutes.InterviewScreen.setRouteModel("")) },
                goBackToHome = { navController.popBackStack() },
                setUserNickName = setUserNickName,
                flowType = flowType,
            )
        }
    }
}

fun NavGraphBuilder.autobiographyRequestNavGraph(
    navController: NavController,
) {
    navigation(
        startDestination = NavRoutes.AutobiographyRequestScreen.route,
        route = NavRoutes.AutobiographyRequestGraph.route,
    ) {
        composable(
            route = NavRoutes.AutobiographyRequestScreen.route,
            arguments = listOf(navArgument("autobiographyId") { type = NavType.IntType }),
        ) {
            val autobiographyId = it.arguments?.getInt("autobiographyId") ?: 0

            AutobiographyRequestScreen(
                goToLogIn = { navController.navigate(NavRoutes.LogInScreen.route) { popUpTo(0) } },
                autobiographyId = autobiographyId,
            )
        }
    }
}

fun NavGraphBuilder.publicationNavGraph(
    navController: NavController,
) {
    navigation(
        startDestination = NavRoutes.PublicationScreen.route,
        route = NavRoutes.PublicationGraph.route,
    ) {
        composable(NavRoutes.PublicationScreen.route) {
            PublicationScreen()
        }
    }
}

fun NavGraphBuilder.settingNavGraph(
    navController: NavController,
) {
    navigation(
        startDestination = NavRoutes.SettingPageScreen.route,
        route = NavRoutes.SettingPageGraph.route,
    ) {
        composable(NavRoutes.SettingPageScreen.route) {
            SettingScreen(
                goBackPage = { navController.popBackStack() },
                goToLogInPage = { navController.navigate(NavRoutes.LogInScreen.route) },
            )
        }
    }
}
