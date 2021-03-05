package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StickerSet(
    val name: String,
    val title: String,
    @SerialName("is_animated") val isAnimated: Boolean,
    @SerialName("contains_masks") val containsMasks: Boolean,
    val stickers: List<Sticker>,
    @SerialName("thumb") val thumbnail: PhotoSize? = null
)
