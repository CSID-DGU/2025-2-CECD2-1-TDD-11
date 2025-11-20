package com.tdd.talktobook.feature.startprogress

import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.domain.entity.enums.MaterialType
import com.tdd.talktobook.feature.startprogress.type.StartProgressPageType
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class StartProgressViewModel(

): BaseViewModel<StartProgressPageState>(
    StartProgressPageState()
) {
    fun setPageType(type: StartProgressPageType) {
        updateState(
            uiState.value.copy(
                pageType = type,
                isBtnActivated = false
            )
        )
    }

    fun setMaterial(type: MaterialType) {
        updateState(
            uiState.value.copy(
                material = type,
                isBtnActivated = true
            )
        )
    }

    fun onReasonValueChange(newValue: String) {
        updateState(
            uiState.value.copy(
                reasonInput = newValue,
                isBtnActivated = newValue.isNotEmpty()
            )
        )
    }

    fun postStartProgress() {
        //
    }
}