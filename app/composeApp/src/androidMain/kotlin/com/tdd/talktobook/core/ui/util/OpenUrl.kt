package com.tdd.talktobook.core.ui.util

import android.content.Intent
import androidx.core.net.toUri
import com.tdd.talktobook.app.TalkToBookApplication

actual fun openUrl(url: String) {
    val context = TalkToBookApplication.applicationContext()
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    context.startActivity(intent)
}