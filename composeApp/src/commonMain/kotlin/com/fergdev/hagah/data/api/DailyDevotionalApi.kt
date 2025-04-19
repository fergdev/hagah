package com.fergdev.hagah.data.api

import arrow.core.Either
import com.fergdev.hagah.Flavor
import com.fergdev.hagah.data.api.DailyDevotionalApi.Error.Network
import com.fergdev.hagah.data.api.DailyDevotionalApi.Error.Server
import com.fergdev.hagah.hagahUrl
import com.fergdev.hagah.logger
import com.fergev.hagah.data.dto.DailyDevotionalDto
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

internal class DailyDevotionalApiImpl(private val client: HttpClient) : DailyDevotionalApi {
    private val endpoint = "${Flavor.current.hagahUrl}/daily"
    private val logger = logger("HagahApi")

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
            .onLeft { logger.e("Error requesting hagah", it) }
            .mapLeft {
                when (it) {
                    is IOException -> Network
                    else -> Server
                }
            }
}
