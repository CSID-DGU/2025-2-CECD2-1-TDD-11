package com.tdd.talktobook.feature.autobiographyrequest

import com.tdd.talktobook.core.ui.base.Event

sealed class AutobiographyRequestEvent : Event {
    data object GoToLogIn : AutobiographyRequestEvent()
}
