package com.github.ageofwar.ktelegram

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.SendChannel
import kotlinx.serialization.SerializationException
import kotlin.reflect.KClass

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun getPreviousUpdates(
    api: TelegramApi,
    channel: SendChannel<Update>,
    allowedUpdates: Array<KClass<out Update>> = emptyArray(),
    exceptionHandler: suspend (Throwable) -> Unit = { it.printStackTrace() }
): Long? {
    var offset: Long? = null
    while (!channel.isClosedForSend) {
        try {
            val updates = api.getUpdates(offset, allowedUpdates = allowedUpdates)
            if (updates.isEmpty()) break
            updates.forEach {
                channel.send(it)
                offset = it.id + 1
            }
        } catch (e: CancellationException) {
            api.getUnknownUpdates(offset, allowedUpdates = allowedUpdates)
            throw e
        } catch (e: ClosedSendChannelException) {
            break
        } catch (e: SerializationException) {
            exceptionHandler(e)
            offset = discardPreviousUpdates(api)
        } catch (e: Throwable) {
            exceptionHandler(e)
        }
    }
    api.getUnknownUpdates(offset)
    return offset
}

suspend fun getPreviousUpdates(
    api: TelegramApi,
    channel: SendChannel<Update>,
    allowedUpdates: Array<KClass<out Update>> = emptyArray(),
    exceptionHandlers: Array<ExceptionHandler>
) = getPreviousUpdates(api, channel, allowedUpdates) {
    exceptionHandlers.handle(it)
}

suspend fun discardPreviousUpdates(api: TelegramApi): Long? {
    val lastUpdate = api.getUnknownUpdates(offset = -1).singleOrNull()
    if (lastUpdate != null) {
        val offset = lastUpdate.id + 1
        api.getUnknownUpdates(offset)
        return offset
    }
    return null
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun getUpdates(
    api: TelegramApi,
    channel: SendChannel<Update>,
    offset: Long? = null,
    backOff: (Long) -> Long = { _ -> 50L },
    allowedUpdates: Array<KClass<out Update>> = emptyArray(),
    exceptionHandler: suspend (Throwable) -> Unit = { it.printStackTrace() }
) {
    @Suppress("NAME_SHADOWING")
    var offset = offset
    var attempts = 0L
    while (!channel.isClosedForSend) {
        try {
            val updates = api.getUpdates(offset, allowedUpdates = allowedUpdates)
            if (updates.isNotEmpty()) {
                updates.forEach {
                    channel.send(it)
                    offset = it.id + 1
                }
                attempts = 0L
            } else {
                attempts++
            }
            delay(backOff(attempts))
        } catch (e: CancellationException) {
            api.getUnknownUpdates(offset, allowedUpdates = allowedUpdates)
            throw e
        } catch (e: ClosedSendChannelException) {
            break
        } catch (e: SerializationException) {
            exceptionHandler(e)
            discardPreviousUpdates(api)
        } catch (e: Throwable) {
            exceptionHandler(e)
        }
    }
    api.getUnknownUpdates(offset)
}

suspend fun getUpdates(
    api: TelegramApi,
    channel: SendChannel<Update>,
    offset: Long? = null,
    backOff: (Long) -> Long = { _ -> 50L },
    allowedUpdates: Array<KClass<out Update>> = emptyArray(),
    exceptionHandlers: Array<ExceptionHandler>
) = getUpdates(api, channel, offset, backOff, allowedUpdates) {
    exceptionHandlers.handle(it)
}

fun CoroutineScope.getPreviousUpdates(
    api: TelegramApi,
    allowedUpdates: Array<KClass<out Update>> = emptyArray(),
    exceptionHandler: suspend (Throwable) -> Unit = { it.printStackTrace() }
): Channel<Update> {
    val channel = Channel<Update>()
    launch {
        try {
            getPreviousUpdates(api, channel, allowedUpdates, exceptionHandler)
        } finally {
            channel.close()
        }
    }
    return channel
}

fun CoroutineScope.getPreviousUpdates(
    api: TelegramApi,
    allowedUpdates: Array<KClass<out Update>> = emptyArray(),
    exceptionHandlers: Array<ExceptionHandler>
) = getPreviousUpdates(api, allowedUpdates) {
    exceptionHandlers.handle(it)
}

fun CoroutineScope.getUpdates(
    api: TelegramApi,
    offset: Long? = null,
    backOff: (Long) -> Long = { _ -> 50L },
    allowedUpdates: Array<KClass<out Update>> = emptyArray(),
    exceptionHandler: suspend (Throwable) -> Unit = { it.printStackTrace() }
): Channel<Update> {
    val channel = Channel<Update>()
    launch {
        try {
            getUpdates(api, channel, offset, backOff, allowedUpdates, exceptionHandler)
        } finally {
            channel.close()
        }
    }
    return channel
}

fun CoroutineScope.getUpdates(
    api: TelegramApi,
    offset: Long? = null,
    backOff: (Long) -> Long = { _ -> 50L },
    allowedUpdates: Array<KClass<out Update>> = emptyArray(),
    exceptionHandlers: Array<ExceptionHandler>
) = getUpdates(api, offset, backOff, allowedUpdates) {
    exceptionHandlers.handle(it)
}
