package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfilePhotos(
    @SerialName("total_count") val totalCount: Int,
    val photos: List<List<PhotoSize>>
)
