package com.fergdev.hagah.data.storage

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.fergdev.fcommon.coroutines.mapIfNull
import com.fergdev.hagah.Flavor
import com.fergdev.hagah.clean
import com.fergdev.hagah.data.DailyHagah
import com.fergdev.hagah.data.storage.HagahStorage.Error.NotFound
import com.fergdev.hagah.logger
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.extensions.plus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

internal interface HagahStorage {
    interface Error {
        data object NotFound : Error
    }

    suspend fun history(): Flow<List<DailyHagah>>
    suspend fun addDevotional(devotional: DailyHagah)
    suspend fun getHagah(id: Long): Either<NotFound, DailyHagah>
}

const val HagahDb = "hagah.db"
internal data class HagahStoreWrapper(val store: KStore<List<DailyHagah>>)

internal class HagahStorageImpl(wrapper: HagahStoreWrapper) :
    HagahStorage {
    private val store: KStore<List<DailyHagah>> = wrapper.store

    private val logger = logger("DailyDevotionalStorage")
    private val scope = CoroutineScope(EmptyCoroutineContext)

    init {
        if (Flavor.current.clean) {
            logger.d { "Cleaning storage" }
            scope.launch { store.set(emptyList()) }
        }
    }

    override suspend fun history(): Flow<List<DailyHagah>> =
        store.updates
            .mapIfNull(emptyList())
            .onEach { logger.d { it.toString() } }

    override suspend fun addDevotional(devotional: DailyHagah) = store.plus(devotional)
    override suspend fun getHagah(id: Long): Either<NotFound, DailyHagah> =
        history()
            .first()
            .firstOrNull { it.id == id }
            ?.right()
            ?: NotFound.left()
}
