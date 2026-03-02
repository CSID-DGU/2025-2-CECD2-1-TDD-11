package com.tdd.talktobook.feature.publication

import com.tdd.talktobook.core.ui.base.PageState
import com.tdd.talktobook.domain.entity.response.autobiography.AllAutobiographyItemModel

data class PublicationPageState(
    val autobiographyList: List<AllAutobiographyItemModel> = emptyList(),
    val selectedAutobiographyId: Int = 0,
) : PageState
