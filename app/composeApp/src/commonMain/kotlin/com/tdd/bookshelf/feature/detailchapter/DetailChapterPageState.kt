package com.tdd.bookshelf.feature.detailchapter

import com.tdd.bookshelf.core.ui.base.PageState
import com.tdd.bookshelf.domain.entity.response.autobiography.AutobiographiesDetailModel

data class DetailChapterPageState(
    val selectedAutobiographyId: Int = 0,
    val detailChapter: AutobiographiesDetailModel = AutobiographiesDetailModel(),
) : PageState
