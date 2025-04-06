package com.fergdev.dailydevotional.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

interface DailyDevotionalStorage {
    suspend fun saveObjects(newObjects: List<DailyDevotional>)

    fun getObjectById(objectId: Int): Flow<DailyDevotional?>

    fun getObjects(): Flow<List<DailyDevotional>>
}

class InMemoryStorage : DailyDevotionalStorage {
    private val storedObjects = MutableStateFlow(emptyList<DailyDevotional>())

    override suspend fun saveObjects(newObjects: List<DailyDevotional>) {
        storedObjects.value = newObjects
    }

    override fun getObjectById(objectId: Int) = flowOf<DailyDevotional>()
//        return storedObjects.map { objects ->
//            objects.find { it.objectID == objectId }
//        }
//    }

    override fun getObjects(): Flow<List<DailyDevotional>> = storedObjects
}
