package com.fergdev.hagah.data.api

import arrow.core.Either
import com.fergdev.hagah.BuildFlags
import com.fergdev.hagah.data.api.DailyDevotionalApi.Error.Network
import com.fergdev.hagah.data.api.DailyDevotionalApi.Error.Server
import com.fergev.hagah.data.dto.DailyDevotionalDto
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.io.IOException

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
    private val endpoint = "${BuildFlags.baseUrl}/daily"

    private suspend fun requestData(): DailyDevotionalDto {
        val response = client.get(endpoint) {
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }
        }
        return response.body<DailyDevotionalDto>()
    }

    override suspend fun requestHagah() =
        Either.catch { requestData() }
            .onLeft { Napier.e(tag = LogTag) { it.toString() } }
            .mapLeft {
                when (it) {
                    is IOException -> Network
                    else -> Server
                }
            }
}
