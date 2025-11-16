package com.tdd.bookshelf.feature

import bookshelf.composeapp.generated.resources.Res
import bookshelf.composeapp.generated.resources.ic_transparent
import bookshelf.composeapp.generated.resources.img_home
import bookshelf.composeapp.generated.resources.img_profile
import com.tdd.bookshelf.core.designsystem.Empty
import com.tdd.bookshelf.core.designsystem.Home
import com.tdd.bookshelf.core.designsystem.MyPage
import com.tdd.bookshelf.core.navigation.NavRoutes
import org.jetbrains.compose.resources.DrawableResource

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
