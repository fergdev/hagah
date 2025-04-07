package com.fergdev.hagah.data

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DailyDevotional(
    @SerialName("date")
    val date: Instant,
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
