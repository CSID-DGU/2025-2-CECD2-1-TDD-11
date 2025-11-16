package com.tdd.bookshelf.data.base

object EndPoints {
    object Auth {
        private const val AUTH = "api/v1/auth"
        const val EMAILLOGIN = "$AUTH/email-login"
        const val EMAILSIGNUP = "$AUTH/email-register"
    }

    object Autobiography {
        const val AUTOBIOGRAPHIES = "api/v1/autobiographies"
        const val AUTOBIOGRAPHIESDETAIL = "$AUTOBIOGRAPHIES/{autobiographyId}"
        const val AUTOBIOGRAPHIESCHAPTER = "$AUTOBIOGRAPHIES/chapters"
        const val UPDATECURRENTCHAPTER = "$AUTOBIOGRAPHIESCHAPTER/current-chapter"
    }

    object Member {
        const val MEMBER = "api/v1/members/me"
        const val PROFILE = "$MEMBER/profile"
    }

    object Interview {
        private const val INTERVIEW = "api/v1/interviews"
        const val INTERVIEWQUESTION = "$INTERVIEW/interview-questions"
        const val CREATEINTERVIEW = "$INTERVIEW/interview-chat"

        const val INTERVIEWCONVERSATION = "$INTERVIEW/{interviewId}/conversations"
        const val INTERVIEWRENEWAL = "$INTERVIEW/{interviewId}/questions/current-question"
        const val INTERVIEWQUESTIONLIST = "$INTERVIEW/{interviewId}/questions"
    }

    object Publication {
        const val PUBLICATIONS = "api/v1/publications"
        const val MYPUBLICATIONS = "$PUBLICATIONS/me"
        const val PROGRESS = "$PUBLICATIONS/{publicationId}/progress"
        const val DELETE = "$PUBLICATIONS/{bookId}"
    }
}
