package com.fergdev.hagah.data

import com.fergdev.hagah.BuildFlags
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json

interface DailyDevotionalApi {
    suspend fun getData(): DailyDevotional
}

class DailyDevotionalApiImpl(private val client: HttpClient) : DailyDevotionalApi {
    private val chatGptUrl = "https://api.openai.com/v1/chat/completions"

    private val prompt = """
        Provide a daily devotional json object with no key,
        including a 'verse' (object with 'reference' and 'text'),
        a 'reflection', a 'callToAction', and 'prayer'. Make it  catholic.
    """.trimIndent()

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getData(): DailyDevotional {
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
            return json
                .decodeFromString<DailyDevotional>(firstChoice.message.content)
        } catch (t: IllegalArgumentException) {
            Napier.e("Error parsing JSON", t, "Api")
        }
        try {
            return json
                .decodeFromString<DailyDevotionalWrapper>(firstChoice.message.content)
                .dailyDevotional
        } catch (t: IllegalArgumentException) {
            Napier.e("Error parsing JSON", t, "Api")
            throw t
        }
    }
}
