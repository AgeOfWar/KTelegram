package com.github.ageofwar.ktelegram.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

actual fun <T> runTestBlocking(block: suspend CoroutineScope.() -> T): T = runBlocking { block() }
