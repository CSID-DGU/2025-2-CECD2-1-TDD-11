package com.tdd.talktobook.feature.detailchapter

import com.tdd.talktobook.core.ui.base.PageState
import com.tdd.talktobook.domain.entity.response.autobiography.AutobiographiesDetailModel

data class DetailChapterPageState(
    val selectedAutobiographyId: Int = 0,
    val detailChapter: AutobiographiesDetailModel = AutobiographiesDetailModel(),
) : PageState
