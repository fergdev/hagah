package com.fergdev.hagah.server

import com.fergdev.hagah.server.api.ChatGPTApi
import com.fergdev.hagah.server.api.toDailyDevotionalDto
import com.fergdev.hagah.server.di.serverModule
import com.fergdev.hagah.server.repository.DevotionalRepository
import com.fergdev.hagah.server.util.flatMapLeft
import com.fergdev.hagah.server.util.nowDate
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import org.kodein.di.ktor.di
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private val Port = System.getenv("PORT")?.toIntOrNull() ?: 8080
private val Host = System.getenv("HOST") ?: "0.0.0.0"

fun main() {
    embeddedServer(
        factory = Netty,
        host = Host,
        port = Port,
        module = Application::module
    ).start(wait = true)
}

@OptIn(ExperimentalTime::class)
fun Application.module() {
    install(ContentNegotiation) { json() }
    di { import(serverModule) }

    routing {
        get("/daily") {
            val repo by call.closestDI().instance<DevotionalRepository>()
            repo.getByDate(Clock.System.nowDate()).flatMapLeft {
                val api by call.closestDI().instance<ChatGPTApi>()
                api.requestHagah()
                    .map { it.toDailyDevotionalDto() }
                    .onRight { repo.insert(it) }
            }.fold(ifLeft = {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = "Something wrong"
                )
            }, ifRight = {
                call.respond(status = HttpStatusCode.OK, message = it)
            })
        }
    }
}
