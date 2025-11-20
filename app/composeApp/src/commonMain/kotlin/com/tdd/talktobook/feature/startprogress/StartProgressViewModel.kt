package com.tdd.talktobook.feature.startprogress

import androidx.lifecycle.viewModelScope
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.domain.entity.enums.MaterialType
import com.tdd.talktobook.domain.entity.request.autobiography.StartProgressRequestModel
import com.tdd.talktobook.domain.entity.response.autobiography.InterviewAutobiographyModel
import com.tdd.talktobook.domain.usecase.autobiograph.PostStartProgressUseCase
import com.tdd.talktobook.feature.startprogress.type.StartProgressPageType
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class StartProgressViewModel(
    private val postStartProgressUseCase: PostStartProgressUseCase,
) : BaseViewModel<StartProgressPageState>(
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
        viewModelScope.launch {
            postStartProgressUseCase(
                StartProgressRequestModel(uiState.value.material.type, uiState.value.reasonInput)
            ).collect { resultResponse(it, ::onSuccessStartProgress) }
        }
    }

    private fun onSuccessStartProgress(data: InterviewAutobiographyModel) {
        updateState(
            uiState.value.copy(
                interviewId = data.interviewId,
                autobiographyId = data.autobiographyId
            )
        )

        emitEventFlow(StartProgressEvent.GoToInterviewPage)
    }
}