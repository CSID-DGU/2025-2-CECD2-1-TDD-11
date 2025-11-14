package com.tdd.progress

import com.tdd.domain.entity.response.CreatedBookModel
import com.tdd.domain.entity.response.progress.ProgressStepItem
import com.tdd.ui.base.PageState

data class ProgressPageState(
    val progressStep: List<ProgressStepItem> = emptyList(),
    val isCreatedBook: Boolean = false,
    val createdBook: CreatedBookModel = CreatedBookModel()
) : PageState