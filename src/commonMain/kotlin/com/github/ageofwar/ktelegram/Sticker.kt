package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sticker(
    @SerialName("file_unique_id") override val id: String,
    @SerialName("file_id") override val fileId: String,
    @SerialName("file_size") override val fileSize: Int,
    val width: Int,
    val height: Int,
    @SerialName("is_animated") val isAnimated: Boolean,
    @SerialName("thumb") val thumbnail: PhotoSize? = null,
    val emoji: String? = null,
    @SerialName("set_name") val setName: String? = null,
    @SerialName("mask_position") val maskPosition: MaskPosition? = null
) : TelegramFile

@Serializable
data class MaskPosition(
    val point: Point,
    @SerialName("x_shift") val xShift: Float,
    @SerialName("y_shift") val yShift: Float,
    val scale: Float
) {
    @Serializable
    enum class Point {
        @SerialName("forehead")
        FOREHEAD,

        @SerialName("eyes")
        EYES,

        @SerialName("mouth")
        MOUTH,

        @SerialName("chin")
        CHIN
    }
}
