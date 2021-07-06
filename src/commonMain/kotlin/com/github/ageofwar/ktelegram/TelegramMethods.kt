package com.github.ageofwar.ktelegram

import com.github.ageofwar.ktelegram.json.json
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlin.reflect.KClass

suspend fun TelegramApi.getUpdates(
    offset: Long? = null,
    limit: Int? = null,
    timeout: Int? = null,
    vararg allowedUpdates: KClass<out Update>
) = request<Array<Update>>(
    "getUpdates", mapOf(
        "offset" to offset,
        "lmiMit" to limit,
        "timeout" to timeout,
        "allowed_updates" to allowedUpdates.map { it.toJsonString() }.toJson(),
    )
)

suspend fun TelegramApi.getUpdatesAsList(
    offset: Long? = null,
    limit: Int? = null,
    timeout: Int? = null,
    vararg allowedUpdates: KClass<out Update>
) = request<List<Update>>(
    "getUpdates", mapOf(
        "offset" to offset,
        "lmiMit" to limit,
        "timeout" to timeout,
        "allowed_updates" to allowedUpdates.map { it.toJsonString() }.toJson(),
    )
)

suspend fun TelegramApi.getUnknownUpdates(
    offset: Long? = null,
    limit: Int? = null,
    timeout: Int? = null,
    vararg allowedUpdates: KClass<out Update>
) = request<Array<UnknownUpdate>>(
    "getUpdates", mapOf(
        "offset" to offset,
        "lmiMit" to limit,
        "timeout" to timeout,
        "allowed_updates" to allowedUpdates.map { it.toJsonString() }.toJson(),
    )
)

suspend fun TelegramApi.setWebhook(
    url: String,
    certificate: ByteArray,
    ipAddress: String? = null,
    maxConnections: Int = 40,
    dropPendingUpdates: Boolean = false,
    vararg allowedUpdates: KClass<out Update>
) {
    request<Boolean>(
        "setWebhook", mapOf(
            "url" to url,
            "ip_address" to ipAddress,
            "max_connections" to maxConnections,
            "allowed_updates" to allowedUpdates.map { it.toJsonString() }.toJson(),
            "drop_pending_updates" to dropPendingUpdates
        ), mapOf("certificate" to certificate)
    )
}

suspend fun TelegramApi.deleteWebhook(dropPendingUpdates: Boolean = false) {
    request<Boolean>("setWebhook", mapOf("drop_pending_updates" to dropPendingUpdates))
}

suspend fun TelegramApi.getWebhookInfo() = request<WebhookInfo>("getWebhookInfo")

suspend fun TelegramApi.getMe() = request<DetailedBot>("getMe")

suspend fun TelegramApi.logOut() {
    request<Boolean>("logOut")
}

suspend fun TelegramApi.closeBot() {
    request<Boolean>("close")
}

suspend fun <T : Message> TelegramApi.sendMessage(
    chatId: ChatId,
    content: MessageContent<T>,
    replyToMessageId: Long? = null,
    replyMarkup: ReplyMarkup? = null,
    disableNotification: Boolean = false,
    allowSendingWithoutReply: Boolean = true
): T {
    val parameters = mapOf(
        "chat_id" to (chatId.id ?: chatId.username),
        "disable_notification" to disableNotification,
        "reply_to_message_id" to replyToMessageId,
        "allow_sending_without_reply" to allowSendingWithoutReply,
        "reply_markup" to replyMarkup?.toJson()
    )
    @Suppress("UNCHECKED_CAST")
    return when (content) {
        is TextContent -> request<Message>(
            "sendMessage", parameters + mapOf(
                "text" to content.text.text,
                "entities" to content.text.entities.toJson(),
                "disable_web_page_preview" to content.disableWebPagePreview
            )
        )
        is PhotoContent -> request(
            "sendPhoto", parameters + mapOf(
                "photo" to (content.photo.fileId ?: content.photo.url),
                "caption" to content.caption?.text,
                "caption_entities" to content.caption?.entities?.toJson()
            ), mapOf("photo" to content.photo.content?.invoke())
        )
        is AudioContent -> request(
            "sendAudio", parameters + mapOf(
                "audio" to (content.audio.fileId ?: content.audio.url),
                "caption" to content.caption?.text,
                "caption_entities" to content.caption?.entities?.toJson(),
                "duration" to content.duration,
                "performer" to content.performer,
                "title" to content.title,
                "thumb" to (content.thumbnail?.fileId ?: content.thumbnail?.url)
            ), mapOf("audio" to content.audio.content?.invoke(), "thumb" to content.thumbnail?.content?.invoke())
        )
        is AnimationContent -> request(
            "sendAnimation",
            parameters + mapOf(
                "animation" to (content.animation.fileId ?: content.animation.url),
                "caption" to content.caption?.text,
                "caption_entities" to content.caption?.entities?.toJson(),
                "duration" to content.duration,
                "width" to content.width,
                "height" to content.height,
                "thumb" to (content.thumbnail?.fileId ?: content.thumbnail?.url)
            ),
            mapOf("animation" to content.animation.content?.invoke(), "thumb" to content.thumbnail?.content?.invoke())
        )
        is DocumentContent -> request(
            "sendDocument", parameters + mapOf(
                "document" to (content.document.fileId ?: content.document.url),
                "caption" to content.caption?.text,
                "caption_entities" to content.caption?.entities?.toJson(),
                "disable_content_type_detection" to content.disableContentTypeDetection,
                "thumb" to (content.thumbnail?.fileId ?: content.thumbnail?.url)
            ), mapOf("document" to content.document.content?.invoke(), "thumb" to content.thumbnail?.content?.invoke())
        )
        is StickerContent -> request(
            "sendSticker", parameters + mapOf(
                "sticker" to (content.sticker.fileId ?: content.sticker.url),
            ), mapOf("sticker" to content.sticker.content?.invoke())
        )
        is VideoContent -> request(
            "sendVideo", parameters + mapOf(
                "video" to (content.video.fileId ?: content.video.url),
                "caption" to content.caption?.text,
                "caption_entities" to content.caption?.entities?.toJson(),
                "duration" to content.duration,
                "width" to content.width,
                "height" to content.height,
                "supports_streaming" to content.supportsStreaming,
                "thumb" to (content.thumbnail?.fileId ?: content.thumbnail?.url)
            ), mapOf("video" to content.video.content?.invoke(), "thumb" to content.thumbnail?.content?.invoke())
        )
        is VideoNoteContent -> request(
            "sendVideoNote",
            parameters + mapOf(
                "video_note" to (content.videoNote.fileId ?: content.videoNote.url),
                "duration" to content.duration,
                "length" to content.length,
                "thumb" to (content.thumbnail?.fileId ?: content.thumbnail?.url)
            ),
            mapOf("video_note" to content.videoNote.content?.invoke(), "thumb" to content.thumbnail?.content?.invoke())
        )
        is VoiceContent -> request(
            "sendVoice", parameters + mapOf(
                "voice" to (content.voice.fileId ?: content.voice.url),
                "caption" to content.caption?.text,
                "caption_entities" to content.caption?.entities?.toJson(),
                "duration" to content.duration,
            ), mapOf("voice" to content.voice.content?.invoke())
        )
        is ContactContent -> request(
            "sendMessage", parameters + mapOf(
                "phone_number" to content.phoneNumber,
                "first_name" to content.firstName,
                "last_name" to content.lastName,
                "vcard" to content.vcard
            )
        )
        is DiceContent -> request("sendDice", parameters + mapOf("emoji" to content.emoji))
        is GameContent -> request(
            "sendGame",
            parameters + mapOf("game_short_name" to content.gameShortName)
        )
        is PollContent.Regular -> request(
            "sendPoll", parameters + mapOf(
                "question" to content.question,
                "options" to content.options.toJson(),
                "is_anonymous" to content.isAnonymous,
                "type" to "regular",
                "allows_multiple_answers" to content.allowsMultipleAnswers,
                "open_period" to content.openPeriod,
                "close_date" to content.closeDate,
                "is_closed" to content.isClosed
            )
        )
        is PollContent.Quiz -> request(
            "sendPoll", parameters + mapOf(
                "question" to content.question,
                "options" to content.options.toJson(),
                "is_anonymous" to content.isAnonymous,
                "type" to "quiz",
                "correct_option_id" to content.correctOption,
                "explanation" to content.explanation?.text,
                "explanation_entities" to content.explanation?.entities?.toJson(),
                "open_period" to content.openPeriod,
                "close_date" to content.closeDate,
                "is_closed" to content.isClosed
            )
        )
        is VenueContent -> request(
            "sendVenue", parameters + mapOf(
                "latitude" to content.venue.location.latitude,
                "longitude" to content.venue.location.longitude,
                "title" to content.venue.title,
                "address" to content.venue.address,
                "foursquare_id" to content.venue.foursquareId,
                "foursquare_type" to content.venue.foursquareType,
                "google_place_id" to content.venue.googlePlaceId,
                "google_place_type" to content.venue.googlePlaceType
            )
        )
        is LocationContent -> request(
            "sendLocation", parameters + mapOf(
                "latitude" to content.location.latitude,
                "longitude" to content.location.longitude,
                "horizontal_accuracy" to content.location.horizontalAccuracy,
                "live_period" to content.location.livePeriod,
                "heading" to content.location.heading,
                "proximity_alert_radius" to content.location.proximityAlertRadius
            )
        )
        is InvoiceContent -> request(
            "sendInvoice", parameters + mapOf(
                "title" to content.title,
                "description" to content.description,
                "payload" to content.payload,
                "provider_token" to content.providerToken,
                "currency" to content.currency,
                "prices" to content.prices.toJson(),
                "max_tip_amount" to content.maxTipAmount,
                "suggested_tip_amounts" to content.suggestedTipAmounts.toJson(),
                "start_parameter" to content.startParameter,
                "provider_data" to content.providerData,
                "photo_url" to content.photoUrl,
                "photo_size" to content.photoSize,
                "photo_width" to content.photoWidth,
                "photo_height" to content.photoHeight,
                "need_name" to content.needName,
                "need_phone_number" to content.needPhoneNumber,
                "need_email" to content.needEmail,
                "need_shipping_address" to content.needShippingAddress,
                "send_phone_number_to_provider" to content.sendPhoneNumberToProvider,
                "send_email_to_provider" to content.sendEmailToProvider,
                "is_flexible" to content.flexible
            )
        )
    } as T
}

suspend fun <T : Message> TelegramApi.sendMessage(
    replyToMessageId: MessageId,
    content: MessageContent<T>,
    replyMarkup: ReplyMarkup? = null,
    disableNotification: Boolean = false,
    allowSendingWithoutReply: Boolean = true
): T = sendMessage(
    replyToMessageId.chatId,
    content,
    replyToMessageId.messageId,
    replyMarkup,
    disableNotification,
    allowSendingWithoutReply
)

suspend fun TelegramApi.forwardMessage(
    chatId: ChatId,
    fromChatId: ChatId,
    messageId: Long,
    disableNotification: Boolean = false
) = request<Message>(
    "forwardMessage", mapOf(
        "chat_id" to (chatId.id ?: chatId.username),
        "from_chat_id" to (fromChatId.id ?: chatId.username),
        "message_id" to messageId,
        "disable_notification" to disableNotification
    )
)

suspend fun TelegramApi.sendTextMessage(
    chatId: ChatId,
    text: Text,
    disableWebPagePreview: Boolean = false,
    replyToMessageId: Long? = null,
    replyMarkup: ReplyMarkup? = null,
    disableNotification: Boolean = false,
    allowSendingWithoutReply: Boolean = true
) = sendMessage(chatId, TextContent(text, disableWebPagePreview), replyToMessageId, replyMarkup, disableNotification, allowSendingWithoutReply)

suspend fun TelegramApi.sendTextMessage(
    replyToMessageId: MessageId,
    text: Text,
    disableWebPagePreview: Boolean = false,
    replyMarkup: ReplyMarkup? = null,
    disableNotification: Boolean = false,
    allowSendingWithoutReply: Boolean = true
) = sendMessage(replyToMessageId, TextContent(text, disableWebPagePreview), replyMarkup, disableNotification, allowSendingWithoutReply)

suspend fun TelegramApi.forwardMessage(
    chatId: ChatId,
    messageId: MessageId,
    disableNotification: Boolean = false
) = request<Message>(
    "forwardMessage", mapOf(
        "chat_id" to (chatId.id ?: chatId.username),
        "from_chat_id" to (messageId.chatId.id ?: messageId.chatId.username),
        "message_id" to messageId.messageId,
        "disable_notification" to disableNotification
    )
)

suspend fun TelegramApi.copyMessage(
    chatId: ChatId,
    messageId: MessageId,
    caption: Text? = null,
    replyToMessageId: Long? = null,
    disableNotification: Boolean = false,
    allowSendingWithoutReply: Boolean = true,
    replyMarkup: ReplyMarkup? = null
) = request<JsonObject>(
    "copyMessage", mapOf(
        "chat_id" to (chatId.id ?: chatId.username),
        "from_chat_id" to (messageId.chatId.id ?: messageId.chatId.username),
        "message_id" to messageId.messageId,
        "caption" to caption?.text,
        "caption_entities" to caption?.entities?.toJson(),
        "reply_to_message_id" to replyToMessageId,
        "disable_notification" to disableNotification,
        "allow_sending_without_reply" to allowSendingWithoutReply,
        "reply_markup" to replyMarkup?.toJson()
    )
)["message_id"]?.jsonPrimitive?.long ?: throw SerializationException()

suspend fun TelegramApi.sendMediaGroup(
    chatId: ChatId,
    media: List<OutputMedia>,
    disableNotification: Boolean = false,
    replyToMessageId: Long? = null,
    allowSendingWithoutReply: Boolean = true
) = request<List<Message>>("sendMediaGroup", mapOf(
    "chat_id" to (chatId.id ?: chatId.username),
    "media" to media.toJson(),
    "disable_notification" to disableNotification,
    "reply_to_message_id" to replyToMessageId,
    "allow_sending_without_reply" to allowSendingWithoutReply
), media.mapNotNull { it.media.fileName?.to(it.media.content!!.invoke()) }.toMap())

suspend fun TelegramApi.sendMediaGroup(
    replyToMessageId: MessageId,
    media: List<OutputMedia>,
    disableNotification: Boolean = false,
    allowSendingWithoutReply: Boolean = true
) = request<List<Message>>(
    "sendMediaGroup", mapOf(
        "chat_id" to (replyToMessageId.chatId.id ?: replyToMessageId.chatId.username),
        "media" to media.toJson(),
        "disable_notification" to disableNotification,
        "reply_to_message_id" to replyToMessageId.messageId,
        "allow_sending_without_reply" to allowSendingWithoutReply
    ), media.mapNotNull { it.media.fileName?.to(it.media.content!!.invoke()) }.toMap()
)

suspend fun TelegramApi.editMessageLiveLocation(
    messageId: MessageId,
    location: Location,
    replyMarkup: InlineKeyboard? = null
) = request<LocationMessage>(
    "editMessageLiveLocation", mapOf(
        "chat_id" to (messageId.chatId.id ?: messageId.chatId.username),
        "message_id" to messageId.messageId,
        "latitude" to location.latitude,
        "longitude" to location.longitude,
        "horizontal_accuracy" to location.horizontalAccuracy,
        "heading" to location.heading,
        "proximity_alert_radius" to location.proximityAlertRadius,
        "reply_markup" to replyMarkup?.toJson()
    )
)

suspend fun TelegramApi.editMessageLiveLocation(
    inlineMessageId: InlineMessageId,
    location: Location,
    replyMarkup: InlineKeyboard? = null
) {
    request<JsonElement>(
        "editMessageLiveLocation", mapOf(
            "inline_message_id" to inlineMessageId.inlineId,
            "message_id" to inlineMessageId.messageId?.messageId,
            "chat_id" to (inlineMessageId.messageId?.chatId?.id
                ?: inlineMessageId.messageId?.chatId?.username),
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "horizontal_accuracy" to location.horizontalAccuracy,
            "heading" to location.heading,
            "proximity_alert_radius" to location.proximityAlertRadius,
            "reply_markup" to replyMarkup?.toJson()
        )
    )
}

suspend fun TelegramApi.stopMessageLiveLocation(
    messageId: MessageId,
    replyMarkup: InlineKeyboard? = null
) = request<LocationMessage>(
    "stopMessageLiveLocation", mapOf(
        "chat_id" to (messageId.chatId.id ?: messageId.chatId.username),
        "message_id" to messageId.messageId,
        "reply_markup" to replyMarkup?.toJson()
    )
)


suspend fun TelegramApi.stopMessageLiveLocation(
    inlineMessageId: InlineMessageId,
    replyMarkup: InlineKeyboard? = null
) {
    request<JsonElement>(
        "stopMessageLiveLocation", mapOf(
            "inline_message_id" to inlineMessageId.inlineId,
            "message_id" to inlineMessageId.messageId?.messageId,
            "chat_id" to (inlineMessageId.messageId?.chatId?.id
                ?: inlineMessageId.messageId?.chatId?.username),
            "reply_markup" to replyMarkup?.toJson()
        )
    )
}

suspend fun TelegramApi.sendChatAction(chatId: ChatId, action: ChatAction) {
    request<Boolean>(
        "sendChatAction", mapOf(
            "chat_id" to (chatId.id ?: chatId.username),
            "action" to action.toString()
        )
    )
}

suspend fun TelegramApi.getUserProfilePhotos(userId: Long, offset: Int = 0, limit: Int = 100) =
    request<UserProfilePhotos>(
        "getUserProfilePhotos", mapOf(
            "user_id" to userId,
            "offset" to offset,
            "limit" to limit
        )
    )

suspend fun TelegramApi.getFile(fileId: String) =
    request<File>("getFile", mapOf("file_id" to fileId))

suspend fun TelegramApi.downloadFile(fileId: String): ByteArray {
    val file = getFile(fileId)
    requireNotNull(file.path) { "Cannot download file \"${file.fileId}\"" }
    return download(file.path)
}

@Deprecated("old Telegram Bot API", ReplaceWith("banChatMember"))
suspend fun TelegramApi.kickChatMember(
    chatId: ChatId,
    userId: Long,
    untilDate: Int? = null,
    revokeMessages: Boolean = false
) {
    request<Boolean>(
        "kickChatMember", mapOf(
            "chat_id" to (chatId.id ?: chatId.username),
            "user_id" to userId,
            "until_date" to untilDate,
            "revoke_messages" to revokeMessages
        )
    )
}

suspend fun TelegramApi.banChatMember(
    chatId: ChatId,
    userId: Long,
    untilDate: Int? = null,
    revokeMessages: Boolean = false
) {
    request<Boolean>(
        "banChatMember", mapOf(
            "chat_id" to (chatId.id ?: chatId.username),
            "user_id" to userId,
            "until_date" to untilDate,
            "revoke_messages" to revokeMessages
        )
    )
}

suspend fun TelegramApi.unbanChatMember(
    chatId: ChatId,
    userId: Long,
    onlyIfBanned: Boolean = true
) {
    request<Boolean>(
        "unbanChatMember", mapOf(
            "chat_id" to (chatId.id ?: chatId.username),
            "user_id" to userId,
            "only_if_banned" to onlyIfBanned
        )
    )
}

suspend fun TelegramApi.restrictChatMember(
    chatId: ChatId,
    userId: Long,
    permissions: ChatPermissions,
    untilDate: Int? = null
) {
    request<Boolean>(
        "restrictChatMember", mapOf(
            "chat_id" to (chatId.id ?: chatId.username),
            "user_id" to userId,
            "permissions" to permissions,
            "until_date" to untilDate
        )
    )
}

suspend fun TelegramApi.promoteChatMember(
    chatId: ChatId,
    userId: Long,
    permissions: AdminPermissions,
    untilDate: Int? = null
) {
    request<Boolean>(
        "promoteChatMember", mapOf(
            "chat_id" to (chatId.id ?: chatId.username),
            "user_id" to userId,
            "until_date" to untilDate,
            "is_anonymous" to permissions.isAnonymous,
            "can_change_info" to permissions.canChangeInfo,
            "can_post_messages" to permissions.canPostMessages,
            "can_edit_messages" to permissions.canEditMessages,
            "can_delete_messages" to permissions.canDeleteMessages,
            "can_invite_users" to permissions.canInviteUsers,
            "can_restrict_members" to permissions.canRestrictMembers,
            "can_pin_messages" to permissions.canPinMessages,
            "can_promote_members" to permissions.canPromoteMembers,
            "can_manage_voice_chats" to permissions.canManageVoiceChats,
            "can_manage_chat" to permissions.canManageChat
        )
    )
}

suspend fun TelegramApi.setChatAdministratorCustomTitle(
    chatId: ChatId,
    userId: Long,
    customTitle: String
) {
    request<Boolean>(
        "setChatAdministratorCustomTitle", mapOf(
            "chat_id" to (chatId.id ?: chatId.username),
            "user_id" to userId,
            "custom_title" to customTitle
        )
    )
}

suspend fun TelegramApi.setChatPermissions(chatId: ChatId, permissions: ChatPermissions) {
    request<Boolean>(
        "setChatPermissions", mapOf(
            "chat_id" to (chatId.id ?: chatId.username),
            "permissions" to permissions
        )
    )
}

suspend fun TelegramApi.exportChatInviteLink(chatId: ChatId) = request<String>(
    "exportChatInviteLink", mapOf(
        "chat_id" to (chatId.id ?: chatId.username)
    )
)

suspend fun TelegramApi.createChatInviteLink(
    chatId: ChatId,
    expireDate: Int? = null,
    memberLimit: Int? = null
) = request<ChatInviteLink>(
    "createChatInviteLink", mapOf(
        "chat_id" to (chatId.id ?: chatId.username),
        "expire_date" to expireDate,
        "member_limit" to memberLimit
    )
)

suspend fun TelegramApi.editChatInviteLink(
    chatId: ChatId,
    inviteLink: String,
    expireDate: Int? = null,
    numberLimit: Int? = null
) = request<ChatInviteLink>(
    "editChatInviteLink", mapOf(
        "chat_id" to (chatId.id ?: chatId.username),
        "invite_link" to inviteLink,
        "expire_date" to expireDate,
        "number_limit" to numberLimit
    )
)

suspend fun TelegramApi.revokeChatInviteLink(
    chatId: ChatId,
    inviteLink: String,
) = request<ChatInviteLink>(
    "revokeChatInviteLink", mapOf(
        "chat_id" to (chatId.id ?: chatId.username),
        "invite_link" to inviteLink
    )
)

suspend fun TelegramApi.setChatPhoto(chatId: ChatId, photo: ByteArray) {
    request<Boolean>(
        "setChatPhoto", mapOf(
            "chat_id" to (chatId.id ?: chatId.username)
        ), mapOf("photo" to photo)
    )
}

suspend fun TelegramApi.deleteChatPhoto(chatId: ChatId) {
    request<Boolean>(
        "deleteChatPhoto", mapOf(
            "chat_id" to (chatId.id ?: chatId.username)
        )
    )
}

suspend fun TelegramApi.setChatTitle(chatId: ChatId, title: String) {
    request<Boolean>(
        "setChatTitle", mapOf(
            "chat_id" to (chatId.id ?: chatId.username),
            "title" to title
        )
    )
}

suspend fun TelegramApi.setChatDescription(chatId: ChatId, description: String) {
    request<Boolean>(
        "setChatDescription", mapOf(
            "chat_id" to (chatId.id ?: chatId.username),
            "description" to description
        )
    )
}

suspend fun TelegramApi.pinChatMessage(messageId: MessageId, disableNotification: Boolean = false) {
    request<Boolean>(
        "pinChatMessage", mapOf(
            "chat_id" to (messageId.chatId.id ?: messageId.chatId.username),
            "message_id" to messageId.messageId,
            "disable_notification" to disableNotification
        )
    )
}

suspend fun TelegramApi.unpinChatMessage(chatId: ChatId, messageId: Long? = null) {
    request<Boolean>(
        "unpinChatMessage", mapOf(
            "chat_id" to (chatId.id ?: chatId.username),
            "message_id" to messageId,
        )
    )
}

suspend fun TelegramApi.unpinAllChatMessages(chatId: ChatId) {
    request<Boolean>(
        "unpinAllChatMessages", mapOf(
            "chat_id" to (chatId.id ?: chatId.username)
        )
    )
}

suspend fun TelegramApi.leaveChat(chatId: ChatId) {
    request<Boolean>(
        "leaveChat", mapOf(
            "chat_id" to (chatId.id ?: chatId.username)
        )
    )
}

suspend fun TelegramApi.getChat(chatId: ChatId) = request<DetailedChat>(
    "getChat", mapOf(
        "chat_id" to (chatId.id ?: chatId.username)
    )
)

suspend fun TelegramApi.getChatAdministrators(chatId: ChatId) = request<List<ChatMember>>(
    "getChatAdministrators", mapOf(
        "chat_id" to (chatId.id ?: chatId.username)
    )
)

@Deprecated("old Telegram Bot API", ReplaceWith("banChatMember"))
suspend fun TelegramApi.getChatMembersCount(chatId: ChatId) = request<Int>(
    "getChatMembersCount", mapOf(
        "chat_id" to (chatId.id ?: chatId.username)
    )
)

suspend fun TelegramApi.getChatMemberCount(chatId: ChatId) = request<Int>(
    "getChatMemberCount", mapOf(
        "chat_id" to (chatId.id ?: chatId.username)
    )
)

suspend fun TelegramApi.getChatMember(chatId: ChatId, userId: Long) = request<ChatMember>(
    "getChatMember", mapOf(
        "chat_id" to (chatId.id ?: chatId.username),
        "user_id" to userId
    )
)

suspend fun TelegramApi.setChatStickerSet(chatId: ChatId, stickerSetName: String) {
    request<Boolean>(
        "setChatStickerSet", mapOf(
            "chat_id" to (chatId.id ?: chatId.username),
            "sticker_set_name" to stickerSetName
        )
    )
}

suspend fun TelegramApi.deleteChatStickerSet(chatId: ChatId) {
    request<Boolean>("deleteChatStickerSet", mapOf("chat_id" to (chatId.id ?: chatId.username)))
}

suspend fun TelegramApi.answerCallbackQuery(
    callbackQueryId: String,
    text: String? = null,
    showAlert: Boolean = false,
    url: String? = null,
    cacheTime: Int = 0
) {
    request<Boolean>(
        "answerCallbackQuery", mapOf(
            "callback_query_id" to callbackQueryId,
            "text" to text,
            "show_alert" to showAlert,
            "url" to url,
            "cache_time" to cacheTime
        )
    )
}

suspend fun TelegramApi.setMyCommands(
    scope: BotCommandScope = BotCommandScope.Default,
    languageCode: String? = null,
    vararg commands: BotCommand
) {
    request<Boolean>(
        "setMyCommands", mapOf(
            "commands" to commands.toJson(),
            "scope" to scope.toJson(),
            "language_code" to languageCode
        )
    )
}

suspend fun TelegramApi.setMyCommands(
    scope: BotCommandScope = BotCommandScope.Default,
    languageCode: String? = null,
    commands: List<BotCommand>
) {
    request<Boolean>(
        "setMyCommands", mapOf(
            "commands" to commands.toJson(),
            "scope" to scope.toJson(),
            "language_code" to languageCode
        )
    )
}

suspend fun TelegramApi.getMyCommands(scope: BotCommandScope = BotCommandScope.Default, languageCode: String? = null) =
    request<List<BotCommand>>("getMyCommands", mapOf(
        "scope" to scope.toJson(),
        "language_code" to languageCode
    ))

suspend fun TelegramApi.deleteMyCommands(scope: BotCommandScope = BotCommandScope.Default, languageCode: String? = null) =
    request<List<BotCommand>>("deleteMyCommands", mapOf(
        "scope" to scope.toJson(),
        "language_code" to languageCode
    ))

suspend inline fun <reified T : Message> TelegramApi.editMessageText(
    messageId: MessageId,
    text: Text,
    disableWebPagePreview: Boolean = false,
    replyMarkup: InlineKeyboard? = null
) = request<T>(
    "editMessageText", mapOf(
        "chat_id" to (messageId.chatId.id ?: messageId.chatId.username),
        "message_id" to messageId.messageId,
        "text" to text.text,
        "entities" to json.encodeToString(text.entities),
        "disable_web_page_preview" to disableWebPagePreview,
        "reply_markup" to if (replyMarkup != null) json.encodeToString(replyMarkup) else null
    )
)

suspend fun TelegramApi.editMessageText(
    inlineMessageId: InlineMessageId,
    text: Text,
    disableWebPagePreview: Boolean = false,
    replyMarkup: InlineKeyboard? = null
) {
    request<JsonElement>(
        "editMessageText", mapOf(
            "inline_message_id" to inlineMessageId.inlineId,
            "message_id" to inlineMessageId.messageId?.messageId,
            "chat_id" to (inlineMessageId.messageId?.chatId?.id
                ?: inlineMessageId.messageId?.chatId?.username),
            "text" to text.text,
            "entities" to json.encodeToString(text.entities),
            "disable_web_page_preview" to disableWebPagePreview,
            "reply_markup" to replyMarkup?.toJson()
        )
    )
}

suspend inline fun <reified T : Message> TelegramApi.editMessageCaption(
    messageId: MessageId,
    text: Text,
    replyMarkup: InlineKeyboard? = null
) = request<T>(
    "editMessageCaption", mapOf(
        "chat_id" to (messageId.chatId.id ?: messageId.chatId.username),
        "message_id" to messageId.messageId,
        "caption" to text.text,
        "caption_entities" to json.encodeToString(text.entities),
        "reply_markup" to if (replyMarkup != null) json.encodeToString(replyMarkup) else null
    )
)

suspend fun TelegramApi.editMessageCaption(
    inlineMessageId: InlineMessageId,
    text: Text,
    replyMarkup: InlineKeyboard? = null
) {
    request<JsonElement>(
        "editMessageCaption", mapOf(
            "inline_message_id" to inlineMessageId.inlineId,
            "message_id" to inlineMessageId.messageId?.messageId,
            "chat_id" to (inlineMessageId.messageId?.chatId?.id
                ?: inlineMessageId.messageId?.chatId?.username),
            "caption" to text.text,
            "caption_entities" to json.encodeToString(text.entities),
            "reply_markup" to replyMarkup?.toJson()
        )
    )
}

suspend inline fun <reified T : Message> TelegramApi.editMessageMedia(
    messageId: MessageId,
    media: OutputMedia,
    replyMarkup: InlineKeyboard? = null
) = request<T>(
    "editMessageMedia",
    mapOf(
        "chat_id" to (messageId.chatId.id ?: messageId.chatId.username),
        "message_id" to messageId.messageId,
        "media" to json.encodeToString(media),
        "reply_markup" to if (replyMarkup != null) json.encodeToString(replyMarkup) else null
    ),
    if (media.media.fileName != null) mapOf(media.media.fileName!! to media.media.content?.invoke()) else emptyMap()
)

suspend fun TelegramApi.editMessageMedia(
    inlineMessageId: InlineMessageId,
    media: OutputMedia,
    replyMarkup: InlineKeyboard? = null
) {
    request<JsonElement>(
        "editMessageMedia",
        mapOf(
            "inline_message_id" to inlineMessageId.inlineId,
            "message_id" to inlineMessageId.messageId?.messageId,
            "chat_id" to (inlineMessageId.messageId?.chatId?.id
                ?: inlineMessageId.messageId?.chatId?.username),
            "media" to json.encodeToString(media),
            "reply_markup" to replyMarkup?.toJson()
        ),
        if (media.media.fileName != null) mapOf(media.media.fileName!! to media.media.content?.invoke()) else emptyMap()
    )
}

suspend inline fun <reified T : Message> TelegramApi.editMessageReplyMarkup(
    messageId: MessageId,
    replyMarkup: InlineKeyboard? = null
) = request<T>(
    "editMessageReplyMarkup", mapOf(
        "chat_id" to (messageId.chatId.id ?: messageId.chatId.username),
        "message_id" to messageId.messageId,
        "reply_markup" to if (replyMarkup != null) json.encodeToString(replyMarkup) else null
    )
)

suspend fun TelegramApi.editMessageReplyMarkup(
    inlineMessageId: InlineMessageId,
    replyMarkup: InlineKeyboard? = null
) {
    request<JsonElement>(
        "editMessageReplyMarkup", mapOf(
            "inline_message_id" to inlineMessageId.inlineId,
            "message_id" to inlineMessageId.messageId?.messageId,
            "chat_id" to (inlineMessageId.messageId?.chatId?.id
                ?: inlineMessageId.messageId?.chatId?.username),
            "reply_markup" to replyMarkup?.toJson()
        )
    )
}

suspend fun TelegramApi.stopPoll(messageId: MessageId, replyMarkup: InlineKeyboard? = null) =
    request<Poll>(
        "stopPoll", mapOf(
            "chat_id" to (messageId.chatId.id ?: messageId.chatId.username),
            "message_id" to messageId.messageId,
            "reply_markup" to if (replyMarkup != null) json.encodeToString(replyMarkup) else null
        )
    )

suspend fun TelegramApi.deleteMessage(messageId: MessageId) {
    request<Boolean>(
        "deleteMessage", mapOf(
            "chat_id" to (messageId.chatId.id ?: messageId.chatId.username),
            "message_id" to messageId.messageId
        )
    )
}

suspend fun TelegramApi.getStickerSet(name: String) =
    request<StickerSet>("getStickerSet", mapOf("name" to name))

suspend fun TelegramApi.uploadStickerFile(userId: Long, pngSticker: ByteArray): File {
    return request<File>(
        "uploadStickerFile",
        mapOf("user_id" to userId),
        mapOf("png_sticker" to pngSticker)
    )
}

suspend fun TelegramApi.createNewStickerSet(
    userId: Long,
    name: String,
    title: String,
    pngSticker: OutputFile,
    emojis: String,
    containsMasks: Boolean = false,
    maskPosition: MaskPosition? = null
) = request<StickerSet>(
    "createNewStickerSet", mapOf(
        "user_id" to userId,
        "name" to name,
        "title" to title,
        "png_sticker" to (pngSticker.fileId ?: pngSticker.url),
        "emojis" to emojis,
        "contains_masks" to containsMasks,
        "mask_position" to maskPosition?.toJson()
    ), mapOf("png_sticker" to pngSticker.content?.invoke())
)

suspend fun TelegramApi.createNewStickerSet(
    userId: Long,
    name: String,
    title: String,
    tgsSticker: ByteArray,
    emojis: String,
    containsMasks: Boolean = false,
    maskPosition: MaskPosition? = null
) = request<StickerSet>(
    "createNewStickerSet", mapOf(
        "user_id" to userId,
        "name" to name,
        "title" to title,
        "emojis" to emojis,
        "contains_masks" to containsMasks,
        "mask_position" to maskPosition?.toJson()
    ), mapOf("tgs_sticker" to tgsSticker)
)

suspend fun TelegramApi.addStickerToSet(
    userId: Long,
    name: String,
    pngSticker: OutputFile,
    emojis: String,
    maskPosition: MaskPosition? = null
) {
    request<Boolean>(
        "addStickerToSet", mapOf(
            "user_id" to userId,
            "name" to name,
            "png_sticker" to (pngSticker.fileId ?: pngSticker.url),
            "emojis" to emojis,
            "mask_position" to maskPosition?.toJson()
        ), mapOf("png_sticker" to pngSticker.content?.invoke())
    )
}

suspend fun TelegramApi.addStickerToSet(
    userId: Long,
    name: String,
    tgsSticker: ByteArray,
    emojis: String,
    maskPosition: MaskPosition? = null
) {
    request<Boolean>(
        "addStickerToSet", mapOf(
            "user_id" to userId,
            "name" to name,
            "emojis" to emojis,
            "mask_position" to maskPosition?.toJson()
        ), mapOf("tgs_sticker" to tgsSticker)
    )
}

suspend fun TelegramApi.setStickerPositionInSet(sticker: String, position: Int) {
    request<Boolean>(
        "setStickerPositionInSet", mapOf(
            "sticker" to sticker,
            "position" to position
        )
    )
}

suspend fun TelegramApi.deleteStickerFromSet(sticker: String) {
    request<Boolean>(
        "deleteStickerFromSet", mapOf("sticker" to sticker)
    )
}

suspend fun TelegramApi.setStickerSetThumb(
    name: String,
    userId: Long,
    thumbnail: OutputFile? = null
) {
    request<Boolean>(
        "setStickerSetThumb", mapOf(
            "user_id" to userId,
            "name" to name,
            "thumb" to (thumbnail?.fileId ?: thumbnail?.url)
        ), mapOf("thumb" to thumbnail?.content?.invoke())
    )
}

suspend fun TelegramApi.answerInlineQuery(
    inlineQueryId: String,
    vararg results: InlineQueryResult,
    cacheTime: Int = 0,
    isPersonal: Boolean = false,
    nextOffset: String = "",
    switchPrivateMessageText: String? = null,
    switchPrivateMessageParameter: String? = null
) {
    request<Boolean>(
        "answerInlineQuery", mapOf(
            "inline_query_id" to inlineQueryId,
            "results" to results.toJson(),
            "cache_time" to cacheTime,
            "is_personal" to isPersonal,
            "next_offset" to nextOffset,
            "switch_pm_text" to switchPrivateMessageText,
            "switch_pm_parameter" to switchPrivateMessageParameter
        )
    )
}

suspend fun TelegramApi.setGameScore(
    userId: Long,
    score: Int,
    force: Boolean = false,
    disableEditMessage: Boolean = false,
    messageId: MessageId
) = request<GameMessage>(
    "setGameScore", mapOf(
        "user_id" to userId,
        "score" to score,
        "force" to force,
        "disable_edit_message" to disableEditMessage,
        "message_id" to messageId.messageId,
        "chat_id" to (messageId.chatId.id ?: messageId.chatId.username)
    )
)

suspend fun TelegramApi.setGameScore(
    userId: Long,
    score: Int,
    force: Boolean = false,
    disableEditMessage: Boolean = false,
    inlineMessageId: InlineMessageId
) {
    request<JsonElement>(
        "setGameScore", mapOf(
            "user_id" to userId,
            "score" to score,
            "force" to force,
            "disable_edit_message" to disableEditMessage,
            "inline_message_id" to inlineMessageId.inlineId,
            "message_id" to inlineMessageId.messageId?.messageId,
            "chat_id" to (inlineMessageId.messageId?.chatId?.id
                ?: inlineMessageId.messageId?.chatId?.username)
        )
    )
}

suspend fun TelegramApi.getGameHighScores(
    userId: Long,
    inlineMessageId: InlineMessageId
) = request<List<GameHighScore>>(
    "getGameHighScores", mapOf(
        "user_id" to userId,
        "inline_message_id" to inlineMessageId.inlineId,
        "message_id" to inlineMessageId.messageId?.messageId,
        "chat_id" to (inlineMessageId.messageId?.chatId?.id
            ?: inlineMessageId.messageId?.chatId?.username)
    )
)

suspend fun TelegramApi.answerShippingQuery(
    shippingQueryId: String,
    shippingOptions: List<ShippingOption>
) = request<Boolean>(
    "answerShippingQuery", mapOf(
        "shipping_query_id" to shippingQueryId,
        "ok" to true,
        "shipping_options" to shippingOptions.toJson()
    )
)

suspend fun TelegramApi.answerShippingQuery(
    shippingQueryId: String,
    errorMessage: String
) = request<Boolean>(
    "answerShippingQuery", mapOf(
        "shipping_query_id" to shippingQueryId,
        "ok" to false,
        "error_message" to errorMessage
    )
)

suspend fun TelegramApi.answerPreCheckoutQuery(preCheckoutQueryId: String) = request<Boolean>(
    "answerPreCheckoutQuery", mapOf(
        "pre_checkout_query_id" to preCheckoutQueryId,
        "ok" to true
    )
)

suspend fun TelegramApi.answerPreCheckoutQuery(
    preCheckoutQueryId: String,
    errorMessage: String
) = request<Boolean>(
    "answerPreCheckoutQuery", mapOf(
        "pre_checkout_query_id" to preCheckoutQueryId,
        "ok" to false,
        "error_message" to errorMessage
    )
)

private inline fun <reified T : Any> T.toJson() = json.encodeToString(this)
