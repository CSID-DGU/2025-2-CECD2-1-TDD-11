package com.tdd.talktobook.core.ui.util.stt

import co.touchlab.kermit.Logger.Companion.d
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class StreamingSpeechToText(
    private val client: StreamingStt,
    private val recorder: AudioRecorder,
) : SpeechToText {
    private var partialText = ""
    private var finalText = ""

    override var isRunning = false
        private set

    override suspend fun start(
        onPartial: (String) -> Unit,
        onFinal: (String) -> Unit,
    ) {
        if (isRunning) return

        isRunning = true
        partialText = ""
        finalText = ""

        client.connect()

        CoroutineScope(Dispatchers.IO).launch {
            client.events.collect { event ->
                when (event) {
                    is SttEvent.Partial -> {
                        partialText = event.text
                        d("[stt] (client) partial -> ${event.text}")
                        onPartial(event.text)
                    }

                    is SttEvent.Final -> {
                        finalText = event.text
                        partialText = event.text
                        d("[stt] (client) final -> ${event.text}")
                        onFinal(event.text)
                    }
                }
            }
        }

        recorder.start(client)
    }

    override suspend fun stop(): String {
        if (!isRunning) return finalText.ifBlank { partialText }

        isRunning = false
        recorder.stop()
        client.disconnect()

        return finalText.ifBlank { partialText }
    }
}
