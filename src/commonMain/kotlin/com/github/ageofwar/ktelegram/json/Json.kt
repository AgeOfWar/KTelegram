package com.github.ageofwar.ktelegram.json

import kotlinx.serialization.json.Json

val json = Json {
    ignoreUnknownKeys = true
}

val prettyPrint = Json(json) {
    prettyPrint = true
}
