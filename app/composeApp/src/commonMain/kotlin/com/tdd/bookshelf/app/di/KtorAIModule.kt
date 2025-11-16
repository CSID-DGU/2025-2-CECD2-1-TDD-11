package com.tdd.bookshelf.app.di

import co.touchlab.kermit.Logger.Companion.d
import com.tdd.bookshelf.BuildKonfig
import com.tdd.bookshelf.data.dataStore.LocalDataStore
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan
object KtorAIModule {
    private const val HEADER_VALUE = "utf-8"

    @Single
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

    @Single
    @BookShelfKtorAI
    fun provideAIHttpClient(
        json: Json,
        localDataStore: LocalDataStore,
    ): HttpClient =
        HttpClient {
            install(ContentNegotiation) {
                json(json)
                register(
                    ContentType.Text.Plain,
                    KotlinxSerializationConverter(json),
                )
            }

            install(HttpTimeout) {
                connectTimeoutMillis = 10_000
                socketTimeoutMillis = 10_000
                requestTimeoutMillis = 10_000
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

            install(Auth) {
                val baseHost = Url(BuildKonfig.AI_URL).host

                bearer {
                    loadTokens {
                        val token = localDataStore.accessToken.firstOrNull()

                        token?.let {
                            BearerTokens(
                                accessToken = it,
                                refreshToken = "",
                            )
                        }
                    }

                    sendWithoutRequest { request ->
                        request.url.host == baseHost
                    }
                }
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                headers.append(HttpHeaders.AcceptCharset, HEADER_VALUE)
            }
        }

    @Single
    @BookShelfKtorAI
    fun provideAIKtorfit(
        @BookShelfKtorAI httpClient: HttpClient,
    ): Ktorfit =
        Ktorfit.Builder()
            .baseUrl(BuildKonfig.AI_URL)
            .httpClient(client = httpClient)
            .build()
}
