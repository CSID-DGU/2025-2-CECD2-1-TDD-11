package com.tdd.bookshelf.feature.my

import androidx.lifecycle.viewModelScope
import com.tdd.bookshelf.core.ui.base.BaseViewModel
import com.tdd.bookshelf.domain.entity.request.default.GetQueryDefaultModel
import com.tdd.bookshelf.domain.entity.response.member.MemberInfoModel
import com.tdd.bookshelf.domain.entity.response.publication.PublicationProgressModel
import com.tdd.bookshelf.domain.entity.response.publication.PublishMyListModel
import com.tdd.bookshelf.domain.usecase.member.GetMemberInfoUseCase
import com.tdd.bookshelf.domain.usecase.publication.GetMyPublicationUseCase
import com.tdd.bookshelf.domain.usecase.publication.GetPublicationProgressUseCase
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class MyViewModel(
    private val getMemberInfoUseCase: GetMemberInfoUseCase,
    private val getMyPublicationUseCase: GetMyPublicationUseCase,
    private val getPublicationProgressUseCase: GetPublicationProgressUseCase,
) : BaseViewModel<MyPageState>(
        MyPageState(),
    ) {
    init {
        initSetPublishBookList()
        initSetMemberInfo()
    }

    private fun initSetPublishBookList() {
        viewModelScope.launch {
            getMyPublicationUseCase(GetQueryDefaultModel(PUBLISH_PAGE, PUBLISH_SIZE)).collect {
                resultResponse(
                    it,
                    ::onSuccessPublishBookList,
                )
            }
        }
    }

    private fun onSuccessPublishBookList(data: PublishMyListModel) {
        updateState(
            uiState.value.copy(
                publishBookList = data.results,
            ),
        )

        if (data.results.isNotEmpty()) {
            initSetPublicationProgress(data.results[0].publicationId)
        }
    }

    fun changeAlarmActivatedStatus() {
        updateState(
            uiState.value.copy(
                isAlarmActivated = !uiState.value.isAlarmActivated,
            ),
        )
    }

    private fun initSetMemberInfo() {
        viewModelScope.launch {
            getMemberInfoUseCase(Unit).collect { resultResponse(it, ::onSuccessGetMemberInfo) }
        }
    }

    private fun onSuccessGetMemberInfo(data: MemberInfoModel) {
        updateState(
            uiState.value.copy(
                memberInfo = data,
            ),
        )
    }

    private fun initSetPublicationProgress(publicationId: Int) {
        viewModelScope.launch {
            getPublicationProgressUseCase(publicationId).collect {
                resultResponse(
                    it,
                    ::onSuccessPublicationProgress,
                )
            }
        }
    }

    private fun onSuccessPublicationProgress(data: PublicationProgressModel) {
        updateState(
            uiState.value.copy(
                publishStatus = data.publishStatus,
            ),
        )
    }

    companion object {
        const val PUBLISH_PAGE = 0
        const val PUBLISH_SIZE = 10
    }
}
