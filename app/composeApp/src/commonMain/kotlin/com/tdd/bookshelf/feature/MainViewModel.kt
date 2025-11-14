package com.tdd.bookshelf.feature

import com.tdd.bookshelf.core.navigation.NavRoutes
import com.tdd.bookshelf.core.ui.base.BaseViewModel
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class MainViewModel : BaseViewModel<MainPageState>(
    MainPageState(),
) {
    fun setBottomNavType(route: String?) {
        val type =
            when (route) {
                NavRoutes.HomeScreen.route -> {
                    BottomNavType.HOME
                }

                NavRoutes.MyPageScreen.route -> {
                    BottomNavType.MY
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
}
