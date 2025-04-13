package com.fergdev.hagah.data

import arrow.core.Either
import arrow.core.left
import com.fergdev.hagah.data.MockHagah.generateMockList
import com.fergdev.hagah.data.MockHagah.generateRandomDailyDevotional
import com.fergdev.hagah.data.api.DailyDevotionalApi
import com.fergdev.hagah.data.storage.DailyDevotionalStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit.Companion.DAY
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

internal interface DataRepository {
    suspend fun requestDailyDevotional(): Flow<Either<DailyHagah, DailyDevotionalApi.ApiError>>
    suspend fun history(): Flow<List<DailyHagah>>
    suspend fun setDevotional(id: Long)
    val lookBackDevotional: Flow<DailyHagah>
}

internal abstract class BaseDataRepository : DataRepository {
    private val _lookBackDevotional = MutableSharedFlow<DailyHagah>()
    override val lookBackDevotional = _lookBackDevotional
    override suspend fun setDevotional(id: Long) {
        _lookBackDevotional.emit(history().first().first { it.id == id })
    }
}

internal class DataRepositoryImpl(
    private val dailyDevotionalApi: DailyDevotionalApi,
    private val dailyDevotionalStorage: DailyDevotionalStorage
) : BaseDataRepository() {
    override suspend fun history() = dailyDevotionalStorage.history()
    override suspend fun requestDailyDevotional() =
        this.dailyDevotionalApi.getData().onEach { either ->
            either.onLeft {
                dailyDevotionalStorage.addDevotional(it)
            }
        }
}

internal class DataRepositoryMockImpl : BaseDataRepository() {
    private val mockDelay = 3000L
    private val devotionals = generateMockList(100)
    override suspend fun history() = flowOf(devotionals)
    override suspend fun requestDailyDevotional(): Flow<Either<DailyHagah, DailyDevotionalApi.ApiError>> {
        delay(mockDelay)
        return flowOf(generateRandomDailyDevotional().left())
    }
}

private object MockHagah {
    private val random = Random(Clock.System.now().toEpochMilliseconds())

    fun generateRandomDailyDevotional(
        index: Long = random.nextLong(),
        now: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    ) =
        DailyHagah(
            id = index,
            date = now,
            verse = generateRandomVerse(),
            reflection = generateRandomReflection(),
            callToAction = generateRandomCallToAction(),
            prayer = generateRandomPrayer()
        )

    private val books = listOf(
        "Genesis",
        "Exodus",
        "Leviticus",
        "Numbers",
        "Deuteronomy",
        "Matthew",
        "Mark",
        "Luke",
        "John"
    )

    fun generateRandomVerse(): Verse {
        val book = books.random(random)
        val chapter = random.nextInt(1, 51)
        val verseNumber = random.nextInt(1, 31)
        val reference = "$book $chapter:$verseNumber"
        val text = generateRandomText(random.nextInt(10, 30)) // Random length for verse text
        return Verse(reference = reference, text = text)
    }

    private const val REFLECTION_FROM = 50
    private const val REFLECTION_UNTIL = 150
    fun generateRandomReflection(): String =
        generateRandomText(random.nextInt(REFLECTION_FROM, REFLECTION_UNTIL))

    private const val CTA_FROM = 20
    private const val CTA_UNTIL = 50
    fun generateRandomCallToAction(): String =
        generateRandomText(random.nextInt(CTA_FROM, CTA_UNTIL))

    private const val PRAYER_FROM = 30
    private const val PRAYER_UNTIL = 100
    fun generateRandomPrayer(): String =
        generateRandomText(random.nextInt(PRAYER_FROM, PRAYER_UNTIL))

    private fun generateRandomText(wordCount: Int): String {
        val words = listOf(
            "Lorem",
            "ipsum",
            "dolor",
            "sit",
            "amet",
            "consectetur",
            "adipiscing",
            "elit",
            "sed",
            "do",
            "eiusmod",
            "tempor",
            "incididunt",
            "ut",
            "labore",
            "et",
            "dolore",
            "magna",
            "aliqua",
            "Ut",
            "enim",
            "ad",
            "minim",
            "veniam",
            "quis",
            "nostrud",
            "exercitation",
            "ullamco",
            "laboris",
            "nisi",
            "aliquip",
            "ex",
            "ea",
            "commodo",
            "consequat",
            "Duis",
            "aute",
            "irure",
            "in",
            "reprehenderit",
            "voluptate",
            "velit",
            "esse",
            "cillum",
            "eu",
            "fugiat",
            "nulla",
            "pariatur",
            "Excepteur",
            "sint",
            "occaecat",
            "cupidatat",
            "non",
            "proident",
            "sunt",
            "culpa",
            "qui",
            "officia",
            "deserunt",
            "mollit",
            "anim",
            "id",
            "est",
            "laborum",
            "Deus",
            "Pater",
            "Jesus",
            "Christus"
        )

        return buildString {
            repeat(wordCount) {
                append(words.random(random))
                if (it < wordCount - 1) {
                    append(" ")
                }
            }
        }
    }

    fun generateMockList(count: Int): List<DailyHagah> {
        var now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        return List(count) {
            generateRandomDailyDevotional(now = now).apply { now = now.minus(2, DAY) }
        }
    }
}
