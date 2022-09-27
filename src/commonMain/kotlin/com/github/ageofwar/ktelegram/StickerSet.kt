package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StickerSet(
    val name: String,
    val title: String,
    @SerialName("is_animated") val isAnimated: Boolean,
    @SerialName("sticker_type") val stickerType: Type,
    val stickers: List<Sticker>,
    @SerialName("thumb") val thumbnail: PhotoSize? = null,
    @SerialName("is_video") val isVideo: Boolean = false,

) {
    val containsMasks: Boolean get() = stickerType == Type.MASK
    val containsCustomEmojis: Boolean get() = stickerType == Type.CUSTOM_EMOJI

    @Serializable
    enum class Type {
        @SerialName("regular") REGULAR,
        @SerialName("mask") MASK,
        @SerialName("custom_emoji") CUSTOM_EMOJI
    }
}
