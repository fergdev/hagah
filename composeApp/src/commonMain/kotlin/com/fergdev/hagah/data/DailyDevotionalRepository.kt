package com.fergdev.hagah.data

import arrow.core.Either
import com.fergdev.hagah.data.api.DailyDevotionalApi
import com.fergdev.hagah.data.storage.DailyDevotionalStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

interface DailyDevotionalRepository {
    suspend fun requestDailyDevotional(): Flow<Either<DailyDevotional, DailyDevotionalApi.DevotionalError>>
    suspend fun history(): Flow<List<DailyDevotional>>
}

class DailyDevotionalRepositoryImpl(
    private val dailyDevotionalApi: DailyDevotionalApi,
    private val dailyDevotionalStorage: DailyDevotionalStorage
) : DailyDevotionalRepository {
    override suspend fun history() = dailyDevotionalStorage.history()
    override suspend fun requestDailyDevotional() =
        this.dailyDevotionalApi.getData().onEach { either ->
            either.onLeft {
                dailyDevotionalStorage.addDevotional(it)
            }
        }
}
