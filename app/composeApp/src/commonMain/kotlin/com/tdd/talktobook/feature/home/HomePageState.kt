package com.tdd.talktobook.feature.home

import com.tdd.talktobook.core.ui.base.PageState
import com.tdd.talktobook.domain.entity.response.autobiography.AllAutobiographyItemModel
import com.tdd.talktobook.domain.entity.response.autobiography.AllAutobiographyListModel
import com.tdd.talktobook.domain.entity.response.autobiography.ChapterItemModel
import com.tdd.talktobook.domain.entity.response.autobiography.CreatedMaterialIItemModel
import com.tdd.talktobook.domain.entity.response.autobiography.SubChapterItemModel
import com.tdd.talktobook.domain.entity.response.interview.MonthInterviewItemModel
import com.tdd.talktobook.domain.entity.response.member.MemberInfoModel

data class HomePageState(
    val createdMaterialList: List<CreatedMaterialIItemModel> = emptyList(),
    val autobiographyProgress: Int = 0,
    val monthInterviewList: List<MonthInterviewItemModel> = emptyList(),
    val selectedDay: Int = 1,
    val selectedDate: String = "",
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
