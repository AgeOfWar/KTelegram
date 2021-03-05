package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Voice(
    @SerialName("file_unique_id") override val id: String,
    @SerialName("file_id") override val fileId: String,
    @SerialName("file_size") override val fileSize: Int,
    val duration: Int,
    @SerialName("mime_type") val mimeType: String? = null
) : TelegramFile
