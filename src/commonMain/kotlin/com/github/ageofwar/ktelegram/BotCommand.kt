package com.github.ageofwar.ktelegram

import kotlinx.serialization.Serializable

@Serializable
data class BotCommand(
    val command: String,
    val description: String
) {
    init {
        require(command.length in 1..32) { "Bot command length should be 1..32" }
        require(description.length in 3..256) { "Bot command description length should be 3..256" }
    }
}
