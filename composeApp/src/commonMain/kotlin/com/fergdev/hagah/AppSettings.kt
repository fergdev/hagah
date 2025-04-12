package com.fergdev.hagah

import com.fergdev.hagah.AppSettingsManager.AppSettings
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

private const val Prefix = "App:"
private const val KeySeenIntro = "${Prefix}SeenFirstIntro"

private const val KeyMeditationDuration = "${Prefix}MeditationDuration"
private const val DefaultMeditationDuration = 5L * 60L * 1000L

internal interface AppSettingsManager {
    data class AppSettings(
        val meditationLength: Long = DefaultMeditationDuration,
        val seenFirstIntro: Boolean = false
    )

    val settings: Flow<AppSettings>
    suspend fun setMeditationDuration(duration: Long)
    suspend fun setMeditationDuration(setFirstSeen: Boolean)
}

internal class AppSettingsMangerImpl(private val flowSettings: FlowSettings) : AppSettingsManager {
    private val meditationDuration =
        flowSettings.getLongFlow(KeyMeditationDuration, DefaultMeditationDuration)
    private val seenIntro =
        flowSettings.getBooleanFlow(KeySeenIntro, false)

    override val settings: Flow<AppSettings>
        get() = combine(meditationDuration, seenIntro) { meditationDuration, seenIntro ->
            AppSettings(
                meditationLength = meditationDuration,
                seenFirstIntro = seenIntro
            )
        }

    override suspend fun setMeditationDuration(duration: Long) =
        flowSettings.putLong(KeyMeditationDuration, duration)

    override suspend fun setMeditationDuration(setFirstSeen: Boolean) =
        flowSettings.putBoolean(KeySeenIntro, setFirstSeen)
}
