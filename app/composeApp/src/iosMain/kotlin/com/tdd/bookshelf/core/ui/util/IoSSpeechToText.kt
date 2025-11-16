package com.tdd.bookshelf.core.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.AVFAudio.AVAudioEngine
import platform.AVFAudio.AVAudioPCMBuffer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryRecord
import platform.AVFAudio.AVAudioSessionModeMeasurement
import platform.AVFAudio.AVAudioTime
import platform.AVFAudio.setActive
import platform.Foundation.NSLocale
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUUID
import platform.Foundation.create
import platform.Foundation.writeToFile
import platform.Speech.SFSpeechAudioBufferRecognitionRequest
import platform.Speech.SFSpeechRecognitionTask
import platform.Speech.SFSpeechRecognizer
import platform.Speech.SFSpeechRecognizerAuthorizationStatus
import kotlin.coroutines.resume

private class IOSSpeechToText : SpeechToText {
    private val audioEngine = AVAudioEngine()
    private val recognizer = SFSpeechRecognizer(locale = NSLocale(localeIdentifier = "ko-KR"))
    private var request: SFSpeechAudioBufferRecognitionRequest? = null
    private var task: SFSpeechRecognitionTask? = null
    private var finalText: String = ""
    private var partialCb: ((String) -> Unit)? = null
    override var isRunning: Boolean = false
        private set

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun start(onPartial: (String) -> Unit) {
        withContext(Dispatchers.Default) {
            if (isRunning) return@withContext
            partialCb = onPartial
            finalText = ""

            // 권한
            val auth =
                suspendCancellableCoroutine<SFSpeechRecognizerAuthorizationStatus> { cont ->
                    SFSpeechRecognizer.requestAuthorization { status ->
                        cont.resume(status)
                    }
                }
            if (auth != SFSpeechRecognizerAuthorizationStatus.SFSpeechRecognizerAuthorizationStatusAuthorized) {
                return@withContext
            }

            val session = AVAudioSession.sharedInstance()
            session.setCategory(AVAudioSessionCategoryRecord, error = null)
            session.setMode(AVAudioSessionModeMeasurement, error = null)
            session.setActive(true, error = null)

            request =
                SFSpeechAudioBufferRecognitionRequest().apply {
                    shouldReportPartialResults = true
                }

            val inputNode = audioEngine.inputNode
            val format = inputNode.outputFormatForBus(0u)
            inputNode.installTapOnBus(0u, 1024u, format) { buffer: AVAudioPCMBuffer?, _: AVAudioTime? ->
                buffer?.let { b ->
                    request?.appendAudioPCMBuffer(b)
                }
            }

            audioEngine.prepare()
            audioEngine.startAndReturnError(null)

            task =
                recognizer?.recognitionTaskWithRequest(request!!, resultHandler = { result, error ->
                    if (result != null) {
                        val text = result.bestTranscription.formattedString
                        if (result.isFinal()) {
                            finalText = text
                            stopInternal()
                        } else {
                            partialCb?.invoke(text)
                        }
                    } else if (error != null) {
                        stopInternal()
                    }
                })

            isRunning = true
        }
    }

    override suspend fun stop(): String {
        return withContext(Dispatchers.Default) {
            if (!isRunning) return@withContext finalText
            stopInternal()
            finalText
        }
    }

    private fun stopInternal() {
        try {
            audioEngine.stop()
            audioEngine.inputNode.removeTapOnBus(0u)
        } catch (_: Throwable) {
        }
        request?.endAudio()
        task?.cancel()
        request = null
        task = null
        isRunning = false
    }
}

@Composable
actual fun rememberSpeechToText(): SpeechToText = remember { IOSSpeechToText() }

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun saveTranscriptionText(text: String): String {
    val dir = NSTemporaryDirectory() as String
    val path = dir + "conversation_${NSUUID().UUIDString}.txt"
    NSString.create(string = text).writeToFile(path, atomically = true, encoding = NSUTF8StringEncoding, error = null)
    return path
}
