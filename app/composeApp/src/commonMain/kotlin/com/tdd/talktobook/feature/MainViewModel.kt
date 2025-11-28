package com.tdd.talktobook.feature

import com.tdd.talktobook.core.navigation.NavRoutes
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.core.ui.common.type.FlowType
import com.tdd.talktobook.domain.entity.request.page.OneBtnDialogModel
import com.tdd.talktobook.domain.entity.request.page.TwoBtnDialogModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class MainViewModel : BaseViewModel<MainPageState>(
    MainPageState(),
) {
    val userNickName = MutableStateFlow("")
    val screenFlowType = MutableStateFlow(FlowType.DEFAULT)

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
        updateState { state ->
            state.copy(
                bottomNavType = type,
            )
        }
    }

    fun onSetOneBtnDialog(data: OneBtnDialogModel) {
        updateState { state ->
            state.copy(
                oneBtnDialogModel = data,
            )
        }
    }

    fun onSetTwoBtnDialog(data: TwoBtnDialogModel) {
        updateState { state ->
            state.copy(
                twoBtnDialogModel = data,
            )
        }
    }
}
