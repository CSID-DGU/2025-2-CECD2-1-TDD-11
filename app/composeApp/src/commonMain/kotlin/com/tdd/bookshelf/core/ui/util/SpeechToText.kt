package com.tdd.bookshelf.core.ui.util

interface SpeechToText {
    suspend fun start(onPartial: (String) -> Unit = {})

    suspend fun stop(): String

    val isRunning: Boolean
}

expect fun saveTranscriptionText(text: String): String
