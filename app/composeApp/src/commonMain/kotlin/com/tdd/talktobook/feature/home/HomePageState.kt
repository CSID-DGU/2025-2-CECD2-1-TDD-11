package com.tdd.talktobook.feature.home

import com.tdd.talktobook.core.ui.base.PageState
import com.tdd.talktobook.domain.entity.enums.AutobiographyStatusType
import com.tdd.talktobook.domain.entity.response.autobiography.CountMaterialsItemModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewSummariesItemModel
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class HomePageState(
    val createdMaterialList: List<CountMaterialsItemModel> = emptyList(),
    val autobiographyProgress: Float = 0f,
    val monthInterviewList: List<InterviewSummariesItemModel> = emptyList(),
    val today: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val selectedDay: Int = 1,
    val selectedDate: String = "",
    val currentAutobiographyId: Int = 0,
    val isCurrentProgress: Boolean = false,
    val currentAutobiographyStatus: AutobiographyStatusType = AutobiographyStatusType.EMPTY,
) : PageState
