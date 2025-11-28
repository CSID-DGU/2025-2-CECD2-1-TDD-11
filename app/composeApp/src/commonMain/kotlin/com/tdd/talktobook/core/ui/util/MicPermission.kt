package com.tdd.talktobook.core.ui.util

import androidx.compose.runtime.Composable

@Composable
expect fun rememberMicPermissionRequester(
    onPermissionGranted: () -> Unit,
    onPermissionDeniedPermanently: () -> Unit = {},
): () -> Unit
