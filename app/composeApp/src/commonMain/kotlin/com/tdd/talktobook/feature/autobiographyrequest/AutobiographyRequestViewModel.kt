package com.tdd.talktobook.feature.autobiographyrequest

import androidx.lifecycle.viewModelScope
import com.tdd.talktobook.core.ui.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class AutobiographyRequestViewModel(

): BaseViewModel<AutobiographyRequestPageState>(
    AutobiographyRequestPageState()
) {
    init {
        initGetPdf()
    }

    private fun initGetPdf() {
        viewModelScope.launch {
            delay(2_000)

            onSuccessGetPdf("https://s3.ap-northeast-2.amazonaws.com/lifebookshelf-image-bucket/publications/autobiography_335_20251205_052137.pdf")
        }
    }

    private fun onSuccessGetPdf(pdf: String) {
        updateState { state ->
            state.copy(
                pdfUrl = pdf,
                isSuccessDownload = true
            )
        }
    }
}