package com.tdd.talktobook.core.ui.util.exit

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

@Composable
actual fun ExitApp(): () -> Unit {
    val activity = LocalContext.current.findActivity()
    return remember(activity) {
        { activity?.finishAffinity() }
    }
}