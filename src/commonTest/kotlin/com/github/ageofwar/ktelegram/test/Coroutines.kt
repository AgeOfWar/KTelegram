package com.github.ageofwar.ktelegram.test

import kotlinx.coroutines.CoroutineScope

expect fun <T> runTestBlocking(block: suspend CoroutineScope.() -> T): T