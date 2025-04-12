package com.fergdev.hagah.data

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
