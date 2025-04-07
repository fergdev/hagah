package com.fergdev.hagah.data.api

import com.fergdev.hagah.data.DailyDevotional
import com.fergdev.hagah.data.Verse
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DailyDevotionalWrapper(
    @SerialName("dailyDevotional")
    val dailyDevotionalDto: DailyDevotionalDto
)

@Serializable
data class DailyDevotionalDto(
    @SerialName("verse")
    val verseDto: VerseDto,
    @SerialName("reflection")
    val reflection: String,
    @SerialName("callToAction")
    val callToAction: String,
    @SerialName("prayer")
    val prayer: String
)

@Serializable
data class VerseDto(
    @SerialName("reference")
    val reference: String,
    @SerialName("text")
    val text: String
)

fun DailyDevotionalDto.toDomain() = DailyDevotional(
    date = Clock.System.now(),
    verse = verseDto.toDomain(),
    reflection = reflection,
    callToAction = callToAction,
    prayer = prayer
)

fun VerseDto.toDomain() = Verse(
    reference = reference,
    text = text
)
