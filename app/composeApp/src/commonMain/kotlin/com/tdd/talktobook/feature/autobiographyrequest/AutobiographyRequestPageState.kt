package com.tdd.talktobook.feature.autobiographyrequest

import com.tdd.talktobook.core.ui.base.PageState

data class AutobiographyRequestPageState (
    val pdfUrl: String = "",
    val isSuccessDownload: Boolean = false
): PageState