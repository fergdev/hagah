package com.fergdev.hagah.data.api

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.fergdev.hagah.BuildFlags
import com.fergdev.hagah.data.DailyHagah
import com.fergdev.hagah.data.api.DailyDevotionalApi.DevotionalError
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.io.IOException
import kotlinx.serialization.json.Json

interface DailyDevotionalApi {
    suspend fun getData(): Flow<Either<DailyHagah, DevotionalError>>

    sealed class DevotionalError {
        data class Network(val message: String) : DevotionalError()
        data class Other(val cause: String) : DevotionalError()
    }
}

class DailyDevotionalApiImpl(private val client: HttpClient) : DailyDevotionalApi {
    private val chatGptUrl = "https://api.openai.com/v1/chat/completions"

    private val prompt = """
        Provide a daily devotional json object with no key,
        including a 'verse' (object with 'reference' and 'text'),
        a 'reflection', a 'callToAction', and 'prayer'. Make it  catholic.
    """.trimIndent()

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getData(): Flow<Either<DailyHagah, DevotionalError>> {
        try {
            val req = ChatGPTRequest(messages = listOf(Message(content = prompt)))
            val response = client.post(chatGptUrl) {
                headers {
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    append(
                        "Authorization",
                        BuildFlags.apiKey
                    )
                }
                setBody(req)
            }
            val body = response.body<String>()
            val firstChoice = json.decodeFromString<ChatGPTResponse>(body).choices.first()
            try {
                return flowOf(
                    json
                        .decodeFromString<DailyDevotionalDto>(firstChoice.message.content)
                        .toDomain()
                        .left()
                )
            } catch (t: IllegalArgumentException) {
                Napier.e("Error parsing JSON", t, "Api")
            }
            try {
                return flowOf(
                    json
                        .decodeFromString<DailyDevotionalWrapper>(
                            firstChoice.message.content
                        )
                        .dailyDevotionalDto
                        .toDomain()
                        .left()
                )
            } catch (t: IllegalArgumentException) {
                Napier.e("Error parsing JSON", t, "Api")
                return flowOf(DevotionalError.Network("Network Error").right())
            }
        } catch (ioException: IOException) {
            return flowOf(DevotionalError.Network(ioException.message ?: "IO exception").right())
        } catch (illegalArgumentException: IllegalArgumentException) {
            return flowOf(
                DevotionalError.Other(
                    illegalArgumentException.message ?: "illegal argument exception"
                ).right()
            )
        }
    }
}
