package com.tdd.bookshelf.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.tdd.bookshelf.feature.detailchapter.DetailChapterScreen
import com.tdd.bookshelf.feature.home.HomeScreen
import com.tdd.bookshelf.feature.interview.InterviewScreen
import com.tdd.bookshelf.feature.login.LogInScreen
import com.tdd.bookshelf.feature.my.MyScreen
import com.tdd.bookshelf.feature.onboarding.OnboardingScreen
import com.tdd.bookshelf.feature.publication.PublicationScreen
import com.tdd.bookshelf.feature.signup.SignUpScreen

fun NavGraphBuilder.loginNavGraph(
    navController: NavController,
) {
    navigation(
        startDestination = NavRoutes.LogInScreen.route,
        route = NavRoutes.LogInGraph.route,
    ) {
        composable(NavRoutes.LogInScreen.route) {
            LogInScreen(
                goToOnboardingPage = { navController.navigate(NavRoutes.HomeScreen.route) },
                goToSignUp = { navController.navigate(NavRoutes.SignUpScreen.route) },
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
            OnboardingScreen()
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
                goToInterviewPage = { interviewId -> navController.navigate(NavRoutes.InterviewScreen.setRouteModel(interviewId)) },
                goToDetailChapterPage = { autobiographyId -> navController.navigate(NavRoutes.DetailChapterScreen.setRouteModel(autobiographyId)) },
            )
        }
    }
}

fun NavGraphBuilder.interviewNavGraph(
    navController: NavController,
) {
    navigation(
        startDestination = NavRoutes.InterviewScreen.route,
        route = NavRoutes.InterviewGraph.route,
    ) {
        composable(
            route = NavRoutes.InterviewScreen.route,
            arguments = listOf(navArgument("interviewId") { type = NavType.IntType }),
        ) {
            val interviewId = it.arguments?.getInt("interviewId") ?: 0

            InterviewScreen(
                interviewId = interviewId,
                goBackPage = { navController.popBackStack() },
            )
        }
    }
}

fun NavGraphBuilder.detailChapterNavGraph(
    navController: NavController,
) {
    navigation(
        startDestination = NavRoutes.DetailChapterScreen.route,
        route = NavRoutes.DetailChapterGraph.route,
    ) {
        composable(
            route = NavRoutes.DetailChapterScreen.route,
            arguments = listOf(navArgument("autobiographyId") { type = NavType.IntType }),
        ) {
            val autobiographyId = it.arguments?.getInt("autobiographyId") ?: 0

            DetailChapterScreen(
                autobiographyId = autobiographyId,
                goBackPage = { navController.popBackStack() },
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

fun NavGraphBuilder.myNavGraph(
    navController: NavController,
) {
    navigation(
        startDestination = NavRoutes.MyPageScreen.route,
        route = NavRoutes.MyPageGraph.route,
    ) {
        composable(NavRoutes.MyPageScreen.route) {
            MyScreen()
        }
    }
}
