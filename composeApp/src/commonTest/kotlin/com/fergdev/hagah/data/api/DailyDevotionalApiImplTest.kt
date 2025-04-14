package com.fergdev.hagah.data.api

import com.fergdev.hagah.data.api.DailyDevotionalApi.Error.Server
import com.fergdev.hagah.loadTestResource
import com.fergdev.hagah.shouldBeLeft
import com.fergdev.hagah.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.io.IOException
import kotlinx.serialization.json.Json

private val SuccessDto = DailyDevotionalDto(
    verseDto = VerseDto(
        reference = "John 13:34",
        text = "A new command I give you: Love one another. As I have loved you, so you must love one another."
    ),
    reflection = "In today's verse, Jesus gives us one of the most profound commandments - to love one another just as He loved us. Love is such a powerful force, it can heal, transform, and bring about the most awe-inspiring changes. Yet in the world today, we often find it hard to genuinely love our neighbours. Let's use today as an opportunity to reflect on how we can grow in love and bring Jesus's commandment to life in our actions.",
    callToAction = "Take a moment to consider the people around you. Could be your family, your friends, your colleagues or even strangers you pass on the street. Engage in a deliberate act of kindness today, it could be as simple as a smile, a kind word or a selfless act of service. By doing so, you are loving others as Christ loved you.",
    prayer = "Lord Jesus, thank You for Your great love for us. Help us to understand and appreciate that love so that we may extend it to those around us. Grant us a kind and compassionate heart, that we may look past differences and embrace everyone with open arms. Teach us to love as You have loved us. Amen."
)

@OptIn(ExperimentalCoroutinesApi::class)
class DailyDevotionalApiImplTest : FreeSpec({
    val initApi: (resource: String) -> DailyDevotionalApi = { resource ->
        val data = loadTestResource(resource)
        val mockEngine = MockEngine {
            respond(
                content = data,
                status = HttpStatusCode.OK,
                headers = headersOf(
                    HttpHeaders.ContentType,
                    ContentType.Application.Json.toString()
                )
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        DailyDevotionalApiImpl(client)
    }

    "requestHagah() should return DailyDevotionalDto on success" {
        initApi("chatgpt_success.json").requestHagah() shouldBeRight SuccessDto
    }

    "requestHagah() should return DailyDevotionalDto on success with wrapper" {
        initApi("chatgpt_success_wrapper.json").requestHagah() shouldBeRight SuccessDto
    }

    "requestHagah() should return Network error on IOException" {
        DailyDevotionalApiImpl(
            HttpClient(
                MockEngine {
                    throw IOException("Simulated network error")
                }
            )
        ).requestHagah()
            .shouldBeLeft(Server)
    }

    "requestHagah() should return Network error on Exception" {
        DailyDevotionalApiImpl(
            HttpClient(
                MockEngine {
                    throw Exception("Random error")
                }
            )
        ).requestHagah() shouldBeLeft Server
    }

    "requestHagah() should return Server error on non-200 response" {
        val mockEngine = MockEngine {
            respond(
                content = "{}",
                status = HttpStatusCode.InternalServerError,
                headers = headersOf(
                    HttpHeaders.ContentType,
                    ContentType.Application.Json.toString()
                )
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        DailyDevotionalApiImpl(client).requestHagah() shouldBeLeft Server
    }

    "requestHagah() should return Server error on invalid JSON response" {
        val client = HttpClient(
            MockEngine {
                respond(
                    content = """ { "invalid": "json" } """.trimIndent(),
                    status = HttpStatusCode.OK,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString()
                    )
                )
            }
        ) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        DailyDevotionalApiImpl(client).requestHagah() shouldBeLeft Server
    }
})
