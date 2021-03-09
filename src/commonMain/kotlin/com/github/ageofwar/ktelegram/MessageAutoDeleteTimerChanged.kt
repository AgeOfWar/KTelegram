package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageAutoDeleteTimerChanged(
    @SerialName("message_auto_delete_time") val messageAutoDeleteTime: Int
)