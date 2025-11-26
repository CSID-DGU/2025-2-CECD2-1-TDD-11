package com.tdd.talktobook.feature.startprogress

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.core.ui.common.type.FlowType
import com.tdd.talktobook.domain.entity.enums.AutobiographyStatusType
import com.tdd.talktobook.domain.entity.enums.MaterialType
import com.tdd.talktobook.domain.entity.request.autobiography.StartProgressRequestModel
import com.tdd.talktobook.domain.entity.request.interview.ai.StartInterviewRequestModel
import com.tdd.talktobook.domain.entity.response.autobiography.InterviewAutobiographyModel
import com.tdd.talktobook.domain.entity.response.autobiography.SelectedThemeModel
import com.tdd.talktobook.domain.entity.response.interview.ai.StartInterviewResponseModel
import com.tdd.talktobook.domain.usecase.autobiograph.GetSelectedThemeUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.PostCoShowProgressUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.PostStartProgressUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.SaveAutobiographyIdUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.SaveCurrentAutobiographyStatusUseCase
import com.tdd.talktobook.domain.usecase.interview.SaveInterviewIdUseCase
import com.tdd.talktobook.domain.usecase.interview.ai.PostStartInterviewUseCase
import com.tdd.talktobook.feature.startprogress.type.StartProgressPageType
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class StartProgressViewModel(
    private val postStartProgressUseCase: PostStartProgressUseCase,
    private val saveAutobiographyIdUseCase: SaveAutobiographyIdUseCase,
    private val getSelectedThemeUseCase: GetSelectedThemeUseCase,
    private val postStartInterviewUseCase: PostStartInterviewUseCase,
    private val saveCurrentAutobiographyStatusUseCase: SaveCurrentAutobiographyStatusUseCase,
    private val postCoShowProgressUseCase: PostCoShowProgressUseCase,
    private val saveInterviewIdUseCase: SaveInterviewIdUseCase
) : BaseViewModel<StartProgressPageState>(
        StartProgressPageState(),
    ) {
    fun setFlowType(type: FlowType) {
        updateState(
            uiState.value.copy(
                flowType = type
            )
        )
    }

    fun setPageType(type: StartProgressPageType) {
        updateState(
            uiState.value.copy(
                pageType = type,
                isBtnActivated = false,
            ),
        )
    }

    fun setMaterial(type: MaterialType) {
        updateState(
            uiState.value.copy(
                material = type,
                isBtnActivated = true,
            ),
        )
    }

    fun onNickNameValueChange(newValue: String) {
        updateState(
            uiState.value.copy(
                nickNameInput = newValue,
                isBtnActivated = newValue.isNotEmpty()
            )
        )
    }

    fun onReasonValueChange(newValue: String) {
        updateState(
            uiState.value.copy(
                reasonInput = newValue,
                isBtnActivated = newValue.isNotEmpty(),
            ),
        )
    }

    fun checkStartProgress() {
        val type = uiState.value.flowType

        when (type) {
            FlowType.COSHOW -> {
                postCoShowStartProgress()
            }
            FlowType.DEFAULT -> {
                postStartProgress()
            }
        }
    }

    private fun postCoShowStartProgress() {
        viewModelScope.launch {
            postCoShowProgressUseCase(
                StartProgressRequestModel(uiState.value.material.type, uiState.value.reasonInput),
            ).collect { resultResponse(it, ::onSuccessCoShowStartProgress) }
        }
    }

    private fun onSuccessCoShowStartProgress(data: InterviewAutobiographyModel) {
        updateState(
            uiState.value.copy(
                interviewId = data.interviewId,
                autobiographyId = data.autobiographyId,
            ),
        )

        saveCurrentAutobiographyId(data.autobiographyId)
        saveInterviewId(data.interviewId)
    }

    private fun postStartProgress() {
        viewModelScope.launch {
            postStartProgressUseCase(
                StartProgressRequestModel(uiState.value.material.type, uiState.value.reasonInput),
            ).collect { resultResponse(it, ::onSuccessStartProgress) }
        }
    }

    private fun onSuccessStartProgress(data: InterviewAutobiographyModel) {
        updateState(
            uiState.value.copy(
                interviewId = data.interviewId,
                autobiographyId = data.autobiographyId,
            ),
        )

        changeAutobiographyStatus()
        saveCurrentAutobiographyId(data.autobiographyId)
        initGetSelectedTheme(data.autobiographyId)
    }

    private fun changeAutobiographyStatus() {
        viewModelScope.launch {
            saveCurrentAutobiographyStatusUseCase(AutobiographyStatusType.PROGRESS).collect { resultResponse(it, {}) }
        }
    }

    private fun saveCurrentAutobiographyId(id: Int) {
        viewModelScope.launch {
            saveAutobiographyIdUseCase(id).collect { resultResponse(it, {}) }
        }
    }

    private fun saveInterviewId(id: Int) {
        viewModelScope.launch {
            saveInterviewIdUseCase(id).collect { resultResponse(it, {}) }
        }

        emitEventFlow(StartProgressEvent.GoToInterviewPage)
    }

    private fun initGetSelectedTheme(autobiographyId: Int) {
        viewModelScope.launch {
            getSelectedThemeUseCase(autobiographyId).collect {
                resultResponse(
                    it,
                    { selectedThemes -> initStartInterview(autobiographyId, selectedThemes) },
                )
            }
        }
    }

    private fun initStartInterview(
        autobiographyId: Int,
        selectedThemes: SelectedThemeModel,
    ) {
        d("[ktor] startProgress -> autoId: $autobiographyId, categories -> ${selectedThemes.categories}")

        viewModelScope.launch {
            postStartInterviewUseCase(
                StartInterviewRequestModel(autobiographyId, selectedThemes.categories),
            ).collect { resultResponse(it, ::onSuccessGetInterviewQuestion) }
        }
    }

    private fun onSuccessGetInterviewQuestion(data: StartInterviewResponseModel) {
        updateState(
            uiState.value.copy(
                firstQuestion = data.text,
            ),
        )

        emitEventFlow(StartProgressEvent.GoToInterviewPage)
    }
}
