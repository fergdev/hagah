package com.fergdev.hagah.server.di

import com.fergdev.hagah.server.api.ChatGPTApiImpl
import com.fergdev.hagah.server.database.DatabaseFactory
import com.fergdev.hagah.server.repository.DevotionalRepository
import io.ktor.client.HttpClient
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import org.ktorm.database.Database

val serverModule = DI.Module("devotionalModule") {
    bind<ChatGPTApiImpl>() with singleton { ChatGPTApiImpl(instance()) }
    bind<HttpClient>() with singleton { HttpClient() }
    bind<DevotionalRepository>() with singleton { DevotionalRepository(instance()) }
    bind<Database>() with singleton { DatabaseFactory.init() }
}