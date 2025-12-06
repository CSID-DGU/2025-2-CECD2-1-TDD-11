package com.tdd.talktobook.feature.autobiographyrequest

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.domain.entity.enums.AutobiographyStatusType
import com.tdd.talktobook.domain.entity.response.autobiography.CurrentInterviewProgressModel
import com.tdd.talktobook.domain.usecase.autobiograph.GetCurrentCoShowProgressUseCase
import com.tdd.talktobook.domain.usecase.publication.PostPublicationPdfUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class AutobiographyRequestViewModel(
    private val getCurrentCoShowProgressUseCase: GetCurrentCoShowProgressUseCase,
    private val postPublicationPdfUseCase: PostPublicationPdfUseCase,
) : BaseViewModel<AutobiographyRequestPageState>(
        AutobiographyRequestPageState(),
    ) {
    fun initSetId(autobiographyId: Int) {
        updateState { state ->
            state.copy(
                autobiographyId = autobiographyId,
            )
        }

        initGetCurrentProgress(autobiographyId)
    }

    private fun initGetCurrentProgress(autobiographyId: Int) {
        viewModelScope.launch {
            delay(2_000)

            getCurrentCoShowProgressUseCase(autobiographyId).collect {
                resultResponse(it, ::onSuccessGetProgress)
            }
        }
    }

    private fun onSuccessGetProgress(data: CurrentInterviewProgressModel) {
        if (data.status == AutobiographyStatusType.FINISH) {
            initGetPdf(uiState.value.autobiographyId)
        } else {
            initGetCurrentProgress(uiState.value.autobiographyId)
        }
    }

    private fun initGetPdf(autobiographyId: Int) {
        viewModelScope.launch {
            postPublicationPdfUseCase(autobiographyId).collect {
                resultResponse(it, ::onSuccessGetPdf)
            }
        }
    }

    private fun onSuccessGetPdf(pdf: String) {
        d("[ktor] auto pdf test -> $pdf")
        updateState { state ->
            state.copy(
                pdfUrl = pdf,
                isSuccessDownload = true,
            )
        }
    }
}
