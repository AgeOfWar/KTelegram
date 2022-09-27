package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Video(
    @SerialName("file_id") override val fileId: String,
    @SerialName("file_unique_id") override val id: String,
    val width: Int,
    val height: Int,
    val duration: Int,
    @SerialName("thumb") val thumbnail: PhotoSize? = null,
    @SerialName("file_name") val fileName: String? = null,
    @SerialName("mime_type") val mimeType: String? = null,
    @SerialName("file_size") override val fileSize: Long
) : TelegramFile
