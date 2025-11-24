package com.tdd.talktobook.feature.home

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.domain.entity.enums.AutobiographyStatusType
import com.tdd.talktobook.domain.entity.request.interview.InterviewSummariesRequestModel
import com.tdd.talktobook.domain.entity.response.autobiography.CountMaterialsResponseModel
import com.tdd.talktobook.domain.entity.response.autobiography.CurrentInterviewProgressModel
import com.tdd.talktobook.domain.entity.response.autobiography.CurrentProgressAutobiographyModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewSummariesListModel
import com.tdd.talktobook.domain.usecase.autobiograph.GetCountMaterialsUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetCurrentInterviewProgressUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetCurrentProgressAutobiographyUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.SaveAutobiographyIdUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.SaveCurrentAutobiographyStatusUseCase
import com.tdd.talktobook.domain.usecase.interview.GetInterviewSummariesUseCase
import com.tdd.talktobook.domain.usecase.interview.SaveInterviewIdUseCase
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.android.annotation.KoinViewModel
import kotlin.time.ExperimentalTime

@KoinViewModel
class HomeViewModel(
    private val getCurrentProgressAutobiographyUseCase: GetCurrentProgressAutobiographyUseCase,
    private val getCurrentInterviewProgressUseCase: GetCurrentInterviewProgressUseCase,
    private val getCountMaterialsUseCase: GetCountMaterialsUseCase,
    private val getInterviewSummariesUseCase: GetInterviewSummariesUseCase,
    private val saveCurrentAutobiographyStatusUseCase: SaveCurrentAutobiographyStatusUseCase,
    private val saveAutobiographyIdUseCase: SaveAutobiographyIdUseCase,
    private val saveInterviewIdUseCase: SaveInterviewIdUseCase
) : BaseViewModel<HomePageState>(
    HomePageState(),
) {
    init {
        initSetTodayDate()
        initGetCurrentProgress()
    }

    private fun initSetTodayDate() {
        val today = uiState.value.today
        val todayDate =
            buildString {
                append(today.year.toString().padStart(4, '0'))
                append('.')
                append(today.monthNumber.toString().padStart(2, '0'))
                append('.')
                append(today.dayOfMonth.toString().padStart(2, '0'))
            }

        updateState(
            uiState.value.copy(
                selectedDate = todayDate,
                selectedDay = today.dayOfMonth,
            ),
        )
    }

    private fun initGetCurrentProgress() {
        viewModelScope.launch {
            getCurrentProgressAutobiographyUseCase(Unit).collect { resultResponse(it, ::onSuccessGetCurrent) }
        }
    }

    private fun onSuccessGetCurrent(data: CurrentProgressAutobiographyModel) {
        d("[ktor] homeViewmodel -> $data")
        when (data.isProgress) {
            true -> {
                setCurrentState(data)
            }

            false -> {
                updateState(
                    uiState.value.copy(isCurrentProgress = false)
                )
            }
        }
    }

    private fun setCurrentState(data: CurrentProgressAutobiographyModel) {
        updateState(
            uiState.value.copy(
                currentAutobiographyId = data.autobiographyId,
                isCurrentProgress = true
            )
        )

        saveCurrentAutobiographyId(data.autobiographyId)
        initSetCreatedMaterials(data.autobiographyId)
        initSetInterviewProgress(data.autobiographyId)
        initSetMonthInterviewList(data.autobiographyId)
    }

    private fun saveCurrentAutobiographyId(id: Int) {
        viewModelScope.launch {
            saveAutobiographyIdUseCase(id).collect { resultResponse(it, {}) }
        }
    }


    private fun initSetCreatedMaterials(autobiographyId: Int) {
        viewModelScope.launch {
            getCountMaterialsUseCase(autobiographyId).collect {
                resultResponse(it, ::onSuccessCountMaterials)
            }
        }
    }

    private fun onSuccessCountMaterials(data: CountMaterialsResponseModel) {
        d("[ktor] homeViewmodel -> $data")
        updateState(
            uiState.value.copy(
                createdMaterialList = data.popularMaterials,
            ),
        )
    }

    private fun initSetInterviewProgress(autobiographyId: Int) {
        viewModelScope.launch {
            getCurrentInterviewProgressUseCase(autobiographyId).collect {
                resultResponse(it, ::onSuccessInterviewProgress)
            }
        }
    }

    private fun onSuccessInterviewProgress(data: CurrentInterviewProgressModel) {
        d("[ktor] homeViewmodel -> $data")
        updateState(
            uiState.value.copy(
                autobiographyProgress = data.progressPercentage,
                currentAutobiographyStatus = data.status
            )
        )

        saveCurrentProgress(data.status)
    }

    private fun saveCurrentProgress(status: AutobiographyStatusType) {
        viewModelScope.launch {
            saveCurrentAutobiographyStatusUseCase(status).collect { resultResponse(it, {}) }
        }
    }

    private fun initSetMonthInterviewList(autobiographyId: Int) {
        val today = uiState.value.today

        viewModelScope.launch {
            getInterviewSummariesUseCase(InterviewSummariesRequestModel(autobiographyId, today.year, today.monthNumber)).collect { resultResponse(it, ::onSuccessGetMonthSummaries) }
        }
    }

    private fun onSuccessGetMonthSummaries(interviews: InterviewSummariesListModel) {
        d("[ktor] homeViewmodel -> ${interviews.interviews}")
        updateState(
            uiState.value.copy(
                monthInterviewList = interviews.interviews,
            ),
        )

        setTodayInterviewId(interviews)
    }

    private fun setTodayInterviewId(interviews: InterviewSummariesListModel) {
        val today = uiState.value.today.dayOfMonth
        val interviewId = interviews.interviews.firstOrNull { it.date.split("-")[2].toInt() == today }?.id ?: 0

        viewModelScope.launch {
            saveInterviewIdUseCase(interviewId).collect { resultResponse(it, {}) }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun onClickInterviewDate(day: Int) {
        val today =
            Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
        val targetDate =
            LocalDate(
                year = today.year,
                monthNumber = today.monthNumber,
                dayOfMonth = day,
            )
        val selectedDate =
            buildString {
                append(targetDate.year.toString().padStart(4, '0'))
                append('.')
                append(targetDate.monthNumber.toString().padStart(2, '0'))
                append('.')
                append(targetDate.dayOfMonth.toString().padStart(2, '0'))
            }

        updateState(
            uiState.value.copy(
                selectedDay = day,
                selectedDate = selectedDate,
            ),
        )
    }
}
