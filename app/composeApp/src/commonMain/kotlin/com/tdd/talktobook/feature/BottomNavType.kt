package com.tdd.talktobook.feature

import talktobook.composeapp.generated.resources.Res
import com.tdd.talktobook.core.designsystem.Empty
import com.tdd.talktobook.core.designsystem.Home
import com.tdd.talktobook.core.designsystem.MyPage
import com.tdd.talktobook.core.navigation.NavRoutes
import org.jetbrains.compose.resources.DrawableResource
import talktobook.composeapp.generated.resources.ic_transparent
import talktobook.composeapp.generated.resources.img_home
import talktobook.composeapp.generated.resources.img_profile

enum class BottomNavType(
    val navName: String,
    val navIcon: DrawableResource,
    val destination: String,
) {
    HOME(Home, Res.drawable.img_home, NavRoutes.HomeScreen.route),
    MY(MyPage, Res.drawable.img_profile, NavRoutes.MyPageScreen.route),
    DEFAULT(Empty, Res.drawable.ic_transparent, ""),
    ;

    companion object {
        fun getBottomNavIcon(navType: BottomNavType): DrawableResource =
            entries.firstOrNull { it == navType }?.navIcon ?: Res.drawable.ic_transparent

        fun getDestination(navType: BottomNavType): String =
            entries.firstOrNull { it == navType }?.destination ?: ""
    }
}
