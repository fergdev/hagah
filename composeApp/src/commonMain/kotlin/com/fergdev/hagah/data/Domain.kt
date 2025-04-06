package com.fergdev.hagah.data

import kotlinx.serialization.Serializable

@Serializable
data class DailyDevotionalWrapper(
    val dailyDevotional: DailyDevotional
)

@Serializable
data class DailyDevotional(
    val verse: Verse,
    val reflection: String,
    val callToAction: String,
    val prayer: String
)

@Serializable
data class Verse(
    val reference: String,
    val text: String
)
