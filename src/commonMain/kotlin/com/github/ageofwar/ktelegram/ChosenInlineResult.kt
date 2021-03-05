package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChosenInlineResult(
    @SerialName("result_id") val resultId: String,
    @SerialName("from") val sender: User,
    val location: Location? = null,
    @SerialName("inline_message_id") val inlineMessageId: String? = null,
    val query: String
)
