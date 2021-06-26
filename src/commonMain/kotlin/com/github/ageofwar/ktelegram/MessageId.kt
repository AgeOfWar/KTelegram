package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageId(
    @SerialName("chat_id") val chatId: ChatId,
    @SerialName("message_id") val messageId: Long
)