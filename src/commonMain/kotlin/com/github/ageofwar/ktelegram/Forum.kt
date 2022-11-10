package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForumTopic(
    @SerialName("message_thread_id") val messageThreadId: Long,
    val name: String,
    @SerialName("icon_color") val iconColor: Int,
    @SerialName("icon_custom_emoji_id") val iconCustomEmojiId: String? = null
)

@Serializable
data class ForumTopicCreated(
    val name: String,
    @SerialName("icon_color") val iconColor: Int,
    @SerialName("icon_custom_emoji_id") val iconCustomEmojiId: String? = null
)

@Serializable
object ForumTopicClosed

@Serializable
object ForumTopicReopened
