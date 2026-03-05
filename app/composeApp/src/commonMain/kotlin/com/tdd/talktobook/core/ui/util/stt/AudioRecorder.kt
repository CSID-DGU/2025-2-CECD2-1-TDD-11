package com.tdd.talktobook.core.ui.util.stt

expect class AudioRecorder() {
    fun start(client: StreamingStt)

    fun stop()
}