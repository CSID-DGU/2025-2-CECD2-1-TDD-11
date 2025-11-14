package com.tdd.bookshelf.feature

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.tdd.bookshelf.core.designsystem.White0
import com.tdd.bookshelf.core.navigation.NavRoutes
import com.tdd.bookshelf.core.navigation.detailChapterNavGraph
import com.tdd.bookshelf.core.navigation.homeNavGraph
import com.tdd.bookshelf.core.navigation.interviewNavGraph
import com.tdd.bookshelf.core.navigation.loginNavGraph
import com.tdd.bookshelf.core.navigation.myNavGraph
import com.tdd.bookshelf.core.navigation.onboardingNavGraph
import com.tdd.bookshelf.core.navigation.publicationNavGraph
import com.tdd.bookshelf.core.navigation.signupNavGraph
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
