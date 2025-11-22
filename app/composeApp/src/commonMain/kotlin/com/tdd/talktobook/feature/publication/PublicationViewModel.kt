package com.tdd.talktobook.feature.publication

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.domain.entity.enums.AutobiographyStatusType
import com.tdd.talktobook.domain.entity.response.autobiography.AllAutobiographyListModel
import com.tdd.talktobook.domain.usecase.autobiograph.GetAllAutobiographyUseCase
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class PublicationViewModel(
    private val getAllAutobiographyUseCase: GetAllAutobiographyUseCase,
) : BaseViewModel<PublicationPageState>(
    PublicationPageState(),
) {
    init {
        initSetAutobiographyList()
    }

    private fun initSetAutobiographyList() {
        viewModelScope.launch {
            getAllAutobiographyUseCase(Unit).collect {
                resultResponse(it, ::onSuccessGetAutobiographies)
            }
        }
    }

    private fun onSuccessGetAutobiographies(data: AllAutobiographyListModel) {
        d("[ktor] publicationViewmodel -> $data")
        updateState(
            uiState.value.copy(
                autobiographyList = data.results.filter { it.status == AutobiographyStatusType.FINISH.type },
                selectedAutobiographyId = data.results[0].autobiographyId,
            ),
        )
    }

    fun setSelectedAutobiographyId(id: Int) {
        updateState(
            uiState.value.copy(
                selectedAutobiographyId = id,
            ),
        )
    }
}
