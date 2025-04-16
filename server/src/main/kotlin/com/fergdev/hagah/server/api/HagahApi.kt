package com.fergdev.hagah.server.api

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("ChatGPTLogger")

private const val AUTHORIZATION = "Authorization"

internal class ChatGPTApiImpl(private val client: HttpClient) {
    internal sealed interface ApiError {
        data object Server
        data object Network
    }

    private val chatGptUrl = "https://api.openai.com/v1/chat/completions"

    private val prompt = """
        Provide a 'dailyDevotional' json object,
        including a 'verse' (object with 'reference' and 'text'),
        a 'reflection', a 'callToAction', and 'prayer'. Make it  catholic.
    """.trimIndent()

    private val json = Json { ignoreUnknownKeys = true }

    private val apiKey = System.getenv("API_KEY")

    private suspend fun requestData(): String {
        val response = client.post(chatGptUrl) {
            header(AUTHORIZATION, apiKey)
            contentType(ContentType.Application.Json)
            setBody(ChatGPTRequest(messages = listOf(Message(content = prompt))))
        }
        return response.body<ChatGPTResponse>().choices.first().message.content
    }

    suspend fun requestHagah() =
        Either.catch { requestData() }
            .flatMap { content ->
                Either.catch { json.decodeFromString<ChatGPTDailyDevotionalWrapper>(content) }
            }
            .onLeft { logger.info(it.left().toString()) }
            .mapLeft {
                when (it) {
                    is IOException -> ApiError.Network
                    else -> ApiError.Server
                }
            }
}
