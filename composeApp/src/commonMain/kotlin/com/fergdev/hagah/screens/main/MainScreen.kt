package com.fergdev.hagah.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fergdev.hagah.screens.main.MainViewModel.State
import com.fergdev.hagah.ui.FIconButton
import hagah.generated.resources.Res
import hagah.generated.resources.app_name
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.app_name),
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                actions = {
                    FIconButton(
                        onClick = { navController.navigate("settings") },
                        vector = Icons.Default.Settings,
                    )
                }
            )
        }
    ) { padding ->

        val viewModel = koinViewModel<MainViewModel>()
        val state by viewModel.state.collectAsState()

        when (state) {
            is State.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
                    CircularProgressIndicator()
                }
            }

            is State.Success -> {
                SuccessContent(padding, state as State.Success)
            }

            is State.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    Column {
                        Text((state as State.Error).message)
                        Button(onClick = { viewModel.retry() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SuccessContent(
    padding: PaddingValues,
    success: State.Success
) {
    val devotional = success.dailyDevotional
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        contentPadding = padding,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "5 April 2025",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }

        item {
            SectionCard(title = "Verse", animationPath = "files/bible.json") {
                Text(
                    text = devotional.verse.reference,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "\"${devotional.verse.text}\"",
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic
                )
            }
        }

        item {
            SectionCard(title = "Reflection") {
                BodyText(devotional.reflection)
            }
        }

        item {
            SectionCard(title = "Prayer", animationPath = "files/prayer.json") {
                BodyText(devotional.prayer)
            }
        }
        item {
            SectionCard(title = "Call to Action", animationPath = "files/workout.json") {
                BodyText(devotional.callToAction)
            }
        }
        item {
            HorizontalPager(
                state = rememberPagerState { success.history.size },
                pageSpacing = 16.dp,
            ) {
                val item = success.history[it]
                Card(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = item.date.toString())
                        Text(text = item.verse.reference)
                        Text(text = item.verse.text)
                    }
                }
            }
        }
        item {
            var isReflecting by remember { mutableStateOf(false) }
            if (isReflecting.not()) {
                Button(onClick = { isReflecting = true }) {
                    Text("Reflection time")
                }
            } else {
                var time by remember { mutableStateOf(5 * 60) }
                LaunchedEffect(Unit) {
                    while (time > 0) {
                        delay(1000)
                        time--
                    }
                }

                Text(
                    text = "Reflect for $time seconds",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun BodyText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun SectionCard(
    title: String,
    animationPath: String = "files/heart.json",
    content: @Composable () -> Unit
) {
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes(animationPath).decodeToString()
        )
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = CenterVertically) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Image(
                    modifier = Modifier.size(48.dp),
                    painter = rememberLottiePainter(
                        composition = composition,
                        iterations = Compottie.IterateForever
                    ),
                    contentDescription = "Lottie animation"
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
