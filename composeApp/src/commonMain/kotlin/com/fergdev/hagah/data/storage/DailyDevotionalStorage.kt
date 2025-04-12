package com.fergdev.hagah.data.storage

import com.fergdev.fcommon.coroutines.mapIfNull
import com.fergdev.hagah.data.DailyHagah
import io.github.aakira.napier.Napier
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.extensions.plus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

interface DailyDevotionalStorage {
    suspend fun history(): Flow<List<DailyHagah>>
    suspend fun addDevotional(devotional: DailyHagah)
}

const val HagahDb = "hagah.db"

class DailyDevotionalStorageImpl(private val store: KStore<List<DailyHagah>>) :
    DailyDevotionalStorage {

    override suspend fun history(): Flow<List<DailyHagah>> =
        store.updates.mapIfNull(emptyList()).onEach {
            Napier.d(message = it.toString(), tag = "storage - history")
        }

    override suspend fun addDevotional(devotional: DailyHagah) {
        store.plus(devotional)
    }
}
