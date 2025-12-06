package com.tdd.talktobook.core.ui.util

import androidx.compose.runtime.Composable

interface PdfDownloader {
    fun download(
        url: String,
        suggestedFileName: String? = null,
    )
}

@Composable
expect fun rememberPdfDownloader(): PdfDownloader
