package com.tdd.bookshelf.feature.publication

import com.tdd.bookshelf.core.ui.base.BaseViewModel
import com.tdd.bookshelf.domain.entity.response.autobiography.AllAutobiographyItemModel
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class PublicationViewModel(): BaseViewModel<PublicationPageState>(
    PublicationPageState()
) {
    init {
        initSetAutobiographyList()
    }

    private fun initSetAutobiographyList() {
        val autobiographies: List<AllAutobiographyItemModel> = listOf(
            AllAutobiographyItemModel(0, 0, 0, "나의 인생", "", "", "", ""),
            AllAutobiographyItemModel(1, 0, 0, "나의 인생", "", "", "", ""),
            AllAutobiographyItemModel(2, 0, 0, "나의 인생", "", "", "", "")
        )

        updateState(
            uiState.value.copy(
                autobiographyList = autobiographies,
                selectedAutobiographyId = autobiographies[0].autobiographyId
            )
        )
    }

    fun setSelectedAutobiographyId(id: Int) {
        updateState(
            uiState.value.copy(
                selectedAutobiographyId = id
            )
        )
    }
}