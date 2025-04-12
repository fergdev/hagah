package com.fergdev.fcommon.util

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

public fun LocalDate.formatToMonthAndYear(): String = "${month.name}, ${this.year}"

public fun LocalDate.formatToDayMonthAndYear(): String = "${this.dayOfMonth} ${month.name}, ${this.year}"

public fun LocalDate.clone(
    year: Int = this.year,
    month: Month = this.month,
    dayOfMonth: Int = this.dayOfMonth,
) = LocalDate(year = year, month = month, dayOfMonth = dayOfMonth)

public fun LocalDate.startOfMonth() = this.clone(dayOfMonth = 1)

@Suppress("unused")
public fun LocalDate.startOfWeek(firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY): LocalDate {
    val daysToSubtract = this.dayOfWeek.isoDayNumber - firstDayOfWeek.isoDayNumber
    return this.minus(daysToSubtract, DateTimeUnit.DAY)
}

public fun LocalDate.endOfWeek(lastDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY): LocalDate {
    val daysToAdd = lastDayOfWeek.isoDayNumber - this.dayOfWeek.isoDayNumber
    return this.plus(daysToAdd, DateTimeUnit.DAY)
}

public fun LocalDate.endOfMonth(): LocalDate {
    val start = startOfMonth()
    val end = start.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)
    return end
}

public fun Clock.nowDateTime() = now().toLocalDateTime(TimeZone.currentSystemDefault())
public fun Clock.nowDate() = now().toLocalDateTime(TimeZone.currentSystemDefault()).date
