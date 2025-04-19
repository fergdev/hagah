package com.fergdev.hagah.data

import com.fergev.hagah.data.dto.DailyDevotionalDto
import com.fergev.hagah.data.dto.VerseDto
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DailyHagah(
    @SerialName("index")
    val id: Long,
    @SerialName("date")
    val date: LocalDate,
    @SerialName("verse")
    val verse: Verse,
    @SerialName("reflection")
    val reflection: String,
    @SerialName("callToAction")
    val callToAction: String,
    @SerialName("prayer")
    val prayer: String
)

@Serializable
data class Verse(
    @SerialName("reference")
    val reference: String,
    @SerialName("Text")
    val text: String
)

internal fun DailyDevotionalDto.toDomain(index: Long = 0L) = DailyHagah(
    id = index,
    date = this.date,
    verse = verseDto.toDomain(),
    reflection = reflection,
    callToAction = callToAction,
    prayer = prayer,
)

internal fun VerseDto.toDomain() = Verse(
    reference = reference,
    text = text
)
