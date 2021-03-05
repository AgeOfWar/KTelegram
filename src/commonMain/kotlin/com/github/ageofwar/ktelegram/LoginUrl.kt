package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginUrl(
    val url: String,
    @SerialName("forward_text") val forwardText: String? = null,
    @SerialName("bot_username") val botUsername: String? = null,
    @SerialName("request_write_access") val requestWriteAccess: Boolean = false
)
