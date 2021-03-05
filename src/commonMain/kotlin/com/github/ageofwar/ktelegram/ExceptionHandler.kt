package com.github.ageofwar.ktelegram

import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

interface ExceptionHandler {
    suspend fun handle(exception: Throwable)
}

inline fun ExceptionHandler(crossinline handler: suspend (Throwable) -> Unit) =
    object : ExceptionHandler {
        override suspend fun handle(exception: Throwable) = handler(exception)
    }

operator fun ExceptionHandler.plus(other: ExceptionHandler) = object : ExceptionHandler {
    override suspend fun handle(exception: Throwable) {
        this@plus.handle(exception)
        other.handle(exception)
    }
}

suspend fun Array<out ExceptionHandler>.handle(exception: Throwable) {
    supervisorScope {
        forEach {
            launch {
                it.handle(exception)
            }
        }
    }
}
