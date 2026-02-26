package com.tdd.talktobook.feature.home

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.designsystem.HomeDateSelectTitle
import com.tdd.talktobook.core.designsystem.SelectItem
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.core.ui.util.daysInMonth
import com.tdd.talktobook.core.ui.util.generateCalendarDays
import com.tdd.talktobook.core.ui.util.setDateStringType
import com.tdd.talktobook.domain.entity.enums.AutobiographyStatusType
import com.tdd.talktobook.domain.entity.request.interview.InterviewSummariesRequestModel
import com.tdd.talktobook.domain.entity.request.page.ScrollSelectBottomSheetModel
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
import kotlinx.datetime.LocalDate
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
    private val saveInterviewIdUseCase: SaveInterviewIdUseCase,
) : BaseViewModel<HomePageState>(
        HomePageState(),
    ) {
    init {
        initSetTodayDate()
        initGetCurrentProgress()
    }

    private fun initSetTodayDate() {
        val today = uiState.value.today
        val todayDate = setDateStringType(today.year.toString(), today.monthNumber.toString(), today.dayOfMonth.toString())

        updateState { state ->
            state.copy(
                selectedDate = todayDate,
                selectedDay = today.dayOfMonth,
                days = generateCalendarDays(today.year, today.monthNumber)
            )
        }
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
                updateState { state ->
                    state.copy(
                        isCurrentProgress = false,
                    )
                }

                saveCurrentProgress(AutobiographyStatusType.EMPTY)
            }
        }
    }

    private fun setCurrentState(data: CurrentProgressAutobiographyModel) {
        val today = uiState.value.today

        updateState { state ->
            state.copy(
                currentAutobiographyId = data.autobiographyId,
                isCurrentProgress = true,
            )
        }

        saveCurrentAutobiographyId(data.autobiographyId)
        initSetCreatedMaterials(data.autobiographyId)
        initSetInterviewProgress(data.autobiographyId)
        initSetMonthInterviewList(data.autobiographyId, today.year, today.monthNumber)
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
        updateState { state ->
            state.copy(
                createdMaterialList = data.popularMaterials,
            )
        }
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
        updateState { state ->
            state.copy(
                autobiographyProgress = data.progressPercentage,
                currentAutobiographyStatus = data.status,
            )
        }

        saveCurrentProgress(data.status)
    }

    private fun saveCurrentProgress(status: AutobiographyStatusType) {
        viewModelScope.launch {
            saveCurrentAutobiographyStatusUseCase(status).collect { resultResponse(it, {}) }
        }
    }

    private fun initSetMonthInterviewList(autobiographyId: Int, year: Int, month: Int) {
        viewModelScope.launch {
            getInterviewSummariesUseCase(InterviewSummariesRequestModel(autobiographyId, year, month)).collect { resultResponse(it, ::onSuccessGetMonthSummaries) }
        }
    }

    private fun onSuccessGetMonthSummaries(interviews: InterviewSummariesListModel) {
        d("[ktor] homeViewmodel -> ${interviews.interviews}")
        updateState { state ->
            state.copy(
                monthInterviewList = interviews.interviews,
            )
        }

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
        val today = uiState.value.today
        val targetDate =
            LocalDate(
                year = today.year,
                monthNumber = today.monthNumber,
                dayOfMonth = day,
            )
        val selectedDate = setDateStringType(targetDate.year.toString(), targetDate.monthNumber.toString(), targetDate.dayOfMonth.toString())

        updateState { state ->
            state.copy(
                selectedDay = day,
                selectedDate = selectedDate,
            )
        }
    }

    fun setDateSelectList(): ScrollSelectBottomSheetModel {
        val monthList = (1..12).map { it.toString() }
        val monthVisibleIndex = uiState.value.today.monthNumber - 1

        val year = uiState.value.today.year
        val yearList = (year-10..year).map { it.toString() }
        val yearVisibleIndex = yearList.lastIndex

        val daysInMonth = daysInMonth(uiState.value.today.year, uiState.value.today.monthNumber)
        val dayList = (1..daysInMonth).map { it.toString() }
        val dayVisibleIndex = uiState.value.today.dayOfMonth - 1

        return ScrollSelectBottomSheetModel(monthVisibleIndex, dayVisibleIndex, yearVisibleIndex, monthList, dayList, yearList, HomeDateSelectTitle, SelectItem, onSelectItem = {month, day, year -> setSelectedDate(month, day, year)})
    }

    fun setSelectedDate(month: String, day: String, year: String) {
        d("[테스트] $month, $day, $year")

        val selectedDate = setDateStringType(year, month, day)

        updateState { state ->
            state.copy(
                selectedDay = day.toInt(),
                selectedDate = selectedDate,
                days = generateCalendarDays(year.toInt(), month.toInt())
            )
        }

        if (uiState.value.isCurrentProgress) {
            initSetMonthInterviewList(uiState.value.currentAutobiographyId, year.toInt(), month.toInt())
        }
    }
}
