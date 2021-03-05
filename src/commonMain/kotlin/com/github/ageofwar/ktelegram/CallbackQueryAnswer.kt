package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CallbackQueryAnswer(
    val text: String? = null,
    @SerialName("show_alert") val showAlert: Boolean = false,
    val url: String? = null
)
