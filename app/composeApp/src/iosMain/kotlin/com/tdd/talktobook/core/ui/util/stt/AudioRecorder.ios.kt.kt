package com.tdd.talktobook.core.ui.util.stt

import co.touchlab.kermit.Logger.Companion.d
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.AVFAudio.AVAudioEngine

actual class AudioRecorder {
    private val engine = AVAudioEngine()

    @OptIn(ExperimentalForeignApi::class)
    actual fun start(client: StreamingStt) {
        d("[stt] (client) 대화 iosRecorder -> recorder start")

        val input = engine.inputNode
        val format = input.outputFormatForBus(0u)

        input.installTapOnBus(
            0u,
            1024u,
            format
        ) { buffer, _ ->

            val data = buffer!!.int16ChannelData!!

            val bytes = data.readBytes(2048)

            CoroutineScope(Dispatchers.Default).launch {
//                d("[stt] (client) 대화 iosRecorder -> $bytes")

                client.sendAudio(bytes)
            }
        }

        engine.startAndReturnError(null)
    }

    actual fun stop() {

        engine.stop()

        engine.inputNode.removeTapOnBus(0u)
    }
}