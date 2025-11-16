package com.tdd.bookshelf.feature.publication

import com.tdd.bookshelf.core.ui.base.PageState
import com.tdd.bookshelf.domain.entity.response.autobiography.AllAutobiographyItemModel


data class PublicationPageState(
    val autobiographyList: List<AllAutobiographyItemModel> = emptyList(),
    val selectedAutobiographyId: Int = 0
): PageState