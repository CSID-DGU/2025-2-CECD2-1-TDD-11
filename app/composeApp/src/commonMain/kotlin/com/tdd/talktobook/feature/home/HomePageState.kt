package com.tdd.talktobook.feature.home

import com.tdd.talktobook.core.ui.base.PageState
import com.tdd.talktobook.domain.entity.enums.AutobiographyStatusType
import com.tdd.talktobook.domain.entity.response.autobiography.AllAutobiographyItemModel
import com.tdd.talktobook.domain.entity.response.autobiography.AllAutobiographyListModel
import com.tdd.talktobook.domain.entity.response.autobiography.ChapterItemModel
import com.tdd.talktobook.domain.entity.response.autobiography.CountMaterialsItemModel
import com.tdd.talktobook.domain.entity.response.autobiography.SubChapterItemModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewSummariesItemModel
import com.tdd.talktobook.domain.entity.response.member.MemberInfoModel
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

    // Legacy
    val chapterList: List<ChapterItemModel> = emptyList(),
    val subChapterList: List<SubChapterItemModel> = emptyList(),
    val currentChapterId: Int = 0,
    val currentChapter: SubChapterItemModel = SubChapterItemModel(),
    val allAutobiography: AllAutobiographyListModel = AllAutobiographyListModel(),
    val allAutobiographyList: List<AllAutobiographyItemModel> = emptyList(),
    val memberInfo: MemberInfoModel = MemberInfoModel(),
    val interviewQuestions: List<String> = emptyList(),
    val selectedDetailChapterId: Int = 0,
) : PageState
