package com.tdd.bookshelf.core.navigation

sealed class NavRoutes(val route: String) {
    // LogIn Graph
    data object LogInGraph : NavRoutes("login_graph")
    data object LogInScreen : NavRoutes("login")

    // SignUp Graph
    data object SignUpGraph : NavRoutes("signup_graph")
    data object SignUpScreen : NavRoutes("signup")

    // EmailCheck Graph
    data object EmailCheckGraph: NavRoutes("email_check_graph")
    data object EmailCheckScreen: NavRoutes("email_check/{email}") {
        fun setRouteModel(email: String): String = "email_check/$email"
    }

    // Onboarding Graph
    data object OnboardingGraph : NavRoutes("splash_graph")
    data object OnboardingScreen : NavRoutes("splash")

    // Home Graph
    data object HomeGraph : NavRoutes("home_graph")
    data object HomeScreen : NavRoutes("home")

    // Past Interview Graph
    data object PastInterviewGraph : NavRoutes("past_interview_graph")
    data object PastInterviewScreen : NavRoutes("past_interview/{date}") {
        fun setRouteModel(date: String): String = "past_interview/$date"
    }

    // Interview Graph
    data object InterviewGraph : NavRoutes("interview_graph")
    data object InterviewScreen : NavRoutes("interview/{interviewId}") {
        fun setRouteModel(interviewId: Int): String = "interview/$interviewId"
    }

    // DetailChapter Graph
    data object DetailChapterGraph : NavRoutes("detail_chapter_graph")
    data object DetailChapterScreen : NavRoutes("detail_chapter/{autobiographyId}") {
        fun setRouteModel(autobiographyId: Int): String = "detail_chapter/$autobiographyId"
    }

    // MyPage Graph
    data object MyPageGraph : NavRoutes("my_page_graph")
    data object MyPageScreen : NavRoutes("my_page")

    // Publication
    data object PublicationGraph : NavRoutes("publication_graph")
    data object PublicationScreen : NavRoutes("publication")
}
