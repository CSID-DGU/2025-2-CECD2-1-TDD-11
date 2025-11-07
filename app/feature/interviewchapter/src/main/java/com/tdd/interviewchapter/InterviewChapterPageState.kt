package com.tdd.interviewchapter

import com.tdd.domain.entity.response.interview.InterviewChapterItem
import com.tdd.domain.entity.response.interview.InterviewChapterModel
import com.tdd.ui.base.PageState
import java.io.File

data class InterviewChapterPageState(
    val chapterList: InterviewChapterModel = InterviewChapterModel(),
    val progressChapter: InterviewChapterItem = InterviewChapterItem(),
    val selectedChapter: InterviewChapterItem = InterviewChapterItem(),
    val selectedImg: File? = null
) : PageState