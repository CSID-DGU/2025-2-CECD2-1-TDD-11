package com.tdd.talktobook.feature.publication

import com.tdd.talktobook.core.ui.base.PageState
import com.tdd.talktobook.domain.entity.enums.AutobiographyStatusType
import com.tdd.talktobook.domain.entity.response.autobiography.AllAutobiographyItemModel

data class PublicationPageState(
    val autobiographyId: Int = 0,
    val autobiographyList: List<AllAutobiographyItemModel> = emptyList(),
    val selectedAutobiographyId: Int = 0,
    val autobiographyStatus: AutobiographyStatusType = AutobiographyStatusType.EMPTY,
    val nickName: String = ""
) : PageState
