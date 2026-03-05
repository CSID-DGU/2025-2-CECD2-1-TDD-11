package com.tdd.talktobook.core.ui.util.stt

import com.tdd.talktobook.data.entity.response.interview.SttResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class StreamingStt {
    private val client = HttpClient {
        install(WebSockets)
    }

    private val _events = MutableSharedFlow<SttEvent>()
    val events = _events.asSharedFlow()

    private var session: DefaultClientWebSocketSession? = null

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun connect() {
        session = client.webSocketSession("ws://0.0.0.0:8000/stt") // 10.0.2.2

        CoroutineScope(Dispatchers.IO).launch {
            for (frame in session!!.incoming) {
                if (frame is Frame.Text) {
                    val raw = frame.readText()

                    val response = json.decodeFromString<SttResponseDto>(raw)

                    when (response.type) {

                        "partial" -> {
                            _events.emit(
                                SttEvent.Partial(response.text)
                            )
                        }

                        "final" -> {
                            _events.emit(
                                SttEvent.Final(response.text)
                            )
                        }
                    }
                }
            }
        }
    }

    suspend fun sendAudio(bytes: ByteArray) {
        session?.outgoing?.send(Frame.Binary(true, bytes))
    }

    suspend fun disconnect() {
        session?.close()
    }
}