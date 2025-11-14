package com.tdd.feature

import com.tdd.domain.entity.request.CreateUserModel
import com.tdd.domain.entity.response.interview.InterviewChapterItem
import com.tdd.domain.entity.response.progress.ProgressBookInfoModel
import com.tdd.navigation.NavRoutes
import com.tdd.ui.base.BaseViewModel
import com.tdd.ui.common.type.BottomSheetType
import com.tdd.ui.common.type.InterviewType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(

) : BaseViewModel<MainPageState>(
    MainPageState()
) {

    val userModel = MutableSharedFlow<CreateUserModel>(replay = 1)
    val isBookCreateEnabled = MutableSharedFlow<Boolean>(replay = 1)

    fun setBottomNavType(route: String?) {
        val type = when (route) {
            NavRoutes.InterviewMainScreen.route -> {
                BottomNavType.INTERVIEW
            }

            NavRoutes.InterviewChapterScreen.route -> {
                BottomNavType.CHAPTER
            }

            NavRoutes.ProgressScreen.route -> {
                BottomNavType.PROGRESS
            }

            else -> {
                BottomNavType.DEFAULT
            }
        }

        updateBottomNav(type)
    }

    private fun updateBottomNav(type: BottomNavType) {
        updateState(
            uiState.value.copy(
                bottomNavType = type
            )
        )
    }

    fun setInterviewType(type: InterviewType) {
        updateState(
            uiState.value.copy(
                interviewType = type
            )
        )

        Timber.d("[인터뷰] $type")
    }

    fun setChapterBottomSheet(id: Int, chapter: InterviewChapterItem) {
        updateState(
            uiState.value.copy(
                currentChapterId = id,
                selectedChapter = chapter,
                bottomSheetType = BottomSheetType.CHAPTER
            )
        )
    }

    fun setCreateBookInfoBottomSheet(bookInfo: ProgressBookInfoModel) {
        updateState(
            uiState.value.copy(
                createBookInfo = bookInfo,
                bottomSheetType = BottomSheetType.CREATEBOOK
            )
        )
    }
}