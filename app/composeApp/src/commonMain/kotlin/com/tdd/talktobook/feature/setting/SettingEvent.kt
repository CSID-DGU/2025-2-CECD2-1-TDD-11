package com.tdd.talktobook.feature.setting

import com.tdd.talktobook.core.ui.base.Event

sealed class SettingEvent: Event {
    data object GoToLogInPage: SettingEvent()
}