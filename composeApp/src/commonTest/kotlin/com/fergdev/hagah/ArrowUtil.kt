package com.fergdev.hagah

import arrow.core.Either
import io.kotest.matchers.shouldBe

infix fun <L, R> Either<L, R>.shouldBeLeft(l: L) = this.fold(
    ifLeft = { it shouldBe l },
    ifRight = {
        // TODO: https://github.com/kotest/kotest/issues/3598
        true shouldBe false
    }
)

infix fun <L, R> Either<L, R>.shouldBeRight(r: R) = this.fold(
    ifLeft = {
        // TODO: https://github.com/kotest/kotest/issues/3598
        true shouldBe false
    },
    ifRight = { it shouldBe r }
)
