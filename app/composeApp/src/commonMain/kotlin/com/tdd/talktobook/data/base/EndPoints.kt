package com.tdd.talktobook.data.base

object EndPoints {
    object Auth {
        private const val AUTH = "api/v2/auth"
        const val EMAILLOGIN = "$AUTH/email-login"
        const val EMAILSIGNUP = "$AUTH/email-register"
        const val EMAILVERIFY = "$AUTH/email-verify"
        const val UNREGISTER = "$AUTH/unregister"
        const val LOGOUT = "$AUTH/logout"
    }

    object Autobiography {
        const val AUTOBIOGRAPHIES = "api/v2/autobiographies"
        const val AUTOBIOGRAPHIESDETAIL = "$AUTOBIOGRAPHIES/{autobiographyId}"
        const val AUTOBIOGRAPHIESCHAPTER = "$AUTOBIOGRAPHIES/chapters"
        const val UPDATECURRENTCHAPTER = "$AUTOBIOGRAPHIESCHAPTER/current-chapter"
        const val CURRENT_PROGRESS_AUTOBIOGRAPHIES = "$AUTOBIOGRAPHIES/current"
        const val START_PROGRESS = "$AUTOBIOGRAPHIES/init"
        const val COUNT_MATERIALS = "$AUTOBIOGRAPHIESDETAIL/materials"
        const val CURRENT_INTERVIEW_PROGRESS = "$AUTOBIOGRAPHIESDETAIL/progress"
        const val CREATE_AUTOBIOGRAPHY = "$AUTOBIOGRAPHIESDETAIL/generate"
        const val SELECTED_THEME = "$AUTOBIOGRAPHIESDETAIL/theme"
    }

    object Member {
        const val MEMBER = "api/v2/members/me"
        const val PROFILE = "$MEMBER/profile"
    }

    object Interview {
        private const val INTERVIEW = "api/v2/interviews"
        private const val INTERVIEWID = "$INTERVIEW/{interviewId}"
        const val START_INTERVIEW = "$INTERVIEW/start/{autobiography_id}"
        const val CHAT_INTERVIEW = "$INTERVIEW/chat/{autobiography_id}"

        const val INTERVIEWCONVERSATION = "$INTERVIEWID/conversations"
        const val INTERVIEWRENEWAL = "$INTERVIEWID/questions/current-question"
        const val INTERVIEWQUESTIONLIST = "$INTERVIEWID/questions"
        const val INTERVIEW_SUMMARY = "$INTERVIEW/{autobiographyId}/interviews/summaries"
    }

    object Publication {
        const val PUBLICATIONS = "api/v1/publications"
        const val MYPUBLICATIONS = "$PUBLICATIONS/me"
        const val PROGRESS = "$PUBLICATIONS/{publicationId}/progress"
        const val DELETE = "$PUBLICATIONS/{bookId}"
    }
}
