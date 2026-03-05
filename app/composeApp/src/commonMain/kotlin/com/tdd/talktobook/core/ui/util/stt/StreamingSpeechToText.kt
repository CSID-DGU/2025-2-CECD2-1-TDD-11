package com.tdd.talktobook.core.ui.util.stt

import co.touchlab.kermit.Logger.Companion.d
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class StreamingSpeechToText(
    private val client: StreamingStt,
    private val recorder: AudioRecorder
) : SpeechToText {

    private var partialText = ""
    override var isRunning = false
        private set

    override suspend fun start(onPartial: (String) -> Unit) {

        if (isRunning) return

        isRunning = true
        partialText = ""

        client.connect()

        CoroutineScope(Dispatchers.IO).launch {

            client.events.collect { event ->

                when (event) {

                    is SttEvent.Partial -> {
                        partialText = event.text
                        d("[stt] (client) 대화 streamingSpeechToText -> $partialText")
                        onPartial(event.text)
                    }

                    is SttEvent.Final -> {
                        partialText = event.text
                        onPartial(event.text)
                    }
                }
            }
        }

        recorder.start(client)
    }

    override suspend fun stop(): String {

        if (!isRunning) return partialText

        isRunning = false

        recorder.stop()
        client.disconnect()

        return partialText
    }
}