package com.github.ageofwar.ktelegram

inline fun Update.handleCommand(
    block: (message: TextMessage, name: String, botUsername: String?, args: Text) -> Unit
) = handleMessageOrPost { message ->
    if (message is TextMessage) {
        val text = message.text
        val commandEntity = text.entities.firstOrNull()
        if (commandEntity !is MessageEntity.BotCommand) return
        val (offset, length) = commandEntity
        if (offset != 0) return
        val command = text.substring(1, length).split('@', limit = 2)
        val name = command.first()
        val botUsername = command.getOrNull(1)
        block(message, name, botUsername, text.subSequence(length, text.length).trim())
    }
}

suspend inline fun Update.handleCommand(
    api: TelegramApi,
    vararg names: String,
    ignoreCase: Boolean = true,
    block: (message: TextMessage, name: String, botUsername: String?, args: Text) -> Unit
) = handleCommand { message, name, botUsername, args ->
    if (names.isEmpty()) {
        if (botUsername == null || botUsername == api.getMe().username) {
            block(message, name, botUsername, args)
        }
    } else {
        names.forEach {
            if (name.equals(it, ignoreCase)) {
                if (botUsername == null || botUsername == api.getMe().username) {
                    block(message, name, botUsername, args)
                }
            }
        }
    }
}

inline fun Update.handleMessage(block: (Message) -> Unit) {
    if (this is MessageUpdate) {
        block(message)
    }
}

inline fun Update.handlePost(block: (Message) -> Unit) {
    if (this is ChannelPostUpdate) {
        block(message)
    }
}

inline fun Update.handleMessageOrPost(block: (Message) -> Unit) {
    handleMessage(block)
    handlePost(block)
}

inline fun Update.handleEditedMessage(block: (Message, Long) -> Unit) {
    if (this is EditedMessageUpdate) {
        block(message, lastEditDate)
    }
}

inline fun Update.handleEditedPost(block: (Message, Long) -> Unit) {
    if (this is EditedChannelPostUpdate) {
        block(message, lastEditDate)
    }
}

inline fun Update.handleEditedMessageOrPost(block: (Message, Long) -> Unit) {
    handleEditedMessage(block)
    handleEditedPost(block)
}

inline fun Update.handleInlineQuery(block: (InlineQuery) -> Unit) {
    if (this is InlineQueryUpdate) {
        block(inlineQuery)
    }
}

suspend inline fun Update.handleInlineQuery(
    api: TelegramApi,
    cacheTime: Int = 0,
    isPersonal: Boolean = false,
    nextOffset: String = "",
    switchPrivateMessageText: String? = null,
    switchPrivateMessageParameter: String? = null,
    block: (InlineQuery) -> Array<InlineQueryResult>
) {
    if (this is InlineQueryUpdate) {
        val results = block(inlineQuery)
        api.answerInlineQuery(
            inlineQuery.id,
            *results,
            cacheTime = cacheTime,
            isPersonal = isPersonal,
            nextOffset = nextOffset,
            switchPrivateMessageText = switchPrivateMessageText,
            switchPrivateMessageParameter = switchPrivateMessageParameter
        )
    }
}

inline fun Update.handleChosenInlineResult(block: (ChosenInlineResult) -> Unit) {
    if (this is ChosenInlineResultUpdate) {
        block(chosenInlineResult)
    }
}

inline fun Update.handleCallbackQuery(block: (CallbackQuery) -> Unit) {
    if (this is CallbackQueryUpdate) {
        block(callbackQuery)
    }
}

suspend inline fun Update.handleCallbackQuery(
    api: TelegramApi,
    cacheTime: Int = 0,
    block: (CallbackQuery) -> CallbackQueryAnswer
) {
    if (this is CallbackQueryUpdate) {
        val (text, showAlert, url) = block(callbackQuery)
        api.answerCallbackQuery(callbackQuery.id, text, showAlert, url, cacheTime)
    }
}

inline fun Update.handleCallbackData(block: (CallbackData) -> Unit) = handleCallbackQuery {
    if (it is CallbackData) {
        block(it)
    }
}

suspend inline fun Update.handleCallbackData(
    api: TelegramApi,
    cacheTime: Int = 0,
    block: (CallbackData) -> CallbackQueryAnswer
) = handleCallbackQuery {
    if (it is CallbackData) {
        val (text, showAlert, url) = block(it)
        api.answerCallbackQuery(it.id, text, showAlert, url, cacheTime)
    }
}

inline fun Update.handleCallbackGame(block: (CallbackGame) -> Unit) = handleCallbackQuery {
    if (it is CallbackGame) {
        block(it)
    }
}

suspend inline fun Update.handleCallbackGame(
    api: TelegramApi,
    cacheTime: Int = 0,
    block: (CallbackGame) -> CallbackQueryAnswer
) = handleCallbackQuery {
    if (it is CallbackGame) {
        val (text, showAlert, url) = block(it)
        api.answerCallbackQuery(it.id, text, showAlert, url, cacheTime)
    }
}

inline fun Update.handleShippingQuery(block: (ShippingQuery) -> Unit) {
    if (this is ShippingQueryUpdate) {
        block(shippingQuery)
    }
}

inline fun Update.handlePreCheckoutQuery(block: (PreCheckoutQuery) -> Unit) {
    if (this is PreCheckoutQueryUpdate) {
        block(preCheckoutQuery)
    }
}

inline fun Update.handlePoll(block: (Poll) -> Unit) {
    if (this is PollUpdate) {
        block(poll)
    }
}

inline fun Update.handlePollAnswer(block: (PollAnswer) -> Unit) {
    if (this is PollAnswerUpdate) {
        block(pollAnswer)
    }
}

inline fun Update.handleMyChatMemberUpdated(block: (ChatMemberUpdated) -> Unit) {
    if (this is MyChatMemberUpdate) {
        block(myChatMember)
    }
}

inline fun Update.handleChatMemberUpdated(block: (ChatMemberUpdated) -> Unit) {
    if (this is ChatMemberUpdate) {
        block(chatMember)
    }
}

inline fun Update.handleChatJoinRequest(block: (ChatJoinRequest) -> Unit) {
    if (this is ChatJoinRequestUpdate) {
        block(chatJoinRequest)
    }
}

suspend inline fun Update.handleChatJoinRequest(api: TelegramApi, block: (ChatJoinRequest) -> Boolean?) {
    if (this is ChatJoinRequestUpdate) {
        when (block(chatJoinRequest)) {
            true -> api.approveChatJoinRequest(chatJoinRequest.chat.chatId, chatJoinRequest.sender.id)
            false -> api.declineChatJoinRequest(chatJoinRequest.chat.chatId, chatJoinRequest.sender.id)
            else -> {}
        }
    }
}
