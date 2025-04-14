package com.fergdev.hagah

import arrow.core.Either
import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe

infix fun <L, R> Either<L, R>.shouldBeLeft(l: L) = this.fold(
    ifLeft = { it shouldBe l },
    ifRight = { fail("Expected left, but got right: $it") }
)

infix fun <L, R> Either<L, R>.shouldBeRight(r: R) = this.fold(
    ifLeft = { fail("Expected right, but got left: $it") },
    ifRight = { it shouldBe r }
)
