package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Audio(
    @SerialName("file_id") override val fileId: String,
    @SerialName("file_unique_id") override val id: String,
    val duration: Int,
    val performer: String? = null,
    val title: String? = null,
    @SerialName("file_name") val fileName: String? = null,
    @SerialName("mime_type") val mimeType: String? = null,
    @SerialName("file_size") override val fileSize: Int,
    @SerialName("thumb") val thumbnail: PhotoSize? = null
) : TelegramFile
