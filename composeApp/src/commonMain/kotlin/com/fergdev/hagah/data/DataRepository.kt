package com.fergdev.hagah.data

import arrow.core.Either
import arrow.core.right
import com.fergdev.fcommon.util.nowDate
import com.fergdev.hagah.data.DataRepository.Error
import com.fergdev.hagah.data.DataRepository.Error.NotFound
import com.fergdev.hagah.data.MockHagah.generateMockList
import com.fergdev.hagah.data.api.DailyDevotionalApi
import com.fergdev.hagah.data.storage.DailyDevotionalStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit.Companion.DAY
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

internal interface DataRepository {
    sealed interface Error {
        data object NotFound : Error
        data object Network : Error
        data object Server : Error
    }

    suspend fun requestDailyHagah(): Either<Error, DailyHagah>
    suspend fun history(): Flow<List<DailyHagah>>
    suspend fun setLookBackHagah(id: Long)
    val lookBackHagah: Flow<Either<Error, DailyHagah>>
}

internal class DataRepositoryImpl(
    private val dailyDevotionalApi: DailyDevotionalApi,
    private val dailyDevotionalStorage: DailyDevotionalStorage,
    private val clock: Clock
) : DataRepository {
    private val _lookBackDevotional = MutableSharedFlow<Either<NotFound, DailyHagah>>()
    override val lookBackHagah = _lookBackDevotional

    override suspend fun history() = dailyDevotionalStorage.history()

    override suspend fun setLookBackHagah(id: Long) {
        _lookBackDevotional.emit(
            dailyDevotionalStorage.getHagah(id).mapLeft { NotFound }
        )
    }

    override suspend fun requestDailyHagah() =
        dailyDevotionalStorage.history()
            .first()
            .firstOrNull { it.date == clock.nowDate() }
            ?.right()
            ?: dailyDevotionalApi.requestHagah()
                .map { it.toDomain() }
                .onRight { dailyDevotionalStorage.addDevotional(it) }
                .mapLeft {
                    when (it) {
                        is DailyDevotionalApi.Error.Server -> Error.Server
                        is DailyDevotionalApi.Error.Network -> Error.Network
                    }
                }
}

internal class DataRepositoryMockImpl : DataRepository {
    private val _lookBackDevotional = MutableSharedFlow<Either<NotFound, DailyHagah>>()
    override val lookBackHagah = _lookBackDevotional
    private val mockDelay = 3000L
    private val devotionals = generateMockList(100)
    override suspend fun history() = flowOf(devotionals)
    override suspend fun setLookBackHagah(id: Long) {
        _lookBackDevotional.emit(devotionals.first { it.id == id }.right())
    }

    override suspend fun requestDailyHagah(): Either<Error, DailyHagah> {
        delay(mockDelay)
        return devotionals.first().right()
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
            prayer = generateRandomPrayer(),
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
