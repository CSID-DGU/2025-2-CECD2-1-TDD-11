package com.tdd.talktobook.feature

import com.tdd.talktobook.core.navigation.NavRoutes
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.domain.entity.request.page.OneBtnDialogModel
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class MainViewModel : BaseViewModel<MainPageState>(
    MainPageState(),
) {
    fun setBottomNavType(route: String?) {
        val type =
            when (route) {
                NavRoutes.PublicationScreen.route -> {
                    BottomNavType.PUBLICATION
                }

                NavRoutes.HomeScreen.route -> {
                    BottomNavType.HOME
                }

                NavRoutes.InterviewScreen.route -> {
                    BottomNavType.INTERVIEW
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
                bottomNavType = type,
            ),
        )
    }

    fun onSetOneBtnDialog(data: OneBtnDialogModel) {
        updateState(
            uiState.value.copy(
                oneBtnDialogModel = data,
            ),
        )
    }
}
