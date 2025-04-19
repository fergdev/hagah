package com.fergdev.hagah.server.repository

import arrow.core.Either
import arrow.core.right
import com.fergev.hagah.data.dto.DailyDevotionalDto
import com.fergev.hagah.data.dto.VerseDto
import kotlinx.datetime.LocalDate
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.entity.Entity
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.text
import org.ktorm.schema.varchar

class DevotionalRepository(private val db: Database) {
    fun getByDate(date: LocalDate): Either<Unit, DailyDevotionalDto> {
        val date = date.toString()
        return db.sequenceOf(Devotionals)
            .firstOrNull { it.date eq date }
            ?.right()
            ?.map {
                DailyDevotionalDto(
                    date = LocalDate.parse(it.date),
                    verseDto = VerseDto(reference = it.verseReference, text = it.verseText),
                    reflection = it.reflection,
                    callToAction = it.callToAction,
                    prayer = it.prayer
                )
            }
            ?: Either.Left(Unit)
    }

    fun insert(dto: DailyDevotionalDto) {
        db.insert(Devotionals) {
            set(it.date, dto.date.toString())
            set(it.verseReference, dto.verseDto.reference)
            set(it.verseText, dto.verseDto.text)
            set(it.reflection, dto.reflection)
            set(it.callToAction, dto.callToAction)
            set(it.prayer, dto.prayer)
        }
    }
}

object Devotionals : Table<Devotional>("devotionals") {
    val id = int("id").primaryKey().bindTo(Devotional::id)
    val date = varchar("date").bindTo(Devotional::date)
    val verseReference = text("verse_reference").bindTo { it.verseReference }
    val verseText = text("verse_text").bindTo { it.verseText }
    val reflection = text("reflection").bindTo { it.reflection }
    val callToAction = text("call_to_action").bindTo { it.callToAction }
    val prayer = text("prayer").bindTo { it.prayer }
}

interface Devotional : Entity<Devotional> {
    companion object : Entity.Factory<Devotional>()

    val id: Int
    var date: String
    val verseReference: String
    val verseText: String
    val reflection: String
    val callToAction: String
    val prayer: String
}
