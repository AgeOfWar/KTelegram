# Telegram Bot Kotlin Library
A simple-to-use library to create Telegram Bots in Kotlin multiplatform (actually only JVM target is
supported).
This library uses the [Telegram Bot API](https://core.telegram.org/bots/api)

## Example
This program re-sends messages.

```kotlin
fun main(vararg args: String) = runBlocking {
    if (args.size != 1) error("Pass the bot token as unique program argument.")
    val token = args[0]
    val api = TelegramApi(token)
    discardPreviousUpdates(api) // if you want discard previous updates
    getUpdates(api).handle(MessageRepeater(api))
}

class MessageRepeater(private val api: TelegramApi) : UpdateHandler {
    override suspend fun handle(update: Update) = update.handleMessage { message ->
        val content = message.toMessageContent()
        if (content != null) {
            api.sendMessage(message.messageId, content)
        }
    }
}
```
