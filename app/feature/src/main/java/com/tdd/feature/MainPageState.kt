package com.tdd.feature

import com.tdd.domain.entity.response.interview.InterviewChapterItem
import com.tdd.domain.entity.response.progress.ProgressBookInfoModel
import com.tdd.ui.base.PageState
import com.tdd.ui.common.type.BottomSheetType
import com.tdd.ui.common.type.InterviewType

data class MainPageState (
    val interviewType: InterviewType = InterviewType.MIRROR,
    val bottomNavType: BottomNavType = BottomNavType.DEFAULT,
    val bottomSheetType: BottomSheetType = BottomSheetType.DEFAULT,
    val currentChapterId: Int = 0,
    val selectedChapter: InterviewChapterItem = InterviewChapterItem(),
    val createBookInfo: ProgressBookInfoModel = ProgressBookInfoModel()
): PageState