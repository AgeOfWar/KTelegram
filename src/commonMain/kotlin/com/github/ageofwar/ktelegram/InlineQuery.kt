package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InlineQuery(
    override val id: String,
    @SerialName("from") val sender: User,
    val location: Location? = null,
    val query: String,
    val offset: String
) : Id<String>