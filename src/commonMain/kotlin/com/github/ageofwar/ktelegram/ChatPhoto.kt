package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatPhoto(
    @SerialName("small_file_id") val smallFileId: String,
    @SerialName("small_file_unique_id") val smallFileUniqueId: String,
    @SerialName("big_file_id") val bigFileId: String,
    @SerialName("big_file_unique_id") val bigFileUniqueId: String
)
