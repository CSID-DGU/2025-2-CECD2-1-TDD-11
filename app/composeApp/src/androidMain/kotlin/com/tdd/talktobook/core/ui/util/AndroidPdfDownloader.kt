package com.tdd.talktobook.core.ui.util

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

private class AndroidPdfDownloader(
    private val context: Context,
) : PdfDownloader {
    override fun download(
        url: String,
        suggestedFileName: String?,
    ) {
        val fileName = suggestedFileName ?: url.toUri().lastPathSegment ?: "대화로책 자서전.pdf"

        val request =
            DownloadManager.Request(url.toUri())
                .setTitle(fileName)
                .setDescription("PDF 다운로드 중…")
                .setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED,
                )
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    fileName,
                )
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)
    }
}

@Composable
actual fun rememberPdfDownloader(): PdfDownloader {
    val context = LocalContext.current
    return remember { AndroidPdfDownloader(context) }
}
