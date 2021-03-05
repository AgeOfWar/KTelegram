package com.github.ageofwar.ktelegram

import kotlinx.serialization.Serializable

@Serializable
data class Dice(
    val emoji: String,
    val value: Int
)
