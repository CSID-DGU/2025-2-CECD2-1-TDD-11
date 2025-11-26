package com.tdd.talktobook.feature

import com.tdd.talktobook.core.designsystem.Empty
import com.tdd.talktobook.core.designsystem.Home
import com.tdd.talktobook.core.designsystem.Interview
import com.tdd.talktobook.core.designsystem.Publication
import com.tdd.talktobook.core.navigation.NavRoutes

enum class BottomNavType(
    val navName: String,
    val navIcon: String,
    val destination: String,
) {
    PUBLICATION(Publication, "files/ic_autobiography.svg", NavRoutes.PublicationScreen.route),
    HOME(Home, "files/ic_home.svg", NavRoutes.HomeScreen.route),
    INTERVIEW(Interview, "files/ic_interview.svg", NavRoutes.InterviewScreen.setRouteModel("")),
    DEFAULT(Empty, "drawable/ic_transparent.svg", ""),
    ;

    companion object {
        fun getBottomNavIcon(navType: BottomNavType): String =
            entries.firstOrNull { it == navType }?.navIcon ?: "drawable/ic_transparent.svg"

        fun getDestination(navType: BottomNavType): String =
            entries.firstOrNull { it == navType }?.destination ?: ""
    }
}
