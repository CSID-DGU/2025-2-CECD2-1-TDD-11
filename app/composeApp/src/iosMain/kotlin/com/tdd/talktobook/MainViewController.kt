package com.tdd.talktobook

import androidx.compose.ui.window.ComposeUIViewController
import com.tdd.talktobook.feature.MainScreen

fun MainViewController() =
    ComposeUIViewController {
        MainScreen()
    }
