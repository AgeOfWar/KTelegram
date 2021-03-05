package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class File(
    @SerialName("file_unique_id") override val id: String,
    @SerialName("file_id") val fileId: String,
    @SerialName("file_size") val fileSize: Int? = null,
    @SerialName("file_path") val path: String? = null
) : Id<String>