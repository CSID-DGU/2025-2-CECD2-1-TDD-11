package com.tdd.talktobook.core.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberMicPermissionRequester(
    onPermissionGranted: () -> Unit,
    onPermissionDeniedPermanently: () -> Unit,
): () -> Unit {
    return remember {
        {
            onPermissionGranted()
        }
    }
}
