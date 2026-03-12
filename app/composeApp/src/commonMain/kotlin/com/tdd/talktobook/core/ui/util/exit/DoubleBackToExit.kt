package com.tdd.talktobook.core.ui.util.exit

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.tdd.talktobook.core.designsystem.DoubleBackToExitNotice
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DoubleBackToExit(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    timeoutMs: Long = 2000L,
    message: String = DoubleBackToExitNotice,
) {
    val exitApp = ExitApp()
    var waitingSecondBack by remember { mutableStateOf(false) }
    var resetJob by remember { mutableStateOf<Job?>(null) }
    val scope = rememberCoroutineScope()

    PlatformBackHandler(true) {
        val popped = navController.popBackStack()

        if (popped) {
            waitingSecondBack = false
            resetJob?.cancel()
            return@PlatformBackHandler
        }

        // 루트일 때만 double back
        if (waitingSecondBack) {
            exitApp()
        } else {
            waitingSecondBack = true

            scope.launch {
                snackbarHostState.showSnackbar(message)
            }

            resetJob?.cancel()
            resetJob =
                scope.launch {
                    delay(timeoutMs)
                    waitingSecondBack = false
                }
        }
    }
}
