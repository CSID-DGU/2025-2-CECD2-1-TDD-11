package com.tdd.talktobook.core.ui.util.stt

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import co.touchlab.kermit.Logger.Companion.d
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

actual class AudioRecorder {
    private var recorder: AudioRecord? = null
    private var running = false

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    actual fun start(client: StreamingStt) {
        d("[stt] (client) 대화 androidRecorder -> recorder start")

        val sampleRate = 16000

        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        recorder = AudioRecord(
//            MediaRecorder.AudioSource.MIC,
            MediaRecorder.AudioSource.VOICE_RECOGNITION,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        val buffer = ByteArray(640)

        recorder!!.startRecording()
        running = true

        CoroutineScope(Dispatchers.IO).launch {

            while (running) {

                val read = recorder!!.read(buffer, 0, buffer.size)

                if (read > 0) {
//                    d("[stt] (client) 대화 androidRecorder -> $read")

                    client.sendAudio(buffer.copyOf(read))
                }
            }
        }
    }

    actual fun stop() {

        running = false

        recorder?.stop()
        recorder?.release()
        recorder = null
    }
}