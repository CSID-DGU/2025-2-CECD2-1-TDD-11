package com.tdd.talktobook.core.ui.util

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun openUrl(url: String) {
    val nsUrl = NSURL(string = url)
    if (nsUrl != null) {
        UIApplication.sharedApplication.openURL(nsUrl)
    }
}