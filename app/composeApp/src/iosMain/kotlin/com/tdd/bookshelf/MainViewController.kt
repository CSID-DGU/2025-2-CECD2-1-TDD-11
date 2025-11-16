package com.tdd.bookshelf

import androidx.compose.ui.window.ComposeUIViewController
import com.tdd.bookshelf.feature.MainScreen

fun MainViewController() =
    ComposeUIViewController {
        MainScreen()
    }
