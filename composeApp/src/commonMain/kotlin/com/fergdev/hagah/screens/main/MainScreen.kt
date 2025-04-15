package com.fergdev.hagah.screens.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fergdev.fcommon.ui.Spacer
import com.fergdev.fcommon.ui.TypewriteText
import com.fergdev.fcommon.ui.blockClicks
import com.fergdev.fcommon.ui.widgets.FiveWaySwipeableScreenScope
import com.fergdev.fcommon.ui.widgets.Screen.MAIN
import com.fergdev.fcommon.util.next
import com.fergdev.hagah.screens.main.MainViewModel.State.Error
import com.fergdev.hagah.screens.main.MainViewModel.State.Loading
import com.fergdev.hagah.screens.main.MainViewModel.State.Success
import com.fergdev.hagah.screens.main.SuccessCards.CallToAction
import com.fergdev.hagah.screens.main.SuccessCards.Date
import com.fergdev.hagah.screens.main.SuccessCards.GoodBye
import com.fergdev.hagah.screens.main.SuccessCards.Meditation
import com.fergdev.hagah.screens.main.SuccessCards.Prayer
import com.fergdev.hagah.screens.main.SuccessCards.Reflection
import com.fergdev.hagah.screens.main.SuccessCards.Verse
import com.fergdev.hagah.screens.settings.main.timeFormatted
import com.fergdev.hagah.ui.HCard
import com.fergdev.hagah.ui.PulsingText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FiveWaySwipeableScreenScope.MainScreen(
    modifier: Modifier = Modifier
) {
    val viewModel = koinViewModel<MainViewModel>()

    Box(modifier = modifier, contentAlignment = Center) {
        val state by viewModel.state.collectAsState()
        when (val it = state) {
            is Loading -> LoadingContent()
            is Success -> SuccessContent(it)
            is Error -> ErrorContent(message = it.message, onClick = viewModel::retry)
        }
    }
}

@Composable
private fun ErrorContent(message: String, onClick: () -> Unit) {
    HCard(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = CenterHorizontally
    ) {
        Text(message)
        Spacer(height = 8.dp)
        Text("Retry")
    }
}

@Composable
private fun LoadingContent() {
    HCard(horizontalAlignment = CenterHorizontally) {
        PulsingText(text = "Loading Hagah")
    }
}

@Composable
internal fun MeditationTimer(
    totalTimeMillis: Long,
    modifier: Modifier = Modifier,
    onFinish: () -> Unit = {}
) {
    var timeLeftSeconds by remember { mutableStateOf(totalTimeMillis) }
    val progress by remember(timeLeftSeconds) {
        derivedStateOf { timeLeftSeconds / totalTimeMillis.toFloat() }
    }

    // Countdown logic
    LaunchedEffect(Unit) {
        while (timeLeftSeconds > 0L) {
            delay(1000L)
            timeLeftSeconds -= 1L
        }
        onFinish()
    }

    Box(
        contentAlignment = Center,
        modifier = modifier.blockClicks()
    ) {
        Canvas(modifier = Modifier.size(220.dp).padding(24.dp)) {
            // Background ring
            drawCircle(color = Color.White.copy(alpha = 0.1f), style = Stroke(width = 10f))
            // Progress ring
            drawArc(
                color = Color.White.copy(alpha = 0.9f),
                startAngle = -90f,
                sweepAngle = 360 * progress,
                useCenter = false,
                style = Stroke(width = 10f, cap = StrokeCap.Round)
            )
        }

        Text(
            text = timeLeftSeconds.timeFormatted(),
            color = Color.White,
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

private enum class SuccessCards {
    Date,
    Verse,
    Reflection,
    Prayer,
    CallToAction,
    Meditation,
    GoodBye
}

private const val SuccessContentAnimationDuration = 300

@Composable
private fun FiveWaySwipeableScreenScope.SuccessContent(success: Success) {
    key(success.dailyHagah) {
        LaunchedEffect(success.dailyHagah) { navigate(MAIN) }
        var currentCard by remember { mutableStateOf(Date) }
        val coroutineScope = rememberCoroutineScope()
        val next: (immediate: Boolean) -> Unit = remember(success) {
            {
                    immediate ->
                coroutineScope.launch {
                    if (!immediate) {
                        delay(3000)
                    }
                    currentCard = currentCard.next()
                }
            }
        }
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Center) {
            AnimatedContent(
                targetState = currentCard,
                transitionSpec = {
                    fadeIn(tween(SuccessContentAnimationDuration)) togetherWith
                        fadeOut(tween(SuccessContentAnimationDuration))
                }
            ) {
                when (it) {
                    Date -> HCard {
                        TypewriteText(
                            text = success.date,
                            style = MaterialTheme.typography.titleLarge,
                            textColor = Color.White,
                            onTypingFinish = { next(false) }
                        )
                    }

                    Verse -> {
                        SectionCard(
                            title = "Verse",
                            subtitle = success.dailyHagah.verse.reference,
                            message = "\"${success.dailyHagah.verse.text}\"",
                            onNext = next
                        )
                    }

                    Reflection -> SectionCard(
                        title = "Reflection",
                        message = success.dailyHagah.reflection,
                        onNext = next
                    )

                    Prayer -> SectionCard(
                        title = "Prayer",
                        message = success.dailyHagah.prayer,
                        onNext = next
                    )

                    CallToAction -> SectionCard(
                        title = "Call to Action",
                        message = success.dailyHagah.callToAction,
                        onNext = next
                    )

                    Meditation -> MeditationTimer(
                        totalTimeMillis = success.meditationTime,
                        onFinish = { next(false) }
                    )

                    GoodBye -> {
                        TypewriteText(
                            modifier = Modifier.padding(16.dp),
                            text = "May God bless you!",
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    subtitle: String? = null,
    message: String? = null,
    onNext: (immediate: Boolean) -> Unit = {}
) {
    HCard(modifier = Modifier.fillMaxWidth().clickable { onNext(true) }) {
        Text(
            textAlign = TextAlign.Center,
            text = title,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        subtitle?.let {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        message?.let {
            TypewriteText(text = message, style = MaterialTheme.typography.bodyLarge) {
                onNext(false)
            }
        }
    }
}
