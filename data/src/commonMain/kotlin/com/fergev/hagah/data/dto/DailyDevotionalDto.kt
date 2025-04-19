package com.fergev.hagah.data.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DailyDevotionalWrapper(
    @SerialName("dailyDevotional")
    val dailyDevotionalDto: DailyDevotionalDto
)

@Serializable
public data class DailyDevotionalDto(
    @SerialName("date")
    val date: LocalDate,
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
public data class VerseDto(
    @SerialName("reference")
    val reference: String,
    @SerialName("text")
    val text: String
)
