package com.tdd.talktobook.app.di

import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.BuildKonfig
import com.tdd.talktobook.data.dataStore.LocalDataStore
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan
object KtorModule {
    private const val HEADER_VALUE = "utf-8"

    @Single
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

    @Single
    @BookShelfKtor
    fun provideHttpClient(
        json: Json,
        localDataStore: LocalDataStore,
        tokenProvider: TokenProvider,
    ): HttpClient =
        HttpClient {
            expectSuccess = false

            install(ContentNegotiation) {
                json(json)
                register(
                    ContentType.Text.Plain,
                    KotlinxSerializationConverter(json),
                )
            }

            install(HttpTimeout) {
                connectTimeoutMillis = 30_000
                socketTimeoutMillis = 30_000
                requestTimeoutMillis = 30_000
            }

            install(Logging) {
                level = LogLevel.ALL
                logger =
                    object : Logger {
                        override fun log(message: String) {
                            d("[Ktor] -> $message")
                        }
                    }
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
//                contentType(ContentType.MultiPart.FormData)
                accept(ContentType.Application.Json)
                headers.append(HttpHeaders.AcceptCharset, HEADER_VALUE)

                val token = runBlocking { tokenProvider.getAccessToken() }
                if (!token.isNullOrBlank()) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }

    @Single
    @BookShelfKtor
    fun provideKtorfit(
        @BookShelfKtor httpClient: HttpClient,
    ): Ktorfit =
        Ktorfit.Builder()
            .baseUrl(BuildKonfig.BASE_URL)
            .httpClient(client = httpClient)
            .build()
}
