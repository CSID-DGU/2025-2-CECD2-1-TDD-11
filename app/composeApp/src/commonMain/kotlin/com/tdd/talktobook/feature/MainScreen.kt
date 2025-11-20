package com.tdd.talktobook.feature

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.tdd.talktobook.core.navigation.NavRoutes
import com.tdd.talktobook.core.navigation.detailChapterNavGraph
import com.tdd.talktobook.core.navigation.homeNavGraph
import com.tdd.talktobook.core.navigation.interviewNavGraph
import com.tdd.talktobook.core.navigation.loginNavGraph
import com.tdd.talktobook.core.navigation.myNavGraph
import com.tdd.talktobook.core.navigation.onboardingNavGraph
import com.tdd.talktobook.core.navigation.publicationNavGraph
import com.tdd.talktobook.core.navigation.signupNavGraph
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen() {
    val viewModel: MainViewModel = koinViewModel()
    val uiState: MainPageState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow
            .distinctUntilChanged()
            .collect { backStackEntry ->
                viewModel.setBottomNavType(backStackEntry.destination.route)
            }
    }

    Scaffold(
//        bottomBar = {
//            AnimatedVisibility(
//                visible = uiState.bottomNavType != BottomNavType.DEFAULT,
//                modifier = Modifier.background(White0),
//                enter = fadeIn() + slideIn { IntOffset(0, 0) },
//                exit = fadeOut() + slideOut { IntOffset(0, 0) },
//            ) {
//                BottomNavBar(
//                    modifier = Modifier.navigationBarsPadding(),
//                    interactionSource = interactionSource,
//                    type = uiState.bottomNavType,
//                    onClick = { route: String ->
//                        if (navController.currentDestination?.route != route) {
//                            navController.navigate(route) {
//                                popUpTo(navController.currentDestination?.route!!) {
//                                    inclusive = true
//                                }
//                                launchSingleTop = true
//                            }
//                        }
//                    },
//                )
//            }
//        },
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
                )
                signupNavGraph(
                    navController = navController,
                )
                onboardingNavGraph(
                    navController = navController,
                )
                homeNavGraph(
                    navController = navController,
                )
                interviewNavGraph(
                    navController = navController,
                )
                detailChapterNavGraph(
                    navController = navController,
                )
                publicationNavGraph(
                    navController = navController,
                )
                myNavGraph(
                    navController = navController,
                )
            }
        }
    }
}
