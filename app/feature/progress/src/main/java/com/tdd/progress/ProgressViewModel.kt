package com.tdd.progress

import com.tdd.domain.entity.response.CreatedBookModel
import com.tdd.domain.entity.response.progress.ProgressBookInfoModel
import com.tdd.domain.entity.response.progress.ProgressStepItem
import com.tdd.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(

): BaseViewModel<ProgressPageState>(
    ProgressPageState()
) {
    init {
        initSetProgressStep()
    }

    private fun initSetProgressStep() {
        // TODO 서버통신
        val progressStep: List<ProgressStepItem> = listOf(
            ProgressStepItem(1, "인터뷰 진행", "자서전 생성을 위해\n인터뷰를 진행해요", true, false),
            ProgressStepItem(2, "출판 신청", "챕터 생성이 끝났어요.\n출판 신청을 해보세요!", false, true),
            ProgressStepItem(3, "문법 교정", "자서전 생성이 끝나 문법\n교정을 진행하고 있어요", false, false),
            ProgressStepItem(4, "기업 검토", "최종적으로 기업에서\n자서전을 검토 중이에요.", false, false),
            ProgressStepItem(5, "출판 진행", "자서전 최종 출판을\n진행하고 있어요", false, false),
        )

        updateState(
            uiState.value.copy(
                progressStep = progressStep
            )
        )
    }

    // TODO 서버통신
    fun setCreateBookInfo(): ProgressBookInfoModel = ProgressBookInfoModel("혼란을 건너 성장으로", listOf(), "2025.08.18", "2025.08.30", 300, "50,000")

    fun createBook() {
        val createdBook = CreatedBookModel(com.tdd.design_system.R.drawable.ic_book_example2, "혼란을 건너 성장으로", "")
        val progressStep: List<ProgressStepItem> = listOf(
            ProgressStepItem(1, "인터뷰 진행", "자서전 생성을 위해\n인터뷰를 진행해요", true, false),
            ProgressStepItem(2, "출판 신청", "챕터 생성이 끝났어요.\n출판 신청을 해보세요!", true, false),
            ProgressStepItem(3, "문법 교정", "자서전 생성이 끝나 문법\n교정을 진행하고 있어요", false, true),
            ProgressStepItem(4, "기업 검토", "최종적으로 기업에서\n자서전을 검토 중이에요.", false, false),
            ProgressStepItem(5, "출판 진행", "자서전 최종 출판을\n진행하고 있어요", false, false),
        )

        updateState(
            uiState.value.copy(
                createdBook = createdBook,
                isCreatedBook = true,
                progressStep = progressStep
            )
        )
    }
}