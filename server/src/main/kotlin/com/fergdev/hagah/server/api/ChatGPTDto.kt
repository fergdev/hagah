package com.fergdev.hagah.server.api

import com.fergev.hagah.data.dto.DailyDevotionalDto
import com.fergev.hagah.data.dto.VerseDto
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ChatGPTRequest(
    val model: String = "gpt-4",
    val messages: List<Message>
)

@Serializable
internal data class Message(
    val role: String = "user",
    val content: String
)

@Serializable
internal data class ChatGPTResponse(
    val id: String,
    val choices: List<Choice>
)

@Serializable
internal data class Choice(
    val message: Message,
    @SerialName("finish_reason")
    val finishReason: String,
    val index: Int
)

@Serializable
internal data class ChatGPTDailyDevotionalWrapper(
    @SerialName("dailyDevotional")
    val dailyDevotionalDto: ChatGPTDailyDevotionalDto
)

@Serializable
internal data class ChatGPTDailyDevotionalDto(
    @SerialName("verse")
    val verseDto: ChatGPTVerseDto,
    @SerialName("reflection")
    val reflection: String,
    @SerialName("callToAction")
    val callToAction: String,
    @SerialName("prayer")
    val prayer: String
)

@Serializable
internal data class ChatGPTVerseDto(
    @SerialName("reference")
    val reference: String,
    @SerialName("text")
    val text: String
)

internal fun ChatGPTDailyDevotionalWrapper.toDailyDevotionalDto(): DailyDevotionalDto =
    DailyDevotionalDto(
        date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
        verseDto = dailyDevotionalDto.verseDto.toVerseDto(),
        reflection = dailyDevotionalDto.reflection,
        callToAction = dailyDevotionalDto.callToAction,
        prayer = dailyDevotionalDto.prayer
    )

private fun ChatGPTVerseDto.toVerseDto() =
    VerseDto(
        reference = reference,
        text = text
    )
