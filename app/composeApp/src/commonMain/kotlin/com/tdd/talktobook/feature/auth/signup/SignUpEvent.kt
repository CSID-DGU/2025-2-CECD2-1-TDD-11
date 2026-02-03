package com.tdd.talktobook.feature.auth.signup

import com.tdd.talktobook.core.ui.base.Event

sealed class SignUpEvent : Event {
    data object GoToEmailCheckPage : SignUpEvent()

    data object ShowMemberExistToast : SignUpEvent()

    data object ShowServerErrorToast : SignUpEvent()
}
