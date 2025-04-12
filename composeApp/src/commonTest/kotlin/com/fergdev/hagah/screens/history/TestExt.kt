package com.fergdev.hagah.screens.history

import app.cash.turbine.ReceiveTurbine
import io.kotest.assertions.withClue
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.scopes.FreeSpecContainerScope
import io.kotest.core.spec.style.scopes.FreeSpecTerminalScope
import io.kotest.core.test.TestScope
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineDispatcher

@OptIn(ExperimentalKotest::class)
fun Spec.asViewModel() {
    coroutineTestScope = true
    coroutineDebugProbes = false
    assertSoftly = true
}

fun FreeSpecContainerScope.idle() = testCoroutineScheduler.advanceUntilIdle()

fun FreeSpecTerminalScope.idle() = testCoroutineScheduler.advanceUntilIdle()

val TestScope.testDispatcher: CoroutineDispatcher
    get() = coroutineContext[CoroutineDispatcher]!!

suspend fun <T> ReceiveTurbine<T>.awaitAndExpectNoMore(): T {
    val item = awaitItem()
    expectNoEvents()
    return item
}

infix fun <T> List<T>.shouldBeList(other: List<T>) {
    for (i in this.indices) {
        withClue("List differs at $i, ${this[i]} != ${other[i]}") {
            this[i] shouldBe other[i]
        }
    }
}
