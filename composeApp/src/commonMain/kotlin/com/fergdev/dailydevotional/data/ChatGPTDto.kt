package com.fergdev.dailydevotional.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatGPTRequest(
    val model: String = "gpt-4",
    val messages: List<Message>
)

@Serializable
data class Message(
    val role: String = "user",
    val content: String
)

@Serializable
data class ChatGPTResponse(
    val id: String,
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: Message,
    @SerialName("finish_reason")
    val finishReason: String,
    val index: Int
)
