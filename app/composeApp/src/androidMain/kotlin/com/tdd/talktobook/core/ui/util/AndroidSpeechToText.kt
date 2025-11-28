package com.tdd.talktobook.core.ui.util

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume

private class AndroidSpeechToText(private val app: Application) : SpeechToText {
    private var recognizer: SpeechRecognizer? = null
    private var finalResult: String = ""
    private var partialCb: ((String) -> Unit)? = null
    private var stopContinuation: ((String) -> Unit)? = null

    override var isRunning: Boolean = false
        private set

    override suspend fun start(onPartial: (String) -> Unit) {
        withContext(Dispatchers.Main) {
            if (isRunning) return@withContext

            partialCb = onPartial
            finalResult = ""
            stopContinuation = null

            val sr = SpeechRecognizer.createSpeechRecognizer(app)
            val intent =
                Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")

                    // 침묵 허용 시간
                    putExtra(
                        RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
                        10000L,
                    )
                    putExtra(
                        RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
                        10000L,
                    )
                }
            sr.setRecognitionListener(
                object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {}

                    override fun onBeginningOfSpeech() {}

                    override fun onRmsChanged(rmsdB: Float) {}

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {}

                    override fun onError(error: Int) {
//                        isRunning = false
                        finish(finalResult)
                    }

                    override fun onResults(results: Bundle) {
                        val list = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        finalResult = list?.firstOrNull().orEmpty()
                        partialCb?.invoke(finalResult)
//                        isRunning = false
                        finish(finalResult)
                    }

                    override fun onPartialResults(partialResults: Bundle) {
                        val list = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        list?.firstOrNull()?.let { partialCb?.invoke(it) }
                    }

                    override fun onEvent(
                        eventType: Int,
                        params: Bundle?,
                    ) {}
                },
            )
            recognizer = sr
            isRunning = true
            sr.startListening(intent)
        }
    }

//    override suspend fun stop(): String {
//        return withContext(Dispatchers.Main) {
//            if (!isRunning) return@withContext finalResult
//            suspendCancellableCoroutine { cont ->
//                val sr = recognizer
//                recognizer = null
//                try {
//                    sr?.stopListening()
//                } catch (_: Exception) {
//                }
//                cont.resume(finalResult)
//            }
//        }
//    }
    override suspend fun stop(): String {
        return withContext(Dispatchers.Main) {
            if (!isRunning) return@withContext finalResult
            suspendCancellableCoroutine { cont ->
                stopContinuation = { result ->
                    if (cont.isActive) cont.resume(result)
                }
                try {
                    recognizer?.stopListening()
                } catch (_: Exception) {
                    if (cont.isActive) cont.resume(finalResult)
                    stopContinuation = null
                }
            }
        }
    }

    private fun finish(result: String) {
        isRunning = false

        recognizer?.destroy()
        recognizer = null

        val cont = stopContinuation
        stopContinuation = null
        cont?.invoke(result)
    }
}

@Composable
actual fun rememberSpeechToText(): SpeechToText {
    val app = LocalContext.current.applicationContext as Application
    return remember { AndroidSpeechToText(app) }
}

actual fun saveTranscriptionText(text: String): String {
    val f = File.createTempFile("conversation_", ".txt")
    f.writeText(text)
    return f.absolutePath
}
