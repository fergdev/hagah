package com.fergdev.hagah.data.api

import arrow.core.Either
import arrow.core.flatMap
import com.fergdev.hagah.BuildFlags
import com.fergdev.hagah.data.api.DailyDevotionalApi.Error.Network
import com.fergdev.hagah.data.api.DailyDevotionalApi.Error.Server
import com.fergdev.hagah.flatMapLeft
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.io.IOException
import kotlinx.serialization.json.Json

internal interface DailyDevotionalApi {
    suspend fun requestHagah(): Either<Error, DailyDevotionalDto>
    sealed class Error {
        data object Network : Error()
        data object Server : Error()
    }
}

private const val AUTHORIZATION = "Authorization"
private const val LogTag = "API"

internal class DailyDevotionalApiImpl(private val client: HttpClient) : DailyDevotionalApi {
    private val chatGptUrl = "https://api.openai.com/v1/chat/completions"

    private val prompt = """
        Provide a daily devotional json object with no key,
        including a 'verse' (object with 'reference' and 'text'),
        a 'reflection', a 'callToAction', and 'prayer'. Make it  catholic.
    """.trimIndent()

    private val json = Json { ignoreUnknownKeys = true }

    private suspend fun requestData(): String {
        val response = client.post(chatGptUrl) {
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                append(AUTHORIZATION, BuildFlags.apiKey)
            }
            setBody(ChatGPTRequest(messages = listOf(Message(content = prompt))))
        }
        return response.body<ChatGPTResponse>().choices.first().message.content
    }

    override suspend fun requestHagah() =
        Either.catch { requestData() }
            .flatMap { content ->
                Either.catch { json.decodeFromString<DailyDevotionalDto>(content) }
                    .flatMapLeft {
                        Either.catch {
                            json.decodeFromString<DailyDevotionalWrapper>(content)
                                .dailyDevotionalDto
                        }
                    }
            }
            .onLeft { Napier.e(tag = LogTag) { it.toString() } }
            .mapLeft {
                when (it) {
                    is IOException -> Network
                    else -> Server
                }
            }
}
