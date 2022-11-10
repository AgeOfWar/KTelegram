package com.github.ageofwar.ktelegram

import kotlinx.serialization.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.json.jsonObject

@Serializable(Message.Serializer::class)
sealed class Message : Id<Long> {
    abstract val messageThreadId: Long?
    abstract val sender: Sender
    abstract val date: Long
    abstract val chat: Chat
    abstract val forwardFrom: Sender?
    abstract val forwardFromMessageId: Long?
    abstract val forwardSignature: String?
    abstract val forwardSenderName: String?
    abstract val forwardDate: Long?
    abstract val isTopicMessage: Boolean
    abstract val isAutomaticForward: Boolean
    abstract val replyToMessage: Message?
    abstract val viaBot: Bot?
    abstract val lastEditDate: Long?
    abstract val hasProtectedContent: Boolean
    abstract val mediaGroupId: String?
    abstract val authorSignature: String?
    abstract val replyMarkup: InlineKeyboard?

    abstract fun toMessageContent(): MessageContent<*>?

    object Serializer : JsonContentPolymorphicSerializer<Message>(Message::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out Message> {
            val json = element.jsonObject
            return when {
                "text" in json -> TextMessage.serializer()
                "animation" in json -> AnimationMessage.serializer()
                "audio" in json -> AudioMessage.serializer()
                "document" in json -> DocumentMessage.serializer()
                "photo" in json -> PhotoMessage.serializer()
                "sticker" in json -> StickerMessage.serializer()
                "video" in json -> VideoMessage.serializer()
                "video_note" in json -> VideoNoteMessage.serializer()
                "contact" in json -> ContactMessage.serializer()
                "dice" in json -> DiceMessage.serializer()
                "game" in json -> GameMessage.serializer()
                "poll" in json -> PollMessage.serializer()
                "venue" in json -> VenueMessage.serializer()
                "location" in json -> LocationMessage.serializer()
                "new_chat_members" in json -> NewChatMembersMessage.serializer()
                "left_chat_member" in json -> LeftChatMemberMessage.serializer()
                "new_chat_title" in json -> NewChatTitleMessage.serializer()
                "new_chat_photo" in json -> NewChatPhotoMessage.serializer()
                "delete_chat_photo" in json -> DeleteChatPhotoMessage.serializer()
                "group_chat_created" in json -> GroupChatCreatedMessage.serializer()
                "supergroup_chat_created" in json -> SupergroupChatCreatedMessage.serializer()
                "channel_chat_created" in json -> ChannelChatCreatedMessage.serializer()
                "migrate_to_chat_id" in json -> MigrateToChatIdMessage.serializer()
                "migrate_from_chat_id" in json -> MigrateFromChatIdMessage.serializer()
                "pinned_message" in json -> PinnedMessageMessage.serializer()
                "forum_topic_created" in json -> ForumTopicCreatedMessage.serializer()
                "forum_topic_closed" in json -> ForumTopicClosedMessage.serializer()
                "forum_topic_reopened" in json -> ForumTopicReopenedMessage.serializer()
                "proximity_alert_triggered" in json -> ProximityAlertTriggeredMessage.serializer()
                "video_chat_started" in json -> VideoChatStartedMessage.serializer()
                "video_chat_ended" in json -> VideoChatEndedMessage.serializer()
                "video_chat_participants_invited " in json -> VideoChatParticipantsInvitedMessage.serializer()
                "message_auto_delete_timer_changed" in json -> MessageAutoDeleteTimerChangedMessage.serializer()
                "video_chat_scheduled" in json -> VideoChatScheduledMessage.serializer()
                "invoice" in json -> InvoiceMessage.serializer()
                "successful_payment" in json -> SuccessfulPaymentMessage.serializer()
                else -> UnknownMessage.serializer()
            }
        }
    }
}

@Serializable(ServiceMessage.Serializer::class)
sealed class ServiceMessage : Message() {
    override val isTopicMessage: Boolean get() = false
    override val isAutomaticForward: Boolean get() = false
    override val hasProtectedContent: Boolean get() = true
    override val replyToMessage: Nothing? get() = null
    override val lastEditDate: Nothing? get() = null
    override val viaBot: Nothing? get() = null
    override val authorSignature: Nothing? get() = null
    override val mediaGroupId: Nothing? get() = null
    override val replyMarkup: Nothing? get() = null
    override val forwardFrom: Nothing? get() = null
    override val forwardFromMessageId: Nothing? get() = null
    override val forwardSignature: Nothing? get() = null
    override val forwardSenderName: Nothing? get() = null
    override val forwardDate: Nothing? get() = null

    override fun toMessageContent(): Nothing? = null

    object Serializer : KSerializer<ServiceMessage> {
        override val descriptor get() = Message.Serializer.descriptor
        override fun deserialize(decoder: Decoder) = Message.Serializer.deserialize(decoder) as ServiceMessage
        override fun serialize(encoder: Encoder, value: ServiceMessage) = Message.Serializer.serialize(encoder, value)
    }
}

@Serializable
data class TextMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("forward_from") override val forwardFrom: Sender? = null,
    @SerialName("forward_from_message_id") override val forwardFromMessageId: Long? = null,
    @SerialName("forward_signature") override val forwardSignature: String? = null,
    @SerialName("forward_sender_name") override val forwardSenderName: String? = null,
    @SerialName("forward_date") override val forwardDate: Long? = null,
    @SerialName("reply_to_message") override val replyToMessage: Message? = null,
    @SerialName("is_topic_message") override val isTopicMessage: Boolean = false,
    @SerialName("is_automatic_forward") override val isAutomaticForward: Boolean = false,
    @SerialName("via_bot") override val viaBot: Bot? = null,
    @SerialName("edit_date") override val lastEditDate: Long? = null,
    @SerialName("has_protected_content") override val hasProtectedContent: Boolean = false,
    @SerialName("media_group_id") override val mediaGroupId: String? = null,
    @SerialName("author_signature") override val authorSignature: String? = null,
    @SerialName("reply_markup") override val replyMarkup: InlineKeyboard? = null,
    @SerialName("text") val plainText: String,
    @SerialName("entities") val textEntities: List<MessageEntity> = emptyList(),
) : Message(), WithText {
    @Transient
    override val text = Text(plainText, textEntities)

    override fun toMessageContent(): TextContent {
        return TextContent(text)
    }
}

@Serializable
data class AnimationMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("forward_from") override val forwardFrom: Sender? = null,
    @SerialName("forward_from_message_id") override val forwardFromMessageId: Long? = null,
    @SerialName("forward_signature") override val forwardSignature: String? = null,
    @SerialName("forward_sender_name") override val forwardSenderName: String? = null,
    @SerialName("forward_date") override val forwardDate: Long? = null,
    @SerialName("reply_to_message") override val replyToMessage: Message? = null,
    @SerialName("is_topic_message") override val isTopicMessage: Boolean = false,
    @SerialName("is_automatic_forward") override val isAutomaticForward: Boolean = false,
    @SerialName("via_bot") override val viaBot: Bot? = null,
    @SerialName("edit_date") override val lastEditDate: Long? = null,
    @SerialName("has_protected_content") override val hasProtectedContent: Boolean = false,
    @SerialName("media_group_id") override val mediaGroupId: String? = null,
    @SerialName("author_signature") override val authorSignature: String? = null,
    @SerialName("reply_markup") override val replyMarkup: InlineKeyboard? = null,
    val animation: Animation,
    @SerialName("caption") val plainText: String,
    @SerialName("caption_entities") val textEntities: List<MessageEntity>,
) : Message(), WithText {
    @Transient
    override val text = Text(plainText, textEntities)

    override fun toMessageContent(): AnimationContent {
        return AnimationContent(
            animation = OutputFile.fromFileId(animation.fileId),
            caption = text,
            duration = animation.duration,
            width = animation.width,
            height = animation.height,
            thumbnail = animation.thumbnail?.fileId?.let { OutputFile.fromFileId(it) }
        )
    }
}

@Serializable
data class AudioMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("forward_from") override val forwardFrom: Sender? = null,
    @SerialName("forward_from_message_id") override val forwardFromMessageId: Long? = null,
    @SerialName("forward_signature") override val forwardSignature: String? = null,
    @SerialName("forward_sender_name") override val forwardSenderName: String? = null,
    @SerialName("forward_date") override val forwardDate: Long? = null,
    @SerialName("reply_to_message") override val replyToMessage: Message? = null,
    @SerialName("is_topic_message") override val isTopicMessage: Boolean = false,
    @SerialName("is_automatic_forward") override val isAutomaticForward: Boolean = false,
    @SerialName("via_bot") override val viaBot: Bot? = null,
    @SerialName("edit_date") override val lastEditDate: Long? = null,
    @SerialName("has_protected_content") override val hasProtectedContent: Boolean = false,
    @SerialName("media_group_id") override val mediaGroupId: String? = null,
    @SerialName("author_signature") override val authorSignature: String? = null,
    @SerialName("reply_markup") override val replyMarkup: InlineKeyboard? = null,
    val audio: Audio,
    @SerialName("caption") val plainText: String,
    @SerialName("caption_entities") val textEntities: List<MessageEntity>,
) : Message(), WithText {
    @Transient
    override val text = Text(plainText, textEntities)

    override fun toMessageContent(): AudioContent {
        return AudioContent(
            audio = OutputFile.fromFileId(audio.fileId),
            caption = text,
            duration = audio.duration,
            performer = audio.performer,
            title = audio.title,
            thumbnail = audio.thumbnail?.fileId?.let { OutputFile.fromFileId(it) }
        )
    }
}

@Serializable
data class DocumentMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("forward_from") override val forwardFrom: Sender? = null,
    @SerialName("forward_from_message_id") override val forwardFromMessageId: Long? = null,
    @SerialName("forward_signature") override val forwardSignature: String? = null,
    @SerialName("forward_sender_name") override val forwardSenderName: String? = null,
    @SerialName("forward_date") override val forwardDate: Long? = null,
    @SerialName("reply_to_message") override val replyToMessage: Message? = null,
    @SerialName("is_topic_message") override val isTopicMessage: Boolean = false,
    @SerialName("is_automatic_forward") override val isAutomaticForward: Boolean = false,
    @SerialName("via_bot") override val viaBot: Bot? = null,
    @SerialName("edit_date") override val lastEditDate: Long? = null,
    @SerialName("has_protected_content") override val hasProtectedContent: Boolean = false,
    @SerialName("media_group_id") override val mediaGroupId: String? = null,
    @SerialName("author_signature") override val authorSignature: String? = null,
    @SerialName("reply_markup") override val replyMarkup: InlineKeyboard? = null,
    val document: Document,
    @SerialName("caption") val plainText: String,
    @SerialName("caption_entities") val textEntities: List<MessageEntity>,
) : Message(), WithText {
    @Transient
    override val text = Text(plainText, textEntities)

    override fun toMessageContent(): DocumentContent {
        return DocumentContent(
            document = OutputFile.fromFileId(document.fileId),
            caption = text,
            thumbnail = document.thumbnail?.fileId?.let { OutputFile.fromFileId(it) }
        )
    }
}

@Serializable
data class PhotoMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("forward_from") override val forwardFrom: Sender? = null,
    @SerialName("forward_from_message_id") override val forwardFromMessageId: Long? = null,
    @SerialName("forward_signature") override val forwardSignature: String? = null,
    @SerialName("forward_sender_name") override val forwardSenderName: String? = null,
    @SerialName("forward_date") override val forwardDate: Long? = null,
    @SerialName("reply_to_message") override val replyToMessage: Message? = null,
    @SerialName("is_topic_message") override val isTopicMessage: Boolean = false,
    @SerialName("is_automatic_forward") override val isAutomaticForward: Boolean = false,
    @SerialName("via_bot") override val viaBot: Bot? = null,
    @SerialName("edit_date") override val lastEditDate: Long? = null,
    @SerialName("has_protected_content") override val hasProtectedContent: Boolean = false,
    @SerialName("media_group_id") override val mediaGroupId: String? = null,
    @SerialName("author_signature") override val authorSignature: String? = null,
    @SerialName("reply_markup") override val replyMarkup: InlineKeyboard? = null,
    val photo: List<PhotoSize>,
    @SerialName("caption") val plainText: String,
    @SerialName("caption_entities") val textEntities: List<MessageEntity>,
) : Message(), WithText {
    @Transient
    override val text = Text(plainText, textEntities)

    override fun toMessageContent(): PhotoContent {
        return PhotoContent(OutputFile.fromFileId(photo.first().fileId), text)
    }
}

@Serializable
data class StickerMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("forward_from") override val forwardFrom: Sender? = null,
    @SerialName("forward_from_message_id") override val forwardFromMessageId: Long? = null,
    @SerialName("forward_signature") override val forwardSignature: String? = null,
    @SerialName("forward_sender_name") override val forwardSenderName: String? = null,
    @SerialName("forward_date") override val forwardDate: Long? = null,
    @SerialName("reply_to_message") override val replyToMessage: Message? = null,
    @SerialName("is_topic_message") override val isTopicMessage: Boolean = false,
    @SerialName("is_automatic_forward") override val isAutomaticForward: Boolean = false,
    @SerialName("via_bot") override val viaBot: Bot? = null,
    @SerialName("edit_date") override val lastEditDate: Long? = null,
    @SerialName("has_protected_content") override val hasProtectedContent: Boolean = false,
    @SerialName("media_group_id") override val mediaGroupId: String? = null,
    @SerialName("author_signature") override val authorSignature: String? = null,
    @SerialName("reply_markup") override val replyMarkup: InlineKeyboard? = null,
    val sticker: Sticker,
) : Message() {
    override fun toMessageContent(): StickerContent {
        return StickerContent(OutputFile.fromFileId(sticker.fileId))
    }
}

@Serializable
data class VideoMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("forward_from") override val forwardFrom: Sender? = null,
    @SerialName("forward_from_message_id") override val forwardFromMessageId: Long? = null,
    @SerialName("forward_signature") override val forwardSignature: String? = null,
    @SerialName("forward_sender_name") override val forwardSenderName: String? = null,
    @SerialName("forward_date") override val forwardDate: Long? = null,
    @SerialName("reply_to_message") override val replyToMessage: Message? = null,
    @SerialName("is_topic_message") override val isTopicMessage: Boolean = false,
    @SerialName("is_automatic_forward") override val isAutomaticForward: Boolean = false,
    @SerialName("via_bot") override val viaBot: Bot? = null,
    @SerialName("edit_date") override val lastEditDate: Long? = null,
    @SerialName("has_protected_content") override val hasProtectedContent: Boolean = false,
    @SerialName("media_group_id") override val mediaGroupId: String? = null,
    @SerialName("author_signature") override val authorSignature: String? = null,
    @SerialName("reply_markup") override val replyMarkup: InlineKeyboard? = null,
    val video: Video,
    @SerialName("caption") val plainText: String,
    @SerialName("caption_entities") val textEntities: List<MessageEntity>,
) : Message(), WithText {
    @Transient
    override val text = Text(plainText, textEntities)

    override fun toMessageContent(): VideoContent {
        return VideoContent(
            video = OutputFile.fromFileId(video.fileId),
            caption = text,
            duration = video.duration,
            width = video.width,
            height = video.height,
            thumbnail = video.thumbnail?.fileId?.let { OutputFile.fromFileId(it) }
        )
    }
}

@Serializable
data class VideoNoteMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("forward_from") override val forwardFrom: Sender? = null,
    @SerialName("forward_from_message_id") override val forwardFromMessageId: Long? = null,
    @SerialName("forward_signature") override val forwardSignature: String? = null,
    @SerialName("forward_sender_name") override val forwardSenderName: String? = null,
    @SerialName("forward_date") override val forwardDate: Long? = null,
    @SerialName("reply_to_message") override val replyToMessage: Message? = null,
    @SerialName("is_topic_message") override val isTopicMessage: Boolean = false,
    @SerialName("is_automatic_forward") override val isAutomaticForward: Boolean = false,
    @SerialName("via_bot") override val viaBot: Bot? = null,
    @SerialName("edit_date") override val lastEditDate: Long? = null,
    @SerialName("has_protected_content") override val hasProtectedContent: Boolean = false,
    @SerialName("media_group_id") override val mediaGroupId: String? = null,
    @SerialName("author_signature") override val authorSignature: String? = null,
    @SerialName("reply_markup") override val replyMarkup: InlineKeyboard? = null,
    @SerialName("video_note") val videoNote: VideoNote
) : Message() {
    override fun toMessageContent(): VideoNoteContent {
        return VideoNoteContent(
            videoNote = OutputFile.fromFileId(videoNote.fileId),
            duration = videoNote.duration,
            length = videoNote.length,
            thumbnail = videoNote.thumbnail?.fileId?.let { OutputFile.fromFileId(it) }
        )
    }
}

@Serializable
data class VoiceMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("forward_from") override val forwardFrom: Sender? = null,
    @SerialName("forward_from_message_id") override val forwardFromMessageId: Long? = null,
    @SerialName("forward_signature") override val forwardSignature: String? = null,
    @SerialName("forward_sender_name") override val forwardSenderName: String? = null,
    @SerialName("forward_date") override val forwardDate: Long? = null,
    @SerialName("reply_to_message") override val replyToMessage: Message? = null,
    @SerialName("is_topic_message") override val isTopicMessage: Boolean = false,
    @SerialName("is_automatic_forward") override val isAutomaticForward: Boolean = false,
    @SerialName("via_bot") override val viaBot: Bot? = null,
    @SerialName("edit_date") override val lastEditDate: Long? = null,
    @SerialName("has_protected_content") override val hasProtectedContent: Boolean = false,
    @SerialName("media_group_id") override val mediaGroupId: String? = null,
    @SerialName("author_signature") override val authorSignature: String? = null,
    @SerialName("reply_markup") override val replyMarkup: InlineKeyboard? = null,
    val voice: Voice,
    @SerialName("caption") val plainText: String,
    @SerialName("caption_entities") val textEntities: List<MessageEntity>,
) : Message(), WithText {
    @Transient
    override val text = Text(plainText, textEntities)

    override fun toMessageContent(): VoiceContent {
        return VoiceContent(OutputFile.fromFileId(voice.fileId), text, voice.duration)
    }
}

@Serializable
data class ContactMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("forward_from") override val forwardFrom: Sender? = null,
    @SerialName("forward_from_message_id") override val forwardFromMessageId: Long? = null,
    @SerialName("forward_signature") override val forwardSignature: String? = null,
    @SerialName("forward_sender_name") override val forwardSenderName: String? = null,
    @SerialName("forward_date") override val forwardDate: Long? = null,
    @SerialName("reply_to_message") override val replyToMessage: Message? = null,
    @SerialName("is_topic_message") override val isTopicMessage: Boolean = false,
    @SerialName("is_automatic_forward") override val isAutomaticForward: Boolean = false,
    @SerialName("via_bot") override val viaBot: Bot? = null,
    @SerialName("edit_date") override val lastEditDate: Long? = null,
    @SerialName("has_protected_content") override val hasProtectedContent: Boolean = false,
    @SerialName("media_group_id") override val mediaGroupId: String? = null,
    @SerialName("author_signature") override val authorSignature: String? = null,
    @SerialName("reply_markup") override val replyMarkup: InlineKeyboard? = null,
    val contact: Contact,
) : Message() {
    override fun toMessageContent(): ContactContent {
        return ContactContent(
            contact.phoneNumber,
            contact.firstName,
            contact.lastName,
            contact.vcard
        )
    }
}

@Serializable
data class DiceMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("forward_from") override val forwardFrom: Sender? = null,
    @SerialName("forward_from_message_id") override val forwardFromMessageId: Long? = null,
    @SerialName("forward_signature") override val forwardSignature: String? = null,
    @SerialName("forward_sender_name") override val forwardSenderName: String? = null,
    @SerialName("forward_date") override val forwardDate: Long? = null,
    @SerialName("reply_to_message") override val replyToMessage: Message? = null,
    @SerialName("is_topic_message") override val isTopicMessage: Boolean = false,
    @SerialName("is_automatic_forward") override val isAutomaticForward: Boolean = false,
    @SerialName("via_bot") override val viaBot: Bot? = null,
    @SerialName("edit_date") override val lastEditDate: Long? = null,
    @SerialName("has_protected_content") override val hasProtectedContent: Boolean = false,
    @SerialName("media_group_id") override val mediaGroupId: String? = null,
    @SerialName("author_signature") override val authorSignature: String? = null,
    @SerialName("reply_markup") override val replyMarkup: InlineKeyboard? = null,
    val dice: Dice,
) : Message() {
    override fun toMessageContent(): DiceContent {
        return DiceContent(dice.emoji)
    }
}

@Serializable
data class GameMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("forward_from") override val forwardFrom: Sender? = null,
    @SerialName("forward_from_message_id") override val forwardFromMessageId: Long? = null,
    @SerialName("forward_signature") override val forwardSignature: String? = null,
    @SerialName("forward_sender_name") override val forwardSenderName: String? = null,
    @SerialName("forward_date") override val forwardDate: Long? = null,
    @SerialName("reply_to_message") override val replyToMessage: Message? = null,
    @SerialName("is_topic_message") override val isTopicMessage: Boolean = false,
    @SerialName("is_automatic_forward") override val isAutomaticForward: Boolean = false,
    @SerialName("via_bot") override val viaBot: Bot? = null,
    @SerialName("edit_date") override val lastEditDate: Long? = null,
    @SerialName("has_protected_content") override val hasProtectedContent: Boolean = false,
    @SerialName("media_group_id") override val mediaGroupId: String? = null,
    @SerialName("author_signature") override val authorSignature: String? = null,
    @SerialName("reply_markup") override val replyMarkup: InlineKeyboard? = null,
    val game: Game,
) : Message() {
    override fun toMessageContent(): Nothing? = null
}

@Serializable
data class PollMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("forward_from") override val forwardFrom: Sender? = null,
    @SerialName("forward_from_message_id") override val forwardFromMessageId: Long? = null,
    @SerialName("forward_signature") override val forwardSignature: String? = null,
    @SerialName("forward_sender_name") override val forwardSenderName: String? = null,
    @SerialName("forward_date") override val forwardDate: Long? = null,
    @SerialName("reply_to_message") override val replyToMessage: Message? = null,
    @SerialName("is_topic_message") override val isTopicMessage: Boolean = false,
    @SerialName("is_automatic_forward") override val isAutomaticForward: Boolean = false,
    @SerialName("via_bot") override val viaBot: Bot? = null,
    @SerialName("edit_date") override val lastEditDate: Long? = null,
    @SerialName("has_protected_content") override val hasProtectedContent: Boolean = false,
    @SerialName("media_group_id") override val mediaGroupId: String? = null,
    @SerialName("author_signature") override val authorSignature: String? = null,
    @SerialName("reply_markup") override val replyMarkup: InlineKeyboard? = null,
    val poll: Poll,
) : Message() {
    override fun toMessageContent(): PollContent? {
        return when (poll) {
            is Poll.Regular -> PollContent.Regular(
                question = poll.question,
                options = poll.options.map { it.text },
                isAnonymous = poll.isAnonymous,
                openPeriod = poll.openPeriod,
                closeDate = poll.closeDate,
                isClosed = poll.isClosed,
                allowsMultipleAnswers = poll.allowsMultipleAnswers
            )
            is Poll.Quiz -> if (poll.correctOptionId != null) PollContent.Quiz(
                question = poll.question,
                options = poll.options.map { it.text },
                isAnonymous = poll.isAnonymous,
                openPeriod = poll.openPeriod,
                closeDate = poll.closeDate,
                isClosed = poll.isClosed,
                correctOption = poll.correctOptionId,
                explanation = poll.explanation
            ) else null
        }
    }
}

@Serializable
data class VenueMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("forward_from") override val forwardFrom: Sender? = null,
    @SerialName("forward_from_message_id") override val forwardFromMessageId: Long? = null,
    @SerialName("forward_signature") override val forwardSignature: String? = null,
    @SerialName("forward_sender_name") override val forwardSenderName: String? = null,
    @SerialName("forward_date") override val forwardDate: Long? = null,
    @SerialName("reply_to_message") override val replyToMessage: Message? = null,
    @SerialName("is_topic_message") override val isTopicMessage: Boolean = false,
    @SerialName("is_automatic_forward") override val isAutomaticForward: Boolean = false,
    @SerialName("via_bot") override val viaBot: Bot? = null,
    @SerialName("edit_date") override val lastEditDate: Long? = null,
    @SerialName("has_protected_content") override val hasProtectedContent: Boolean = false,
    @SerialName("media_group_id") override val mediaGroupId: String? = null,
    @SerialName("author_signature") override val authorSignature: String? = null,
    @SerialName("reply_markup") override val replyMarkup: InlineKeyboard? = null,
    val venue: Venue,
) : Message() {
    override fun toMessageContent(): VenueContent {
        return VenueContent(venue)
    }
}

@Serializable
data class LocationMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("forward_from") override val forwardFrom: Sender? = null,
    @SerialName("forward_from_message_id") override val forwardFromMessageId: Long? = null,
    @SerialName("forward_signature") override val forwardSignature: String? = null,
    @SerialName("forward_sender_name") override val forwardSenderName: String? = null,
    @SerialName("forward_date") override val forwardDate: Long? = null,
    @SerialName("reply_to_message") override val replyToMessage: Message? = null,
    @SerialName("is_topic_message") override val isTopicMessage: Boolean = false,
    @SerialName("is_automatic_forward") override val isAutomaticForward: Boolean = false,
    @SerialName("via_bot") override val viaBot: Bot? = null,
    @SerialName("edit_date") override val lastEditDate: Long? = null,
    @SerialName("has_protected_content") override val hasProtectedContent: Boolean = false,
    @SerialName("media_group_id") override val mediaGroupId: String? = null,
    @SerialName("author_signature") override val authorSignature: String? = null,
    @SerialName("reply_markup") override val replyMarkup: InlineKeyboard? = null,
    val location: Location,
) : Message() {
    override fun toMessageContent(): LocationContent {
        return LocationContent(location)
    }
}

@Serializable
data class NewChatMembersMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("new_chat_members") val newChatMembers: List<Sender>,
) : ServiceMessage()

@Serializable
data class LeftChatMemberMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("left_chat_member") val leftChatMember: Sender
) : ServiceMessage()

@Serializable
data class NewChatTitleMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("new_chat_title") val newChatTitle: String
) : ServiceMessage()

@Serializable
data class NewChatPhotoMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("new_chat_photo") val newChatPhoto: List<PhotoSize>
) : ServiceMessage()

@Serializable
data class DeleteChatPhotoMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
) : ServiceMessage()

@Serializable
data class GroupChatCreatedMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Group,
) : ServiceMessage()

@Serializable
data class SupergroupChatCreatedMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Supergroup,
) : ServiceMessage()

@Serializable
data class ChannelChatCreatedMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Channel,
) : ServiceMessage()

@Serializable
data class MigrateToChatIdMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("migrate_to_chat_id") val migrateToChatId: Long
) : ServiceMessage()

@Serializable
data class MigrateFromChatIdMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("migrate_from_chat_id") val migrateFromChatId: Long
) : ServiceMessage()

@Serializable
data class PinnedMessageMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("pinned_message") val pinnedMessage: Message
) : ServiceMessage()

@Serializable
data class ProximityAlertTriggeredMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("proximity_alert_triggered") val proximityAlertTriggered: ProximityAlertTriggered
) : ServiceMessage()

@Deprecated("Use VideoChatStartedMessage", ReplaceWith("VideoChatStartedMessage"))
typealias VoiceChatStartedMessage = VideoChatStartedMessage

@Serializable
data class VideoChatStartedMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("video_chat_started") val voiceChatStarted: VideoChatStarted
) : ServiceMessage() {
    val videoChatStarted get() = voiceChatStarted
}

@Deprecated("Use VideoChatEndedMessage", ReplaceWith("VideoChatEndedMessage"))
typealias VoiceChatEndedMessage = VideoChatEndedMessage

@Serializable
data class VideoChatEndedMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("video_chat_ended") val voiceChatEnded: VideoChatEnded
) : ServiceMessage() {
    val videoChatEnded get() = voiceChatEnded
}

@Deprecated("Use VideoChatParticipantsInvitedMessage", ReplaceWith("VideoChatParticipantsInvitedMessage"))
typealias VoiceChatParticipantsInvitedMessage = VideoChatParticipantsInvitedMessage

@Serializable
data class VideoChatParticipantsInvitedMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("video_chat_participants_invited") val voiceChatParticipantsInvited: VideoChatParticipantsInvited
) : ServiceMessage() {
    val videoChatParticipantsInvited get() = voiceChatParticipantsInvited
}

@Serializable
data class MessageAutoDeleteTimerChangedMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("message_auto_delete_timer_changed") val messageAutoDeleteTimerChanged: MessageAutoDeleteTimerChanged
) : ServiceMessage()

@Deprecated("Use VideoChatScheduledMessage", ReplaceWith("VideoChatScheduledMessage"))
typealias VoiceChatScheduledMessage = VideoChatScheduledMessage

@Serializable
data class VideoChatScheduledMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("video_chat_scheduled") val voiceChatScheduled: VideoChatScheduled
) : ServiceMessage() {
    val videoChatScheduled get() = voiceChatScheduled
}

@Serializable
data class InvoiceMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("forward_from") override val forwardFrom: Sender? = null,
    @SerialName("forward_from_message_id") override val forwardFromMessageId: Long? = null,
    @SerialName("forward_signature") override val forwardSignature: String? = null,
    @SerialName("forward_sender_name") override val forwardSenderName: String? = null,
    @SerialName("forward_date") override val forwardDate: Long? = null,
    @SerialName("reply_to_message") override val replyToMessage: Message? = null,
    @SerialName("is_topic_message") override val isTopicMessage: Boolean = false,
    @SerialName("is_automatic_forward") override val isAutomaticForward: Boolean = false,
    @SerialName("via_bot") override val viaBot: Bot? = null,
    @SerialName("edit_date") override val lastEditDate: Long? = null,
    @SerialName("has_protected_content") override val hasProtectedContent: Boolean = false,
    @SerialName("media_group_id") override val mediaGroupId: String? = null,
    @SerialName("author_signature") override val authorSignature: String? = null,
    @SerialName("reply_markup") override val replyMarkup: InlineKeyboard? = null,
    val invoice: Invoice
) : Message() {
    override fun toMessageContent(): Nothing? = null
}

@Serializable
data class SuccessfulPaymentMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("successful_payment") val successfulPayment: SuccessfulPayment
) : ServiceMessage()

@Serializable
data class ForumTopicCreatedMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("forum_topic_created") val forumTopicCreated: ForumTopicCreated
) : ServiceMessage()

@Serializable
data class ForumTopicClosedMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("forum_topic_closed") val forumTopicClosed: ForumTopicClosed
) : ServiceMessage()

@Serializable
data class ForumTopicReopenedMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("forum_topic_reopened") val forumTopicReopened: ForumTopicReopened
) : ServiceMessage()

@Serializable
data class UnknownMessage(
    @SerialName("message_id") override val id: Long,
    @SerialName("message_thread_id") override val messageThreadId: Long? = null,
    @JsonNames("sender_char")
    @SerialName("from") override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    @SerialName("forward_from") override val forwardFrom: Sender? = null,
    @SerialName("forward_from_message_id") override val forwardFromMessageId: Long? = null,
    @SerialName("forward_signature") override val forwardSignature: String? = null,
    @SerialName("forward_sender_name") override val forwardSenderName: String? = null,
    @SerialName("forward_date") override val forwardDate: Long? = null,
    @SerialName("reply_to_message") override val replyToMessage: Message? = null,
    @SerialName("is_topic_message") override val isTopicMessage: Boolean = false,
    @SerialName("is_automatic_forward") override val isAutomaticForward: Boolean = false,
    @SerialName("via_bot") override val viaBot: Bot? = null,
    @SerialName("edit_date") override val lastEditDate: Long? = null,
    @SerialName("has_protected_content") override val hasProtectedContent: Boolean = false,
    @SerialName("media_group_id") override val mediaGroupId: String? = null,
    @SerialName("author_signature") override val authorSignature: String? = null,
    @SerialName("reply_markup") override val replyMarkup: InlineKeyboard? = null,
) : Message() {
    override fun toMessageContent(): Nothing? = null
}

val Message.chatId get() = chat.chatId
val Message.messageId get() = MessageId(chatId, id)
