package com.tdd.talktobook.core.ui.util

import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual fun openUrl(url: String) {
    val nsUrl = NSURL(string = url)
    val app = UIApplication.sharedApplication

    dispatch_async(dispatch_get_main_queue()) {
        if (!app.canOpenURL(nsUrl)) {
            println("open url failure: $url")
            return@dispatch_async
        }

        app.openURL(
            nsUrl,
            options = emptyMap<Any?, Any?>(),
        ) { success ->
            println("open url success: $success, url=$url")
        }
    }
}
