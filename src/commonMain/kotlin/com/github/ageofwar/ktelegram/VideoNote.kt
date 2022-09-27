package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoNote(
    @SerialName("file_unique_id") override val id: String,
    @SerialName("file_id") override val fileId: String,
    @SerialName("file_size") override val fileSize: Long,
    val length: Int,
    val duration: Int,
    @SerialName("thumb") val thumbnail: PhotoSize? = null
) : TelegramFile
