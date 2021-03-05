package com.github.ageofwar.ktelegram

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

interface UpdateHandler {
    suspend fun handle(update: Update)
}

inline fun UpdateHandler(crossinline handler: suspend (Update) -> Unit) = object : UpdateHandler {
    override suspend fun handle(update: Update) = handler(update)
}

operator fun UpdateHandler.plus(other: UpdateHandler) = object : UpdateHandler {
    override suspend fun handle(update: Update) {
        this@plus.handle(update)
        other.handle(update)
    }
}

suspend fun Update.handle(
    vararg handlers: UpdateHandler,
    exceptionHandler: suspend (Throwable) -> Unit = { it.printStackTrace() }
) = supervisorScope {
    for (handler in handlers) {
        launch(CoroutineName("update handler")) {
            try {
                handler.handle(this@handle)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                exceptionHandler(e)
            }
        }
    }
}

suspend fun ReceiveChannel<Update>.handle(
    vararg handlers: UpdateHandler,
    exceptionHandler: suspend (Throwable) -> Unit = { it.printStackTrace() }
) = supervisorScope {
    for (update in this@handle) {
        for (handler in handlers) {
            launch(CoroutineName("update handler")) {
                try {
                    handler.handle(update)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Throwable) {
                    exceptionHandler(e)
                }
            }
        }
    }
}

suspend fun ReceiveChannel<Update>.handle(
    vararg handlers: UpdateHandler,
    exceptionHandlers: Array<ExceptionHandler>
) = handle(*handlers) {
    exceptionHandlers.handle(it)
}
