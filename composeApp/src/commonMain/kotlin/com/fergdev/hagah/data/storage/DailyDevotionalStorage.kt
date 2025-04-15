package com.fergdev.hagah.data.storage

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.fergdev.fcommon.coroutines.mapIfNull
import com.fergdev.hagah.data.DailyHagah
import com.fergdev.hagah.data.storage.DailyDevotionalStorage.Error.NotFound
import io.github.aakira.napier.Napier
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.extensions.plus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach

internal interface DailyDevotionalStorage {
    interface Error {
        data object NotFound : Error
    }

    suspend fun history(): Flow<List<DailyHagah>>
    suspend fun addDevotional(devotional: DailyHagah)
    suspend fun getHagah(id: Long): Either<NotFound, DailyHagah>
}

const val HagahDb = "hagah.db"
const val StorageKey = "hagah"

private const val LogTag = "Storage"

internal class DailyDevotionalStorageImpl(private val store: KStore<List<DailyHagah>>) :
    DailyDevotionalStorage {

    override suspend fun history(): Flow<List<DailyHagah>> =
        store.updates
            .mapIfNull(emptyList())
            .onEach { Napier.d(message = it.toString(), tag = LogTag) }

    override suspend fun addDevotional(devotional: DailyHagah) = store.plus(devotional)
    override suspend fun getHagah(id: Long): Either<NotFound, DailyHagah> =
        history()
            .first()
            .firstOrNull { it.id == id }
            ?.right()
            ?: NotFound.left()
}
