package com.tdd.talktobook.core.ui.util.stt

interface SpeechToText {
    suspend fun start(onPartial: (String) -> Unit = {}, onFinal: (String) -> Unit = {})

    suspend fun stop(): String

    val isRunning: Boolean
}

expect fun saveTranscriptionText(text: String): String
