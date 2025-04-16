package com.fergdev.hagah.server.util

import arrow.core.Either

inline fun <L, R, L2> Either<L, R>.flatMapLeft(transform: (L) -> Either<L2, R>): Either<L2, R> =
    when (this) {
        is Either.Left -> transform(value)
        is Either.Right -> this
    }
