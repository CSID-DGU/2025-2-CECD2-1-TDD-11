package com.tdd.talktobook.feature.publication

import com.tdd.talktobook.core.ui.base.Event

sealed class PublicationEvent: Event {
    data object ShowPublicationSuccessToast: PublicationEvent()
}