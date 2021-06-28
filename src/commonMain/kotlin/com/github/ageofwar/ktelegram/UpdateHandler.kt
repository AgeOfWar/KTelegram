package com.github.ageofwar.ktelegram

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

fun interface UpdateHandler {
    suspend fun handle(update: Update)
}

operator fun UpdateHandler.plus(other: UpdateHandler) = UpdateHandler {
    this@plus.handle(it)
    other.handle(it)
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
