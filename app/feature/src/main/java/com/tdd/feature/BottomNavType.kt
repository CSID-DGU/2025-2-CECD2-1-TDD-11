package com.tdd.feature

import com.tdd.design_system.Empty
import com.tdd.design_system.InterviewChapterTitle
import com.tdd.design_system.InterviewTitle
import com.tdd.design_system.ProgressTitle
import com.tdd.design_system.R
import com.tdd.navigation.NavRoutes

enum class BottomNavType(
    val navName: String,
    val navIconOn: Int,
    val navIconOff: Int,
    val destination: String,
) {
    INTERVIEW(
        InterviewTitle,
        R.drawable.ic_bottom_interview_on,
        R.drawable.ic_bottom_interview_off,
        NavRoutes.InterviewMainScreen.route
    ),
    CHAPTER(
        InterviewChapterTitle,
        R.drawable.ic_bottom_chapter_on,
        R.drawable.ic_bottom_chapter_off,
        NavRoutes.InterviewChapterScreen.route
    ),
    PROGRESS(
        ProgressTitle,
        R.drawable.ic_bottom_progress_on,
        R.drawable.ic_bottom_progress_off,
        NavRoutes.ProgressScreen.route
    ),
    DEFAULT(Empty, -1, -1, "");

    companion object {
        fun getBottomNavIcon(navType: BottomNavType, isSelected: Boolean): Int =
            when (isSelected) {
                true -> entries.firstOrNull { it == navType }?.navIconOn ?: -1
                false -> entries.firstOrNull { it == navType }?.navIconOff ?: -1
            }

        fun getDestination(navType: BottomNavType): String =
            entries.firstOrNull { it == navType }?.destination ?: ""
    }
}