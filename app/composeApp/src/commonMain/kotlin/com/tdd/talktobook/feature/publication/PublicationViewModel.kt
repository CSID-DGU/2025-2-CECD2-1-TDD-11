package com.tdd.talktobook.feature.publication

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.domain.entity.enums.AutobiographyStatusType
import com.tdd.talktobook.domain.entity.request.autobiography.CreateAutobiographyRequestModel
import com.tdd.talktobook.domain.entity.response.autobiography.AllAutobiographyListModel
import com.tdd.talktobook.domain.usecase.auth.DeleteLocalAllDataExceptTokenUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.ChangeAutobiographyStatusUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetAllAutobiographyUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetAutobiographyIdUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetAutobiographyStatusUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.PatchCreateAutobiographyUseCase
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class PublicationViewModel(
    private val getAutobiographyIdUseCase: GetAutobiographyIdUseCase,
    private val getAllAutobiographyUseCase: GetAllAutobiographyUseCase,
    private val getAutobiographyStatusUseCase: GetAutobiographyStatusUseCase,
    private val createAutobiographyUseCase: PatchCreateAutobiographyUseCase,
    private val changeAutobiographyStatusUseCase: ChangeAutobiographyStatusUseCase,
    private val deleteLocalAllDataUseCase: DeleteLocalAllDataExceptTokenUseCase,
) : BaseViewModel<PublicationPageState>(
        PublicationPageState(),
    ) {
    init {
        initGetAutobiographyStatus()
        initGetAutobiographyId()
        initSetAutobiographyList()
    }

    private fun initGetAutobiographyStatus() {
        viewModelScope.launch {
            getAutobiographyStatusUseCase(Unit).collect { resultResponse(it, ::onSuccessGetAutobiographyStatus) }
        }
    }

    private fun onSuccessGetAutobiographyStatus(status: AutobiographyStatusType) {
        updateState { state ->
            state.copy(
                autobiographyStatus = status,
            )
        }
    }

    private fun initGetAutobiographyId() {
        viewModelScope.launch {
            getAutobiographyIdUseCase(Unit).collect { resultResponse(it, ::onSuccessGetAutobiographyId) }
        }
    }

    private fun onSuccessGetAutobiographyId(id: Int) {
        d("[test] interview (publication) -> id: $id")
        updateState { state ->
            state.copy(
                autobiographyId = id,
            )
        }
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
        updateState { state ->
            state.copy(
                autobiographyList = data.results.filter { it.status == AutobiographyStatusType.FINISH.type },
                selectedAutobiographyId = if (data.results.isNotEmpty()) data.results[0].autobiographyId else 0,
            )
        }
    }

    fun setUserNickName(name: String) {
        d("[test] interview (publication)  -> name: $name")

        updateState { state ->
            state.copy(
                nickName = name,
            )
        }
    }

    fun setSelectedAutobiographyId(id: Int) {
        updateState { state ->
            state.copy(
                selectedAutobiographyId = id,
            )
        }
    }

    fun createAutobiography() {
        viewModelScope.launch {
            createAutobiographyUseCase(CreateAutobiographyRequestModel(uiState.value.autobiographyId, "name")).collect {
                resultResponse(it, {
                    d("[test] interview (publication) -> create success")
                    emitEventFlow(PublicationEvent.ShowPublicationSuccessToast)
                }, { error ->
                    d("[test] interview (publication) -> failure: $error")
                })
            }
        }

        initClearLocalData()
    }

    private fun initClearLocalData() {
        viewModelScope.launch {
            deleteLocalAllDataUseCase(Unit).collect { resultResponse(it, {}) }
        }
    }
}
