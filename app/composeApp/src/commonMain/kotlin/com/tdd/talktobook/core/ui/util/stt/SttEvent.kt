package com.tdd.talktobook.core.ui.util.stt

sealed class SttEvent {
    data class Partial(val text: String) : SttEvent()

    data class Final(val text: String) : SttEvent()
}
