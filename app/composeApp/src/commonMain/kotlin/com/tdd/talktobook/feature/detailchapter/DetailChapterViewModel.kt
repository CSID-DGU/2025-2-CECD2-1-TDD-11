package com.tdd.talktobook.feature.detailchapter

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.domain.entity.response.autobiography.AutobiographiesDetailModel
import com.tdd.talktobook.domain.usecase.autobiograph.GetAutobiographiesDetailUseCase
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class DetailChapterViewModel(
    private val getAutobiographiesDetailUseCase: GetAutobiographiesDetailUseCase,
) : BaseViewModel<DetailChapterPageState>(
        DetailChapterPageState(),
    ) {
    fun setAutobiographyId(autobiographyId: Int) {
        updateState(
            uiState.value.copy(
                selectedAutobiographyId = autobiographyId,
            ),
        )

        initSetAutobiographyDetail(autobiographyId)
    }

    private fun initSetAutobiographyDetail(autobiographyId: Int) {
        viewModelScope.launch {
            getAutobiographiesDetailUseCase(autobiographyId).collect { resultResponse(it, ::onSuccessAutobiographyDetail) }
        }
    }

    private fun onSuccessAutobiographyDetail(data: AutobiographiesDetailModel) {
        d("[ktor] detailChapterViewModel -> $data")
        updateState(
            uiState.value.copy(
                detailChapter = data,
            ),
        )
    }
}
