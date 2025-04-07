package com.fergdev.hagah.data.api

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
