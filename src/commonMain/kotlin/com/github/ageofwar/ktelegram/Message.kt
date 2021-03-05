package com.github.ageofwar.ktelegram

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.ClassSerialDescriptorBuilder
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable(Message.Serializer::class)
sealed class Message : Id<Long> {
    abstract val sender: Sender
    abstract val date: Long
    abstract val chat: Chat
    abstract val replyToMessage: Message?
    abstract val viaBot: Bot?
    abstract val lastEditDate: Long?
    abstract val mediaGroupId: String?
    abstract val authorSignature: String?
    abstract val replyMarkup: InlineKeyboard?
    abstract val forwardFrom: Sender?
    abstract val forwardFromMessageId: Long?
    abstract val forwardSignature: String?
    abstract val forwardSenderName: String?
    abstract val forwardDate: Long?

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
                "proximity_alert_triggered" in json -> ProximityAlertTriggeredMessage.serializer()
                else -> UnknownMessage.serializer()
            }
        }
    }
}

@Serializable(TextMessage.Serializer::class)
data class TextMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    override val replyToMessage: Message? = null,
    override val viaBot: Bot? = null,
    override val lastEditDate: Long? = null,
    override val mediaGroupId: String? = null,
    override val authorSignature: String? = null,
    override val text: Text,
    override val replyMarkup: InlineKeyboard? = null,
    override val forwardFrom: Sender? = null,
    override val forwardFromMessageId: Long? = null,
    override val forwardSignature: String? = null,
    override val forwardSenderName: String? = null,
    override val forwardDate: Long? = null
) : Message(), WithText {
    override fun toMessageContent(): TextContent {
        return TextContent(text)
    }

    object Serializer : KSerializer<TextMessage> {
        override val descriptor = buildClassSerialDescriptor("TextMessage") {
            messageElements()
            element<String?>("text")
            element<List<MessageEntity>>("entities")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var replyToMessage: Message? = null
            var viaBot: Bot? = null
            var lastEditDate: Long? = null
            var mediaGroupId: String? = null
            var authorSignature: String? = null
            var forwardFrom: Sender? = null
            var forwardFromMessageId: Long? = null
            var forwardSignature: String? = null
            var forwardSenderName: String? = null
            var forwardDate: Long? = null
            var replyMarkup: InlineKeyboard? = null
            var text: String? = null
            var textEntities: List<MessageEntity>? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    5 -> replyToMessage = decodeSerializableElement(
                        descriptor,
                        5,
                        Message.serializer(),
                        replyToMessage
                    )
                    6 -> viaBot = decodeSerializableElement(descriptor, 6, Bot.serializer(), viaBot)
                    7 -> lastEditDate = decodeLongElement(descriptor, 7)
                    8 -> mediaGroupId = decodeStringElement(descriptor, 8)
                    9 -> authorSignature = decodeStringElement(descriptor, 9)
                    10 -> forwardFrom =
                        decodeSerializableElement(descriptor, 10, Sender.serializer(), forwardFrom)
                    11 -> forwardFrom =
                        decodeSerializableElement(descriptor, 11, Sender.serializer(), forwardFrom)
                    12 -> forwardFromMessageId = decodeLongElement(descriptor, 12)
                    13 -> forwardSignature = decodeStringElement(descriptor, 13)
                    14 -> forwardSenderName = decodeStringElement(descriptor, 14)
                    15 -> forwardDate = decodeLongElement(descriptor, 15)
                    16 -> replyMarkup = decodeSerializableElement(
                        descriptor,
                        16,
                        InlineKeyboard.serializer(),
                        replyMarkup
                    )
                    17 -> text = decodeStringElement(descriptor, 17)
                    18 -> textEntities = decodeSerializableElement(
                        descriptor,
                        18,
                        ListSerializer(MessageEntity.serializer()),
                        textEntities
                    )
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            requireNotNull(text)
            TextMessage(
                id,
                sender,
                date,
                chat,
                replyToMessage,
                viaBot,
                lastEditDate,
                mediaGroupId,
                authorSignature,
                Text(text, textEntities ?: emptyList()),
                replyMarkup,
                forwardFrom,
                forwardFromMessageId,
                forwardSignature,
                forwardSenderName,
                forwardDate
            )
        }

        override fun serialize(encoder: Encoder, value: TextMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat, replyToMessage, viaBot, lastEditDate, mediaGroupId, authorSignature, text, replyMarkup, forwardFrom, forwardFromMessageId, forwardSignature, forwardSenderName, forwardDate) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                if (replyToMessage != null) encodeSerializableElement(
                    descriptor,
                    5,
                    Message.serializer(),
                    replyToMessage
                )
                if (viaBot != null) encodeSerializableElement(
                    descriptor,
                    6,
                    Bot.serializer(),
                    viaBot
                )
                if (lastEditDate != null) encodeLongElement(descriptor, 7, lastEditDate)
                if (mediaGroupId != null) encodeStringElement(descriptor, 8, mediaGroupId)
                if (authorSignature != null) encodeStringElement(descriptor, 9, authorSignature)
                when (forwardFrom) {
                    is User, is Bot -> encodeSerializableElement(
                        descriptor,
                        10,
                        Sender.serializer(),
                        forwardFrom
                    )
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        11,
                        Sender.serializer(),
                        forwardFrom
                    )
                }
                if (forwardFromMessageId != null) encodeLongElement(
                    descriptor,
                    12,
                    forwardFromMessageId
                )
                if (forwardSignature != null) encodeStringElement(descriptor, 13, forwardSignature)
                if (forwardSenderName != null) encodeStringElement(
                    descriptor,
                    14,
                    forwardSenderName
                )
                if (forwardDate != null) encodeLongElement(descriptor, 15, forwardDate)
                if (replyMarkup != null) encodeSerializableElement(
                    descriptor,
                    16,
                    InlineKeyboard.serializer(),
                    replyMarkup
                )
                encodeStringElement(descriptor, 17, text.toString())
                if (text.entities.isNotEmpty()) encodeSerializableElement(
                    descriptor,
                    18,
                    ListSerializer(MessageEntity.serializer()),
                    text.entities
                )
            }
        }
    }
}

@Serializable(AnimationMessage.Serializer::class)
data class AnimationMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    override val replyToMessage: Message? = null,
    override val viaBot: Bot? = null,
    override val lastEditDate: Long? = null,
    override val mediaGroupId: String? = null,
    override val authorSignature: String? = null,
    val animation: Animation,
    override val text: Text?,
    override val replyMarkup: InlineKeyboard? = null,
    override val forwardFrom: Sender? = null,
    override val forwardFromMessageId: Long? = null,
    override val forwardSignature: String? = null,
    override val forwardSenderName: String? = null,
    override val forwardDate: Long? = null
) : Message(), WithText {
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

    object Serializer : KSerializer<AnimationMessage> {
        override val descriptor = buildClassSerialDescriptor("AnimationMessage") {
            messageElements()
            element<Animation>("animation")
            element<String?>("caption")
            element<List<MessageEntity>>("caption_entities")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var replyToMessage: Message? = null
            var viaBot: Bot? = null
            var lastEditDate: Long? = null
            var mediaGroupId: String? = null
            var authorSignature: String? = null
            var forwardFrom: Sender? = null
            var forwardFromMessageId: Long? = null
            var forwardSignature: String? = null
            var forwardSenderName: String? = null
            var forwardDate: Long? = null
            var replyMarkup: InlineKeyboard? = null
            var animation: Animation? = null
            var caption: String? = null
            var captionEntities: List<MessageEntity>? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    5 -> replyToMessage = decodeSerializableElement(
                        descriptor,
                        5,
                        Message.serializer(),
                        replyToMessage
                    )
                    6 -> viaBot = decodeSerializableElement(descriptor, 6, Bot.serializer(), viaBot)
                    7 -> lastEditDate = decodeLongElement(descriptor, 7)
                    8 -> mediaGroupId = decodeStringElement(descriptor, 8)
                    9 -> authorSignature = decodeStringElement(descriptor, 9)
                    10 -> forwardFrom =
                        decodeSerializableElement(descriptor, 10, Sender.serializer(), forwardFrom)
                    11 -> forwardFrom =
                        decodeSerializableElement(descriptor, 11, Sender.serializer(), forwardFrom)
                    12 -> forwardFromMessageId = decodeLongElement(descriptor, 12)
                    13 -> forwardSignature = decodeStringElement(descriptor, 13)
                    14 -> forwardSenderName = decodeStringElement(descriptor, 14)
                    15 -> forwardDate = decodeLongElement(descriptor, 15)
                    16 -> replyMarkup = decodeSerializableElement(
                        descriptor,
                        16,
                        InlineKeyboard.serializer(),
                        replyMarkup
                    )
                    17 -> animation =
                        decodeSerializableElement(descriptor, 17, Animation.serializer(), animation)
                    18 -> caption = decodeStringElement(descriptor, 18)
                    19 -> captionEntities = decodeSerializableElement(
                        descriptor,
                        19,
                        ListSerializer(MessageEntity.serializer()),
                        captionEntities
                    )
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            requireNotNull(animation)
            val text = caption?.let { Text(it, captionEntities ?: emptyList()) }
            AnimationMessage(
                id,
                sender,
                date,
                chat,
                replyToMessage,
                viaBot,
                lastEditDate,
                mediaGroupId,
                authorSignature,
                animation,
                text,
                replyMarkup,
                forwardFrom,
                forwardFromMessageId,
                forwardSignature,
                forwardSenderName,
                forwardDate
            )
        }

        override fun serialize(encoder: Encoder, value: AnimationMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat, replyToMessage, viaBot, lastEditDate, mediaGroupId, authorSignature, animation, text, replyMarkup, forwardFrom, forwardFromMessageId, forwardSignature, forwardSenderName, forwardDate) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                if (replyToMessage != null) encodeSerializableElement(
                    descriptor,
                    5,
                    Message.serializer(),
                    replyToMessage
                )
                if (viaBot != null) encodeSerializableElement(
                    descriptor,
                    6,
                    Bot.serializer(),
                    viaBot
                )
                if (lastEditDate != null) encodeLongElement(descriptor, 7, lastEditDate)
                if (mediaGroupId != null) encodeStringElement(descriptor, 8, mediaGroupId)
                if (authorSignature != null) encodeStringElement(descriptor, 9, authorSignature)
                when (forwardFrom) {
                    is User, is Bot -> encodeSerializableElement(
                        descriptor,
                        10,
                        Sender.serializer(),
                        forwardFrom
                    )
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        11,
                        Sender.serializer(),
                        forwardFrom
                    )
                }
                if (forwardFromMessageId != null) encodeLongElement(
                    descriptor,
                    12,
                    forwardFromMessageId
                )
                if (forwardSignature != null) encodeStringElement(descriptor, 13, forwardSignature)
                if (forwardSenderName != null) encodeStringElement(
                    descriptor,
                    14,
                    forwardSenderName
                )
                if (forwardDate != null) encodeLongElement(descriptor, 15, forwardDate)
                if (replyMarkup != null) encodeSerializableElement(
                    descriptor,
                    16,
                    InlineKeyboard.serializer(),
                    replyMarkup
                )
                encodeSerializableElement(descriptor, 17, Animation.serializer(), animation)
                if (text != null) encodeStringElement(descriptor, 18, text.toString())
                if (text != null && text.entities.isNotEmpty()) encodeSerializableElement(
                    descriptor,
                    19,
                    ListSerializer(MessageEntity.serializer()),
                    text.entities
                )
            }
        }
    }
}

@Serializable(AudioMessage.Serializer::class)
data class AudioMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    override val replyToMessage: Message? = null,
    override val viaBot: Bot? = null,
    override val lastEditDate: Long? = null,
    override val mediaGroupId: String? = null,
    override val authorSignature: String? = null,
    val audio: Audio,
    override val text: Text? = null,
    override val replyMarkup: InlineKeyboard? = null,
    override val forwardFrom: Sender? = null,
    override val forwardFromMessageId: Long? = null,
    override val forwardSignature: String? = null,
    override val forwardSenderName: String? = null,
    override val forwardDate: Long? = null
) : Message(), WithText {
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

    object Serializer : KSerializer<AudioMessage> {
        override val descriptor = buildClassSerialDescriptor("AudioMessage") {
            messageElements()
            element<Audio>("audio")
            element<String?>("caption")
            element<List<MessageEntity>>("caption_entities")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var replyToMessage: Message? = null
            var viaBot: Bot? = null
            var lastEditDate: Long? = null
            var mediaGroupId: String? = null
            var authorSignature: String? = null
            var forwardFrom: Sender? = null
            var forwardFromMessageId: Long? = null
            var forwardSignature: String? = null
            var forwardSenderName: String? = null
            var forwardDate: Long? = null
            var replyMarkup: InlineKeyboard? = null
            var audio: Audio? = null
            var caption: String? = null
            var captionEntities: List<MessageEntity>? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    5 -> replyToMessage = decodeSerializableElement(
                        descriptor,
                        5,
                        Message.serializer(),
                        replyToMessage
                    )
                    6 -> viaBot = decodeSerializableElement(descriptor, 6, Bot.serializer(), viaBot)
                    7 -> lastEditDate = decodeLongElement(descriptor, 7)
                    8 -> mediaGroupId = decodeStringElement(descriptor, 8)
                    9 -> authorSignature = decodeStringElement(descriptor, 9)
                    10 -> forwardFrom =
                        decodeSerializableElement(descriptor, 10, Sender.serializer(), forwardFrom)
                    11 -> forwardFrom =
                        decodeSerializableElement(descriptor, 11, Sender.serializer(), forwardFrom)
                    12 -> forwardFromMessageId = decodeLongElement(descriptor, 12)
                    13 -> forwardSignature = decodeStringElement(descriptor, 13)
                    14 -> forwardSenderName = decodeStringElement(descriptor, 14)
                    15 -> forwardDate = decodeLongElement(descriptor, 15)
                    16 -> replyMarkup = decodeSerializableElement(
                        descriptor,
                        16,
                        InlineKeyboard.serializer(),
                        replyMarkup
                    )
                    17 -> audio =
                        decodeSerializableElement(descriptor, 17, Audio.serializer(), audio)
                    18 -> caption = decodeStringElement(descriptor, 18)
                    19 -> captionEntities = decodeSerializableElement(
                        descriptor,
                        19,
                        ListSerializer(MessageEntity.serializer()),
                        captionEntities
                    )
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            requireNotNull(audio)
            val text = caption?.let { Text(it, captionEntities ?: emptyList()) }
            AudioMessage(
                id,
                sender,
                date,
                chat,
                replyToMessage,
                viaBot,
                lastEditDate,
                mediaGroupId,
                authorSignature,
                audio,
                text,
                replyMarkup,
                forwardFrom,
                forwardFromMessageId,
                forwardSignature,
                forwardSenderName,
                forwardDate
            )
        }

        override fun serialize(encoder: Encoder, value: AudioMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat, replyToMessage, viaBot, lastEditDate, mediaGroupId, authorSignature, audio, text, replyMarkup, forwardFrom, forwardFromMessageId, forwardSignature, forwardSenderName, forwardDate) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                if (replyToMessage != null) encodeSerializableElement(
                    descriptor,
                    5,
                    Message.serializer(),
                    replyToMessage
                )
                if (viaBot != null) encodeSerializableElement(
                    descriptor,
                    6,
                    Bot.serializer(),
                    viaBot
                )
                if (lastEditDate != null) encodeLongElement(descriptor, 7, lastEditDate)
                if (mediaGroupId != null) encodeStringElement(descriptor, 8, mediaGroupId)
                if (authorSignature != null) encodeStringElement(descriptor, 9, authorSignature)
                when (forwardFrom) {
                    is User, is Bot -> encodeSerializableElement(
                        descriptor,
                        10,
                        Sender.serializer(),
                        forwardFrom
                    )
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        11,
                        Sender.serializer(),
                        forwardFrom
                    )
                }
                if (forwardFromMessageId != null) encodeLongElement(
                    descriptor,
                    12,
                    forwardFromMessageId
                )
                if (forwardSignature != null) encodeStringElement(descriptor, 13, forwardSignature)
                if (forwardSenderName != null) encodeStringElement(
                    descriptor,
                    14,
                    forwardSenderName
                )
                if (forwardDate != null) encodeLongElement(descriptor, 15, forwardDate)
                if (replyMarkup != null) encodeSerializableElement(
                    descriptor,
                    16,
                    InlineKeyboard.serializer(),
                    replyMarkup
                )
                encodeSerializableElement(descriptor, 17, Audio.serializer(), audio)
                if (text != null) encodeStringElement(descriptor, 18, text.toString())
                if (text != null && text.entities.isNotEmpty()) encodeSerializableElement(
                    descriptor,
                    19,
                    ListSerializer(MessageEntity.serializer()),
                    text.entities
                )
            }
        }
    }
}

@Serializable(DocumentMessage.Serializer::class)
data class DocumentMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    override val replyToMessage: Message? = null,
    override val viaBot: Bot? = null,
    override val lastEditDate: Long? = null,
    override val mediaGroupId: String? = null,
    override val authorSignature: String? = null,
    val document: Document,
    override val text: Text? = null,
    override val replyMarkup: InlineKeyboard? = null,
    override val forwardFrom: Sender? = null,
    override val forwardFromMessageId: Long? = null,
    override val forwardSignature: String? = null,
    override val forwardSenderName: String? = null,
    override val forwardDate: Long? = null
) : Message(), WithText {
    override fun toMessageContent(): DocumentContent {
        return DocumentContent(
            document = OutputFile.fromFileId(document.fileId),
            caption = text,
            thumbnail = document.thumbnail?.fileId?.let { OutputFile.fromFileId(it) }
        )
    }

    object Serializer : KSerializer<DocumentMessage> {
        override val descriptor = buildClassSerialDescriptor("DocumentMessage") {
            messageElements()
            element<Document>("document")
            element<String?>("caption")
            element<List<MessageEntity>>("caption_entities")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var replyToMessage: Message? = null
            var viaBot: Bot? = null
            var lastEditDate: Long? = null
            var mediaGroupId: String? = null
            var authorSignature: String? = null
            var forwardFrom: Sender? = null
            var forwardFromMessageId: Long? = null
            var forwardSignature: String? = null
            var forwardSenderName: String? = null
            var forwardDate: Long? = null
            var replyMarkup: InlineKeyboard? = null
            var document: Document? = null
            var caption: String? = null
            var captionEntities: List<MessageEntity>? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    5 -> replyToMessage = decodeSerializableElement(
                        descriptor,
                        5,
                        Message.serializer(),
                        replyToMessage
                    )
                    6 -> viaBot = decodeSerializableElement(descriptor, 6, Bot.serializer(), viaBot)
                    7 -> lastEditDate = decodeLongElement(descriptor, 7)
                    8 -> mediaGroupId = decodeStringElement(descriptor, 8)
                    9 -> authorSignature = decodeStringElement(descriptor, 9)
                    10 -> forwardFrom =
                        decodeSerializableElement(descriptor, 10, Sender.serializer(), forwardFrom)
                    11 -> forwardFrom =
                        decodeSerializableElement(descriptor, 11, Sender.serializer(), forwardFrom)
                    12 -> forwardFromMessageId = decodeLongElement(descriptor, 12)
                    13 -> forwardSignature = decodeStringElement(descriptor, 13)
                    14 -> forwardSenderName = decodeStringElement(descriptor, 14)
                    15 -> forwardDate = decodeLongElement(descriptor, 15)
                    16 -> replyMarkup = decodeSerializableElement(
                        descriptor,
                        16,
                        InlineKeyboard.serializer(),
                        replyMarkup
                    )
                    17 -> document =
                        decodeSerializableElement(descriptor, 17, Document.serializer(), document)
                    18 -> caption = decodeStringElement(descriptor, 18)
                    19 -> captionEntities = decodeSerializableElement(
                        descriptor,
                        19,
                        ListSerializer(MessageEntity.serializer()),
                        captionEntities
                    )
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            requireNotNull(document)
            val text = caption?.let { Text(it, captionEntities ?: emptyList()) }
            DocumentMessage(
                id,
                sender,
                date,
                chat,
                replyToMessage,
                viaBot,
                lastEditDate,
                mediaGroupId,
                authorSignature,
                document,
                text,
                replyMarkup,
                forwardFrom,
                forwardFromMessageId,
                forwardSignature,
                forwardSenderName,
                forwardDate
            )
        }

        override fun serialize(encoder: Encoder, value: DocumentMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat, replyToMessage, viaBot, lastEditDate, mediaGroupId, authorSignature, document, text, replyMarkup, forwardFrom, forwardFromMessageId, forwardSignature, forwardSenderName, forwardDate) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                if (replyToMessage != null) encodeSerializableElement(
                    descriptor,
                    5,
                    Message.serializer(),
                    replyToMessage
                )
                if (viaBot != null) encodeSerializableElement(
                    descriptor,
                    6,
                    Bot.serializer(),
                    viaBot
                )
                if (lastEditDate != null) encodeLongElement(descriptor, 7, lastEditDate)
                if (mediaGroupId != null) encodeStringElement(descriptor, 8, mediaGroupId)
                if (authorSignature != null) encodeStringElement(descriptor, 9, authorSignature)
                when (forwardFrom) {
                    is User, is Bot -> encodeSerializableElement(
                        descriptor,
                        10,
                        Sender.serializer(),
                        forwardFrom
                    )
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        11,
                        Sender.serializer(),
                        forwardFrom
                    )
                }
                if (forwardFromMessageId != null) encodeLongElement(
                    descriptor,
                    12,
                    forwardFromMessageId
                )
                if (forwardSignature != null) encodeStringElement(descriptor, 13, forwardSignature)
                if (forwardSenderName != null) encodeStringElement(
                    descriptor,
                    14,
                    forwardSenderName
                )
                if (forwardDate != null) encodeLongElement(descriptor, 15, forwardDate)
                if (replyMarkup != null) encodeSerializableElement(
                    descriptor,
                    16,
                    InlineKeyboard.serializer(),
                    replyMarkup
                )
                encodeSerializableElement(descriptor, 17, Document.serializer(), document)
                if (text != null) encodeStringElement(descriptor, 18, text.toString())
                if (text != null && text.entities.isNotEmpty()) encodeSerializableElement(
                    descriptor,
                    19,
                    ListSerializer(MessageEntity.serializer()),
                    text.entities
                )
            }
        }
    }
}

@Serializable(PhotoMessage.Serializer::class)
data class PhotoMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    override val replyToMessage: Message? = null,
    override val viaBot: Bot? = null,
    override val lastEditDate: Long? = null,
    override val mediaGroupId: String? = null,
    override val authorSignature: String? = null,
    val photo: List<PhotoSize>,
    override val text: Text? = null,
    override val replyMarkup: InlineKeyboard? = null,
    override val forwardFrom: Sender? = null,
    override val forwardFromMessageId: Long? = null,
    override val forwardSignature: String? = null,
    override val forwardSenderName: String? = null,
    override val forwardDate: Long? = null
) : Message(), WithText {
    override fun toMessageContent(): PhotoContent {
        return PhotoContent(OutputFile.fromFileId(photo.first().fileId), text)
    }

    object Serializer : KSerializer<PhotoMessage> {
        override val descriptor = buildClassSerialDescriptor("PhotoMessage") {
            messageElements()
            element<List<PhotoSize>>("photo")
            element<String?>("caption")
            element<List<MessageEntity>>("caption_entities")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var replyToMessage: Message? = null
            var viaBot: Bot? = null
            var lastEditDate: Long? = null
            var mediaGroupId: String? = null
            var authorSignature: String? = null
            var forwardFrom: Sender? = null
            var forwardFromMessageId: Long? = null
            var forwardSignature: String? = null
            var forwardSenderName: String? = null
            var forwardDate: Long? = null
            var replyMarkup: InlineKeyboard? = null
            var photo: List<PhotoSize>? = null
            var caption: String? = null
            var captionEntities: List<MessageEntity>? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    5 -> replyToMessage = decodeSerializableElement(
                        descriptor,
                        5,
                        Message.serializer(),
                        replyToMessage
                    )
                    6 -> viaBot = decodeSerializableElement(descriptor, 6, Bot.serializer(), viaBot)
                    7 -> lastEditDate = decodeLongElement(descriptor, 7)
                    8 -> mediaGroupId = decodeStringElement(descriptor, 8)
                    9 -> authorSignature = decodeStringElement(descriptor, 9)
                    10 -> forwardFrom =
                        decodeSerializableElement(descriptor, 10, Sender.serializer(), forwardFrom)
                    11 -> forwardFrom =
                        decodeSerializableElement(descriptor, 11, Sender.serializer(), forwardFrom)
                    12 -> forwardFromMessageId = decodeLongElement(descriptor, 12)
                    13 -> forwardSignature = decodeStringElement(descriptor, 13)
                    14 -> forwardSenderName = decodeStringElement(descriptor, 14)
                    15 -> forwardDate = decodeLongElement(descriptor, 15)
                    16 -> replyMarkup = decodeSerializableElement(
                        descriptor,
                        16,
                        InlineKeyboard.serializer(),
                        replyMarkup
                    )
                    17 -> photo = decodeSerializableElement(
                        descriptor,
                        17,
                        ListSerializer(PhotoSize.serializer()),
                        photo
                    )
                    18 -> caption = decodeStringElement(descriptor, 18)
                    19 -> captionEntities = decodeSerializableElement(
                        descriptor,
                        19,
                        ListSerializer(MessageEntity.serializer()),
                        captionEntities
                    )
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            requireNotNull(photo)
            val text = caption?.let { Text(it, captionEntities ?: emptyList()) }
            PhotoMessage(
                id,
                sender,
                date,
                chat,
                replyToMessage,
                viaBot,
                lastEditDate,
                mediaGroupId,
                authorSignature,
                photo,
                text,
                replyMarkup,
                forwardFrom,
                forwardFromMessageId,
                forwardSignature,
                forwardSenderName,
                forwardDate
            )
        }

        override fun serialize(encoder: Encoder, value: PhotoMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat, replyToMessage, viaBot, lastEditDate, mediaGroupId, authorSignature, photo, text, replyMarkup, forwardFrom, forwardFromMessageId, forwardSignature, forwardSenderName, forwardDate) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                if (replyToMessage != null) encodeSerializableElement(
                    descriptor,
                    5,
                    Message.serializer(),
                    replyToMessage
                )
                if (viaBot != null) encodeSerializableElement(
                    descriptor,
                    6,
                    Bot.serializer(),
                    viaBot
                )
                if (lastEditDate != null) encodeLongElement(descriptor, 7, lastEditDate)
                if (mediaGroupId != null) encodeStringElement(descriptor, 8, mediaGroupId)
                if (authorSignature != null) encodeStringElement(descriptor, 9, authorSignature)
                when (forwardFrom) {
                    is User, is Bot -> encodeSerializableElement(
                        descriptor,
                        10,
                        Sender.serializer(),
                        forwardFrom
                    )
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        11,
                        Sender.serializer(),
                        forwardFrom
                    )
                }
                if (forwardFromMessageId != null) encodeLongElement(
                    descriptor,
                    12,
                    forwardFromMessageId
                )
                if (forwardSignature != null) encodeStringElement(descriptor, 13, forwardSignature)
                if (forwardSenderName != null) encodeStringElement(
                    descriptor,
                    14,
                    forwardSenderName
                )
                if (forwardDate != null) encodeLongElement(descriptor, 15, forwardDate)
                if (replyMarkup != null) encodeSerializableElement(
                    descriptor,
                    16,
                    InlineKeyboard.serializer(),
                    replyMarkup
                )
                encodeSerializableElement(
                    descriptor,
                    17,
                    ListSerializer(PhotoSize.serializer()),
                    photo
                )
                if (text != null) encodeStringElement(descriptor, 18, text.toString())
                if (text != null && text.entities.isNotEmpty()) encodeSerializableElement(
                    descriptor,
                    19,
                    ListSerializer(MessageEntity.serializer()),
                    text.entities
                )
            }
        }
    }
}

@Serializable(StickerMessage.Serializer::class)
data class StickerMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    override val replyToMessage: Message? = null,
    override val viaBot: Bot? = null,
    override val lastEditDate: Long? = null,
    override val mediaGroupId: String? = null,
    override val authorSignature: String? = null,
    val sticker: Sticker,
    override val replyMarkup: InlineKeyboard? = null,
    override val forwardFrom: Sender? = null,
    override val forwardFromMessageId: Long? = null,
    override val forwardSignature: String? = null,
    override val forwardSenderName: String? = null,
    override val forwardDate: Long? = null
) : Message() {
    override fun toMessageContent(): StickerContent {
        return StickerContent(OutputFile.fromFileId(sticker.fileId))
    }

    object Serializer : KSerializer<StickerMessage> {
        override val descriptor = buildClassSerialDescriptor("StickerMessage") {
            messageElements()
            element<Sticker>("sticker")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var replyToMessage: Message? = null
            var viaBot: Bot? = null
            var lastEditDate: Long? = null
            var mediaGroupId: String? = null
            var authorSignature: String? = null
            var forwardFrom: Sender? = null
            var forwardFromMessageId: Long? = null
            var forwardSignature: String? = null
            var forwardSenderName: String? = null
            var forwardDate: Long? = null
            var replyMarkup: InlineKeyboard? = null
            var sticker: Sticker? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    5 -> replyToMessage = decodeSerializableElement(
                        descriptor,
                        5,
                        Message.serializer(),
                        replyToMessage
                    )
                    6 -> viaBot = decodeSerializableElement(descriptor, 6, Bot.serializer(), viaBot)
                    7 -> lastEditDate = decodeLongElement(descriptor, 7)
                    8 -> mediaGroupId = decodeStringElement(descriptor, 8)
                    9 -> authorSignature = decodeStringElement(descriptor, 9)
                    10 -> forwardFrom =
                        decodeSerializableElement(descriptor, 10, Sender.serializer(), forwardFrom)
                    11 -> forwardFrom =
                        decodeSerializableElement(descriptor, 11, Sender.serializer(), forwardFrom)
                    12 -> forwardFromMessageId = decodeLongElement(descriptor, 12)
                    13 -> forwardSignature = decodeStringElement(descriptor, 13)
                    14 -> forwardSenderName = decodeStringElement(descriptor, 14)
                    15 -> forwardDate = decodeLongElement(descriptor, 15)
                    16 -> replyMarkup = decodeSerializableElement(
                        descriptor,
                        16,
                        InlineKeyboard.serializer(),
                        replyMarkup
                    )
                    17 -> sticker =
                        decodeSerializableElement(descriptor, 17, Sticker.serializer(), sticker)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            requireNotNull(sticker)
            StickerMessage(
                id,
                sender,
                date,
                chat,
                replyToMessage,
                viaBot,
                lastEditDate,
                mediaGroupId,
                authorSignature,
                sticker,
                replyMarkup,
                forwardFrom,
                forwardFromMessageId,
                forwardSignature,
                forwardSenderName,
                forwardDate
            )
        }

        override fun serialize(encoder: Encoder, value: StickerMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat, replyToMessage, viaBot, lastEditDate, mediaGroupId, authorSignature, sticker, replyMarkup, forwardFrom, forwardFromMessageId, forwardSignature, forwardSenderName, forwardDate) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                if (replyToMessage != null) encodeSerializableElement(
                    descriptor,
                    5,
                    Message.serializer(),
                    replyToMessage
                )
                if (viaBot != null) encodeSerializableElement(
                    descriptor,
                    6,
                    Bot.serializer(),
                    viaBot
                )
                if (lastEditDate != null) encodeLongElement(descriptor, 7, lastEditDate)
                if (mediaGroupId != null) encodeStringElement(descriptor, 8, mediaGroupId)
                if (authorSignature != null) encodeStringElement(descriptor, 9, authorSignature)
                when (forwardFrom) {
                    is User, is Bot -> encodeSerializableElement(
                        descriptor,
                        10,
                        Sender.serializer(),
                        forwardFrom
                    )
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        11,
                        Sender.serializer(),
                        forwardFrom
                    )
                }
                if (forwardFromMessageId != null) encodeLongElement(
                    descriptor,
                    12,
                    forwardFromMessageId
                )
                if (forwardSignature != null) encodeStringElement(descriptor, 13, forwardSignature)
                if (forwardSenderName != null) encodeStringElement(
                    descriptor,
                    14,
                    forwardSenderName
                )
                if (forwardDate != null) encodeLongElement(descriptor, 15, forwardDate)
                if (replyMarkup != null) encodeSerializableElement(
                    descriptor,
                    16,
                    InlineKeyboard.serializer(),
                    replyMarkup
                )
                encodeSerializableElement(descriptor, 17, Sticker.serializer(), sticker)
            }
        }
    }
}

@Serializable(VideoMessage.Serializer::class)
data class VideoMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    override val replyToMessage: Message? = null,
    override val viaBot: Bot? = null,
    override val lastEditDate: Long? = null,
    override val mediaGroupId: String? = null,
    override val authorSignature: String? = null,
    val video: Video,
    override val text: Text? = null,
    override val replyMarkup: InlineKeyboard? = null,
    override val forwardFrom: Sender? = null,
    override val forwardFromMessageId: Long? = null,
    override val forwardSignature: String? = null,
    override val forwardSenderName: String? = null,
    override val forwardDate: Long? = null
) : Message(), WithText {
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

    object Serializer : KSerializer<VideoMessage> {
        override val descriptor = buildClassSerialDescriptor("VideoMessage") {
            messageElements()
            element<Video>("video")
            element<String?>("caption")
            element<List<MessageEntity>>("caption_entities")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var replyToMessage: Message? = null
            var viaBot: Bot? = null
            var lastEditDate: Long? = null
            var mediaGroupId: String? = null
            var authorSignature: String? = null
            var forwardFrom: Sender? = null
            var forwardFromMessageId: Long? = null
            var forwardSignature: String? = null
            var forwardSenderName: String? = null
            var forwardDate: Long? = null
            var replyMarkup: InlineKeyboard? = null
            var video: Video? = null
            var caption: String? = null
            var captionEntities: List<MessageEntity>? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    5 -> replyToMessage = decodeSerializableElement(
                        descriptor,
                        5,
                        Message.serializer(),
                        replyToMessage
                    )
                    6 -> viaBot = decodeSerializableElement(descriptor, 6, Bot.serializer(), viaBot)
                    7 -> lastEditDate = decodeLongElement(descriptor, 7)
                    8 -> mediaGroupId = decodeStringElement(descriptor, 8)
                    9 -> authorSignature = decodeStringElement(descriptor, 9)
                    10 -> forwardFrom =
                        decodeSerializableElement(descriptor, 10, Sender.serializer(), forwardFrom)
                    11 -> forwardFrom =
                        decodeSerializableElement(descriptor, 11, Sender.serializer(), forwardFrom)
                    12 -> forwardFromMessageId = decodeLongElement(descriptor, 12)
                    13 -> forwardSignature = decodeStringElement(descriptor, 13)
                    14 -> forwardSenderName = decodeStringElement(descriptor, 14)
                    15 -> forwardDate = decodeLongElement(descriptor, 15)
                    16 -> replyMarkup = decodeSerializableElement(
                        descriptor,
                        16,
                        InlineKeyboard.serializer(),
                        replyMarkup
                    )
                    17 -> video =
                        decodeSerializableElement(descriptor, 17, Video.serializer(), video)
                    18 -> caption = decodeStringElement(descriptor, 18)
                    19 -> captionEntities = decodeSerializableElement(
                        descriptor,
                        19,
                        ListSerializer(MessageEntity.serializer()),
                        captionEntities
                    )
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            requireNotNull(video)
            val text = caption?.let { Text(it, captionEntities ?: emptyList()) }
            VideoMessage(
                id,
                sender,
                date,
                chat,
                replyToMessage,
                viaBot,
                lastEditDate,
                mediaGroupId,
                authorSignature,
                video,
                text,
                replyMarkup,
                forwardFrom,
                forwardFromMessageId,
                forwardSignature,
                forwardSenderName,
                forwardDate
            )
        }

        override fun serialize(encoder: Encoder, value: VideoMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat, replyToMessage, viaBot, lastEditDate, mediaGroupId, authorSignature, video, text, replyMarkup, forwardFrom, forwardFromMessageId, forwardSignature, forwardSenderName, forwardDate) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                if (replyToMessage != null) encodeSerializableElement(
                    descriptor,
                    5,
                    Message.serializer(),
                    replyToMessage
                )
                if (viaBot != null) encodeSerializableElement(
                    descriptor,
                    6,
                    Bot.serializer(),
                    viaBot
                )
                if (lastEditDate != null) encodeLongElement(descriptor, 7, lastEditDate)
                if (mediaGroupId != null) encodeStringElement(descriptor, 8, mediaGroupId)
                if (authorSignature != null) encodeStringElement(descriptor, 9, authorSignature)
                when (forwardFrom) {
                    is User, is Bot -> encodeSerializableElement(
                        descriptor,
                        10,
                        Sender.serializer(),
                        forwardFrom
                    )
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        11,
                        Sender.serializer(),
                        forwardFrom
                    )
                }
                if (forwardFromMessageId != null) encodeLongElement(
                    descriptor,
                    12,
                    forwardFromMessageId
                )
                if (forwardSignature != null) encodeStringElement(descriptor, 13, forwardSignature)
                if (forwardSenderName != null) encodeStringElement(
                    descriptor,
                    14,
                    forwardSenderName
                )
                if (forwardDate != null) encodeLongElement(descriptor, 15, forwardDate)
                if (replyMarkup != null) encodeSerializableElement(
                    descriptor,
                    16,
                    InlineKeyboard.serializer(),
                    replyMarkup
                )
                encodeSerializableElement(descriptor, 17, Video.serializer(), video)
                if (text != null) encodeStringElement(descriptor, 18, text.toString())
                if (text != null && text.entities.isNotEmpty()) encodeSerializableElement(
                    descriptor,
                    19,
                    ListSerializer(MessageEntity.serializer()),
                    text.entities
                )
            }
        }
    }
}

@Serializable(VideoNoteMessage.Serializer::class)
data class VideoNoteMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    override val replyToMessage: Message? = null,
    override val viaBot: Bot? = null,
    override val lastEditDate: Long? = null,
    override val mediaGroupId: String? = null,
    override val authorSignature: String? = null,
    val videoNote: VideoNote,
    override val replyMarkup: InlineKeyboard? = null,
    override val forwardFrom: Sender? = null,
    override val forwardFromMessageId: Long? = null,
    override val forwardSignature: String? = null,
    override val forwardSenderName: String? = null,
    override val forwardDate: Long? = null
) : Message() {
    override fun toMessageContent(): VideoNoteContent {
        return VideoNoteContent(
            videoNote = OutputFile.fromFileId(videoNote.fileId),
            duration = videoNote.duration,
            length = videoNote.length,
            thumbnail = videoNote.thumbnail?.fileId?.let { OutputFile.fromFileId(it) }
        )
    }

    object Serializer : KSerializer<VideoNoteMessage> {
        override val descriptor = buildClassSerialDescriptor("VideoNoteMessage") {
            messageElements()
            element<VideoNote>("video_note")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var replyToMessage: Message? = null
            var viaBot: Bot? = null
            var lastEditDate: Long? = null
            var mediaGroupId: String? = null
            var authorSignature: String? = null
            var forwardFrom: Sender? = null
            var forwardFromMessageId: Long? = null
            var forwardSignature: String? = null
            var forwardSenderName: String? = null
            var forwardDate: Long? = null
            var replyMarkup: InlineKeyboard? = null
            var videoNote: VideoNote? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    5 -> replyToMessage = decodeSerializableElement(
                        descriptor,
                        5,
                        Message.serializer(),
                        replyToMessage
                    )
                    6 -> viaBot = decodeSerializableElement(descriptor, 6, Bot.serializer(), viaBot)
                    7 -> lastEditDate = decodeLongElement(descriptor, 7)
                    8 -> mediaGroupId = decodeStringElement(descriptor, 8)
                    9 -> authorSignature = decodeStringElement(descriptor, 9)
                    10 -> forwardFrom =
                        decodeSerializableElement(descriptor, 10, Sender.serializer(), forwardFrom)
                    11 -> forwardFrom =
                        decodeSerializableElement(descriptor, 11, Sender.serializer(), forwardFrom)
                    12 -> forwardFromMessageId = decodeLongElement(descriptor, 12)
                    13 -> forwardSignature = decodeStringElement(descriptor, 13)
                    14 -> forwardSenderName = decodeStringElement(descriptor, 14)
                    15 -> forwardDate = decodeLongElement(descriptor, 15)
                    16 -> replyMarkup = decodeSerializableElement(
                        descriptor,
                        16,
                        InlineKeyboard.serializer(),
                        replyMarkup
                    )
                    17 -> videoNote =
                        decodeSerializableElement(descriptor, 17, VideoNote.serializer(), videoNote)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            requireNotNull(videoNote)
            VideoNoteMessage(
                id,
                sender,
                date,
                chat,
                replyToMessage,
                viaBot,
                lastEditDate,
                mediaGroupId,
                authorSignature,
                videoNote,
                replyMarkup,
                forwardFrom,
                forwardFromMessageId,
                forwardSignature,
                forwardSenderName,
                forwardDate
            )
        }

        override fun serialize(encoder: Encoder, value: VideoNoteMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat, replyToMessage, viaBot, lastEditDate, mediaGroupId, authorSignature, videoNote, replyMarkup, forwardFrom, forwardFromMessageId, forwardSignature, forwardSenderName, forwardDate) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                if (replyToMessage != null) encodeSerializableElement(
                    descriptor,
                    5,
                    Message.serializer(),
                    replyToMessage
                )
                if (viaBot != null) encodeSerializableElement(
                    descriptor,
                    6,
                    Bot.serializer(),
                    viaBot
                )
                if (lastEditDate != null) encodeLongElement(descriptor, 7, lastEditDate)
                if (mediaGroupId != null) encodeStringElement(descriptor, 8, mediaGroupId)
                if (authorSignature != null) encodeStringElement(descriptor, 9, authorSignature)
                when (forwardFrom) {
                    is User, is Bot -> encodeSerializableElement(
                        descriptor,
                        10,
                        Sender.serializer(),
                        forwardFrom
                    )
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        11,
                        Sender.serializer(),
                        forwardFrom
                    )
                }
                if (forwardFromMessageId != null) encodeLongElement(
                    descriptor,
                    12,
                    forwardFromMessageId
                )
                if (forwardSignature != null) encodeStringElement(descriptor, 13, forwardSignature)
                if (forwardSenderName != null) encodeStringElement(
                    descriptor,
                    14,
                    forwardSenderName
                )
                if (forwardDate != null) encodeLongElement(descriptor, 15, forwardDate)
                if (replyMarkup != null) encodeSerializableElement(
                    descriptor,
                    16,
                    InlineKeyboard.serializer(),
                    replyMarkup
                )
                encodeSerializableElement(descriptor, 17, VideoNote.serializer(), videoNote)
            }
        }
    }
}

@Serializable(VoiceMessage.Serializer::class)
data class VoiceMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    override val replyToMessage: Message? = null,
    override val viaBot: Bot? = null,
    override val lastEditDate: Long? = null,
    override val mediaGroupId: String? = null,
    override val authorSignature: String? = null,
    val voice: Voice,
    override val text: Text? = null,
    override val replyMarkup: InlineKeyboard? = null,
    override val forwardFrom: Sender? = null,
    override val forwardFromMessageId: Long? = null,
    override val forwardSignature: String? = null,
    override val forwardSenderName: String? = null,
    override val forwardDate: Long? = null
) : Message(), WithText {
    override fun toMessageContent(): VoiceContent {
        return VoiceContent(OutputFile.fromFileId(voice.fileId), text, voice.duration)
    }

    object Serializer : KSerializer<VoiceMessage> {
        override val descriptor = buildClassSerialDescriptor("VoiceMessage") {
            messageElements()
            element<Voice>("voice")
            element<String?>("caption")
            element<List<MessageEntity>>("caption_entities")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var replyToMessage: Message? = null
            var viaBot: Bot? = null
            var lastEditDate: Long? = null
            var mediaGroupId: String? = null
            var authorSignature: String? = null
            var forwardFrom: Sender? = null
            var forwardFromMessageId: Long? = null
            var forwardSignature: String? = null
            var forwardSenderName: String? = null
            var forwardDate: Long? = null
            var replyMarkup: InlineKeyboard? = null
            var voice: Voice? = null
            var caption: String? = null
            var captionEntities: List<MessageEntity>? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    5 -> replyToMessage = decodeSerializableElement(
                        descriptor,
                        5,
                        Message.serializer(),
                        replyToMessage
                    )
                    6 -> viaBot = decodeSerializableElement(descriptor, 6, Bot.serializer(), viaBot)
                    7 -> lastEditDate = decodeLongElement(descriptor, 7)
                    8 -> mediaGroupId = decodeStringElement(descriptor, 8)
                    9 -> authorSignature = decodeStringElement(descriptor, 9)
                    10 -> forwardFrom =
                        decodeSerializableElement(descriptor, 10, Sender.serializer(), forwardFrom)
                    11 -> forwardFrom =
                        decodeSerializableElement(descriptor, 11, Sender.serializer(), forwardFrom)
                    12 -> forwardFromMessageId = decodeLongElement(descriptor, 12)
                    13 -> forwardSignature = decodeStringElement(descriptor, 13)
                    14 -> forwardSenderName = decodeStringElement(descriptor, 14)
                    15 -> forwardDate = decodeLongElement(descriptor, 15)
                    16 -> replyMarkup = decodeSerializableElement(
                        descriptor,
                        16,
                        InlineKeyboard.serializer(),
                        replyMarkup
                    )
                    17 -> voice =
                        decodeSerializableElement(descriptor, 17, Voice.serializer(), voice)
                    18 -> caption = decodeStringElement(descriptor, 18)
                    19 -> captionEntities = decodeSerializableElement(
                        descriptor,
                        19,
                        ListSerializer(MessageEntity.serializer()),
                        captionEntities
                    )
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            requireNotNull(voice)
            val text = caption?.let { Text(it, captionEntities ?: emptyList()) }
            VoiceMessage(
                id,
                sender,
                date,
                chat,
                replyToMessage,
                viaBot,
                lastEditDate,
                mediaGroupId,
                authorSignature,
                voice,
                text,
                replyMarkup,
                forwardFrom,
                forwardFromMessageId,
                forwardSignature,
                forwardSenderName,
                forwardDate
            )
        }

        override fun serialize(encoder: Encoder, value: VoiceMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat, replyToMessage, viaBot, lastEditDate, mediaGroupId, authorSignature, voice, text, replyMarkup, forwardFrom, forwardFromMessageId, forwardSignature, forwardSenderName, forwardDate) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                if (replyToMessage != null) encodeSerializableElement(
                    descriptor,
                    5,
                    Message.serializer(),
                    replyToMessage
                )
                if (viaBot != null) encodeSerializableElement(
                    descriptor,
                    6,
                    Bot.serializer(),
                    viaBot
                )
                if (lastEditDate != null) encodeLongElement(descriptor, 7, lastEditDate)
                if (mediaGroupId != null) encodeStringElement(descriptor, 8, mediaGroupId)
                if (authorSignature != null) encodeStringElement(descriptor, 9, authorSignature)
                when (forwardFrom) {
                    is User, is Bot -> encodeSerializableElement(
                        descriptor,
                        10,
                        Sender.serializer(),
                        forwardFrom
                    )
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        11,
                        Sender.serializer(),
                        forwardFrom
                    )
                }
                if (forwardFromMessageId != null) encodeLongElement(
                    descriptor,
                    12,
                    forwardFromMessageId
                )
                if (forwardSignature != null) encodeStringElement(descriptor, 13, forwardSignature)
                if (forwardSenderName != null) encodeStringElement(
                    descriptor,
                    14,
                    forwardSenderName
                )
                if (forwardDate != null) encodeLongElement(descriptor, 15, forwardDate)
                if (replyMarkup != null) encodeSerializableElement(
                    descriptor,
                    16,
                    InlineKeyboard.serializer(),
                    replyMarkup
                )
                encodeSerializableElement(descriptor, 17, Voice.serializer(), voice)
                if (text != null) encodeStringElement(descriptor, 18, text.toString())
                if (text != null && text.entities.isNotEmpty()) encodeSerializableElement(
                    descriptor,
                    19,
                    ListSerializer(MessageEntity.serializer()),
                    text.entities
                )
            }
        }
    }
}

@Serializable(ContactMessage.Serializer::class)
data class ContactMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    override val replyToMessage: Message? = null,
    override val viaBot: Bot? = null,
    override val lastEditDate: Long? = null,
    override val mediaGroupId: String? = null,
    override val authorSignature: String? = null,
    val contact: Contact,
    override val replyMarkup: InlineKeyboard? = null,
    override val forwardFrom: Sender? = null,
    override val forwardFromMessageId: Long? = null,
    override val forwardSignature: String? = null,
    override val forwardSenderName: String? = null,
    override val forwardDate: Long? = null
) : Message() {
    override fun toMessageContent(): ContactContent {
        return ContactContent(
            contact.phoneNumber,
            contact.firstName,
            contact.lastName,
            contact.vcard
        )
    }

    object Serializer : KSerializer<ContactMessage> {
        override val descriptor = buildClassSerialDescriptor("ContactMessage") {
            messageElements()
            element<Contact>("animation")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var replyToMessage: Message? = null
            var viaBot: Bot? = null
            var lastEditDate: Long? = null
            var mediaGroupId: String? = null
            var authorSignature: String? = null
            var forwardFrom: Sender? = null
            var forwardFromMessageId: Long? = null
            var forwardSignature: String? = null
            var forwardSenderName: String? = null
            var forwardDate: Long? = null
            var replyMarkup: InlineKeyboard? = null
            var contact: Contact? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    5 -> replyToMessage = decodeSerializableElement(
                        descriptor,
                        5,
                        Message.serializer(),
                        replyToMessage
                    )
                    6 -> viaBot = decodeSerializableElement(descriptor, 6, Bot.serializer(), viaBot)
                    7 -> lastEditDate = decodeLongElement(descriptor, 7)
                    8 -> mediaGroupId = decodeStringElement(descriptor, 8)
                    9 -> authorSignature = decodeStringElement(descriptor, 9)
                    10 -> forwardFrom =
                        decodeSerializableElement(descriptor, 10, Sender.serializer(), forwardFrom)
                    11 -> forwardFrom =
                        decodeSerializableElement(descriptor, 11, Sender.serializer(), forwardFrom)
                    12 -> forwardFromMessageId = decodeLongElement(descriptor, 12)
                    13 -> forwardSignature = decodeStringElement(descriptor, 13)
                    14 -> forwardSenderName = decodeStringElement(descriptor, 14)
                    15 -> forwardDate = decodeLongElement(descriptor, 15)
                    16 -> replyMarkup = decodeSerializableElement(
                        descriptor,
                        16,
                        InlineKeyboard.serializer(),
                        replyMarkup
                    )
                    17 -> contact =
                        decodeSerializableElement(descriptor, 17, Contact.serializer(), contact)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            requireNotNull(contact)
            ContactMessage(
                id,
                sender,
                date,
                chat,
                replyToMessage,
                viaBot,
                lastEditDate,
                mediaGroupId,
                authorSignature,
                contact,
                replyMarkup,
                forwardFrom,
                forwardFromMessageId,
                forwardSignature,
                forwardSenderName,
                forwardDate
            )
        }

        override fun serialize(encoder: Encoder, value: ContactMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat, replyToMessage, viaBot, lastEditDate, mediaGroupId, authorSignature, contact, replyMarkup, forwardFrom, forwardFromMessageId, forwardSignature, forwardSenderName, forwardDate) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                if (replyToMessage != null) encodeSerializableElement(
                    descriptor,
                    5,
                    Message.serializer(),
                    replyToMessage
                )
                if (viaBot != null) encodeSerializableElement(
                    descriptor,
                    6,
                    Bot.serializer(),
                    viaBot
                )
                if (lastEditDate != null) encodeLongElement(descriptor, 7, lastEditDate)
                if (mediaGroupId != null) encodeStringElement(descriptor, 8, mediaGroupId)
                if (authorSignature != null) encodeStringElement(descriptor, 9, authorSignature)
                when (forwardFrom) {
                    is User, is Bot -> encodeSerializableElement(
                        descriptor,
                        10,
                        Sender.serializer(),
                        forwardFrom
                    )
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        11,
                        Sender.serializer(),
                        forwardFrom
                    )
                }
                if (forwardFromMessageId != null) encodeLongElement(
                    descriptor,
                    12,
                    forwardFromMessageId
                )
                if (forwardSignature != null) encodeStringElement(descriptor, 13, forwardSignature)
                if (forwardSenderName != null) encodeStringElement(
                    descriptor,
                    14,
                    forwardSenderName
                )
                if (forwardDate != null) encodeLongElement(descriptor, 15, forwardDate)
                if (replyMarkup != null) encodeSerializableElement(
                    descriptor,
                    16,
                    InlineKeyboard.serializer(),
                    replyMarkup
                )
                encodeSerializableElement(descriptor, 17, Contact.serializer(), contact)
            }
        }
    }
}

@Serializable(DiceMessage.Serializer::class)
data class DiceMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    override val replyToMessage: Message? = null,
    override val viaBot: Bot? = null,
    override val lastEditDate: Long? = null,
    override val mediaGroupId: String? = null,
    override val authorSignature: String? = null,
    val dice: Dice,
    override val replyMarkup: InlineKeyboard? = null,
    override val forwardFrom: Sender? = null,
    override val forwardFromMessageId: Long? = null,
    override val forwardSignature: String? = null,
    override val forwardSenderName: String? = null,
    override val forwardDate: Long? = null
) : Message() {
    override fun toMessageContent(): DiceContent {
        return DiceContent(dice.emoji)
    }

    object Serializer : KSerializer<DiceMessage> {
        override val descriptor = buildClassSerialDescriptor("DiceMessage") {
            messageElements()
            element<Animation>("dice")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var replyToMessage: Message? = null
            var viaBot: Bot? = null
            var lastEditDate: Long? = null
            var mediaGroupId: String? = null
            var authorSignature: String? = null
            var forwardFrom: Sender? = null
            var forwardFromMessageId: Long? = null
            var forwardSignature: String? = null
            var forwardSenderName: String? = null
            var forwardDate: Long? = null
            var replyMarkup: InlineKeyboard? = null
            var dice: Dice? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    5 -> replyToMessage = decodeSerializableElement(
                        descriptor,
                        5,
                        Message.serializer(),
                        replyToMessage
                    )
                    6 -> viaBot = decodeSerializableElement(descriptor, 6, Bot.serializer(), viaBot)
                    7 -> lastEditDate = decodeLongElement(descriptor, 7)
                    8 -> mediaGroupId = decodeStringElement(descriptor, 8)
                    9 -> authorSignature = decodeStringElement(descriptor, 9)
                    10 -> forwardFrom =
                        decodeSerializableElement(descriptor, 10, Sender.serializer(), forwardFrom)
                    11 -> forwardFrom =
                        decodeSerializableElement(descriptor, 11, Sender.serializer(), forwardFrom)
                    12 -> forwardFromMessageId = decodeLongElement(descriptor, 12)
                    13 -> forwardSignature = decodeStringElement(descriptor, 13)
                    14 -> forwardSenderName = decodeStringElement(descriptor, 14)
                    15 -> forwardDate = decodeLongElement(descriptor, 15)
                    16 -> replyMarkup = decodeSerializableElement(
                        descriptor,
                        16,
                        InlineKeyboard.serializer(),
                        replyMarkup
                    )
                    17 -> dice = decodeSerializableElement(descriptor, 17, Dice.serializer(), dice)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            requireNotNull(dice)
            DiceMessage(
                id,
                sender,
                date,
                chat,
                replyToMessage,
                viaBot,
                lastEditDate,
                mediaGroupId,
                authorSignature,
                dice,
                replyMarkup,
                forwardFrom,
                forwardFromMessageId,
                forwardSignature,
                forwardSenderName,
                forwardDate
            )
        }

        override fun serialize(encoder: Encoder, value: DiceMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat, replyToMessage, viaBot, lastEditDate, mediaGroupId, authorSignature, dice, replyMarkup, forwardFrom, forwardFromMessageId, forwardSignature, forwardSenderName, forwardDate) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                if (replyToMessage != null) encodeSerializableElement(
                    descriptor,
                    5,
                    Message.serializer(),
                    replyToMessage
                )
                if (viaBot != null) encodeSerializableElement(
                    descriptor,
                    6,
                    Bot.serializer(),
                    viaBot
                )
                if (lastEditDate != null) encodeLongElement(descriptor, 7, lastEditDate)
                if (mediaGroupId != null) encodeStringElement(descriptor, 8, mediaGroupId)
                if (authorSignature != null) encodeStringElement(descriptor, 9, authorSignature)
                when (forwardFrom) {
                    is User, is Bot -> encodeSerializableElement(
                        descriptor,
                        10,
                        Sender.serializer(),
                        forwardFrom
                    )
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        11,
                        Sender.serializer(),
                        forwardFrom
                    )
                }
                if (forwardFromMessageId != null) encodeLongElement(
                    descriptor,
                    12,
                    forwardFromMessageId
                )
                if (forwardSignature != null) encodeStringElement(descriptor, 13, forwardSignature)
                if (forwardSenderName != null) encodeStringElement(
                    descriptor,
                    14,
                    forwardSenderName
                )
                if (forwardDate != null) encodeLongElement(descriptor, 15, forwardDate)
                if (replyMarkup != null) encodeSerializableElement(
                    descriptor,
                    16,
                    InlineKeyboard.serializer(),
                    replyMarkup
                )
                encodeSerializableElement(descriptor, 17, Dice.serializer(), dice)
            }
        }
    }
}

@Serializable(GameMessage.Serializer::class)
data class GameMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    override val replyToMessage: Message? = null,
    override val viaBot: Bot? = null,
    override val lastEditDate: Long? = null,
    override val mediaGroupId: String? = null,
    override val authorSignature: String? = null,
    val game: Game,
    override val replyMarkup: InlineKeyboard? = null,
    override val forwardFrom: Sender? = null,
    override val forwardFromMessageId: Long? = null,
    override val forwardSignature: String? = null,
    override val forwardSenderName: String? = null,
    override val forwardDate: Long? = null
) : Message() {
    override fun toMessageContent(): Nothing? = null

    object Serializer : KSerializer<GameMessage> {
        override val descriptor = buildClassSerialDescriptor("GameMessage") {
            messageElements()
            element<Game>("game")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var replyToMessage: Message? = null
            var viaBot: Bot? = null
            var lastEditDate: Long? = null
            var mediaGroupId: String? = null
            var authorSignature: String? = null
            var forwardFrom: Sender? = null
            var forwardFromMessageId: Long? = null
            var forwardSignature: String? = null
            var forwardSenderName: String? = null
            var forwardDate: Long? = null
            var replyMarkup: InlineKeyboard? = null
            var game: Game? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    5 -> replyToMessage = decodeSerializableElement(
                        descriptor,
                        5,
                        Message.serializer(),
                        replyToMessage
                    )
                    6 -> viaBot = decodeSerializableElement(descriptor, 6, Bot.serializer(), viaBot)
                    7 -> lastEditDate = decodeLongElement(descriptor, 7)
                    8 -> mediaGroupId = decodeStringElement(descriptor, 8)
                    9 -> authorSignature = decodeStringElement(descriptor, 9)
                    10 -> forwardFrom =
                        decodeSerializableElement(descriptor, 10, Sender.serializer(), forwardFrom)
                    11 -> forwardFrom =
                        decodeSerializableElement(descriptor, 11, Sender.serializer(), forwardFrom)
                    12 -> forwardFromMessageId = decodeLongElement(descriptor, 12)
                    13 -> forwardSignature = decodeStringElement(descriptor, 13)
                    14 -> forwardSenderName = decodeStringElement(descriptor, 14)
                    15 -> forwardDate = decodeLongElement(descriptor, 15)
                    16 -> replyMarkup = decodeSerializableElement(
                        descriptor,
                        16,
                        InlineKeyboard.serializer(),
                        replyMarkup
                    )
                    17 -> game = decodeSerializableElement(descriptor, 17, Game.serializer(), game)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            requireNotNull(game)
            GameMessage(
                id,
                sender,
                date,
                chat,
                replyToMessage,
                viaBot,
                lastEditDate,
                mediaGroupId,
                authorSignature,
                game,
                replyMarkup,
                forwardFrom,
                forwardFromMessageId,
                forwardSignature,
                forwardSenderName,
                forwardDate
            )
        }

        override fun serialize(encoder: Encoder, value: GameMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat, replyToMessage, viaBot, lastEditDate, mediaGroupId, authorSignature, game, replyMarkup, forwardFrom, forwardFromMessageId, forwardSignature, forwardSenderName, forwardDate) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                if (replyToMessage != null) encodeSerializableElement(
                    descriptor,
                    5,
                    Message.serializer(),
                    replyToMessage
                )
                if (viaBot != null) encodeSerializableElement(
                    descriptor,
                    6,
                    Bot.serializer(),
                    viaBot
                )
                if (lastEditDate != null) encodeLongElement(descriptor, 7, lastEditDate)
                if (mediaGroupId != null) encodeStringElement(descriptor, 8, mediaGroupId)
                if (authorSignature != null) encodeStringElement(descriptor, 9, authorSignature)
                when (forwardFrom) {
                    is User, is Bot -> encodeSerializableElement(
                        descriptor,
                        10,
                        Sender.serializer(),
                        forwardFrom
                    )
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        11,
                        Sender.serializer(),
                        forwardFrom
                    )
                }
                if (forwardFromMessageId != null) encodeLongElement(
                    descriptor,
                    12,
                    forwardFromMessageId
                )
                if (forwardSignature != null) encodeStringElement(descriptor, 13, forwardSignature)
                if (forwardSenderName != null) encodeStringElement(
                    descriptor,
                    14,
                    forwardSenderName
                )
                if (forwardDate != null) encodeLongElement(descriptor, 15, forwardDate)
                if (replyMarkup != null) encodeSerializableElement(
                    descriptor,
                    16,
                    InlineKeyboard.serializer(),
                    replyMarkup
                )
                encodeSerializableElement(descriptor, 17, Game.serializer(), game)
            }
        }
    }
}

@Serializable(PollMessage.Serializer::class)
data class PollMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    override val replyToMessage: Message? = null,
    override val viaBot: Bot? = null,
    override val lastEditDate: Long? = null,
    override val mediaGroupId: String? = null,
    override val authorSignature: String? = null,
    val poll: Poll,
    override val replyMarkup: InlineKeyboard? = null,
    override val forwardFrom: Sender? = null,
    override val forwardFromMessageId: Long? = null,
    override val forwardSignature: String? = null,
    override val forwardSenderName: String? = null,
    override val forwardDate: Long? = null
) : Message() {
    override fun toMessageContent(): PollContent {
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
            is Poll.Quiz -> PollContent.Quiz(
                question = poll.question,
                options = poll.options.map { it.text },
                isAnonymous = poll.isAnonymous,
                openPeriod = poll.openPeriod,
                closeDate = poll.closeDate,
                isClosed = poll.isClosed,
                correctOption = poll.correctOptionId,
                explanation = poll.explanation
            )
        }
    }

    object Serializer : KSerializer<PollMessage> {
        override val descriptor = buildClassSerialDescriptor("PollMessage") {
            messageElements()
            element<Poll>("poll")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var replyToMessage: Message? = null
            var viaBot: Bot? = null
            var lastEditDate: Long? = null
            var mediaGroupId: String? = null
            var authorSignature: String? = null
            var forwardFrom: Sender? = null
            var forwardFromMessageId: Long? = null
            var forwardSignature: String? = null
            var forwardSenderName: String? = null
            var forwardDate: Long? = null
            var replyMarkup: InlineKeyboard? = null
            var poll: Poll? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    5 -> replyToMessage = decodeSerializableElement(
                        descriptor,
                        5,
                        Message.serializer(),
                        replyToMessage
                    )
                    6 -> viaBot = decodeSerializableElement(descriptor, 6, Bot.serializer(), viaBot)
                    7 -> lastEditDate = decodeLongElement(descriptor, 7)
                    8 -> mediaGroupId = decodeStringElement(descriptor, 8)
                    9 -> authorSignature = decodeStringElement(descriptor, 9)
                    10 -> forwardFrom =
                        decodeSerializableElement(descriptor, 10, Sender.serializer(), forwardFrom)
                    11 -> forwardFrom =
                        decodeSerializableElement(descriptor, 11, Sender.serializer(), forwardFrom)
                    12 -> forwardFromMessageId = decodeLongElement(descriptor, 12)
                    13 -> forwardSignature = decodeStringElement(descriptor, 13)
                    14 -> forwardSenderName = decodeStringElement(descriptor, 14)
                    15 -> forwardDate = decodeLongElement(descriptor, 15)
                    16 -> replyMarkup = decodeSerializableElement(
                        descriptor,
                        16,
                        InlineKeyboard.serializer(),
                        replyMarkup
                    )
                    17 -> poll = decodeSerializableElement(descriptor, 17, Poll.serializer(), poll)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            requireNotNull(poll)
            PollMessage(
                id,
                sender,
                date,
                chat,
                replyToMessage,
                viaBot,
                lastEditDate,
                mediaGroupId,
                authorSignature,
                poll,
                replyMarkup,
                forwardFrom,
                forwardFromMessageId,
                forwardSignature,
                forwardSenderName,
                forwardDate
            )
        }

        override fun serialize(encoder: Encoder, value: PollMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat, replyToMessage, viaBot, lastEditDate, mediaGroupId, authorSignature, poll, replyMarkup, forwardFrom, forwardFromMessageId, forwardSignature, forwardSenderName, forwardDate) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                if (replyToMessage != null) encodeSerializableElement(
                    descriptor,
                    5,
                    Message.serializer(),
                    replyToMessage
                )
                if (viaBot != null) encodeSerializableElement(
                    descriptor,
                    6,
                    Bot.serializer(),
                    viaBot
                )
                if (lastEditDate != null) encodeLongElement(descriptor, 7, lastEditDate)
                if (mediaGroupId != null) encodeStringElement(descriptor, 8, mediaGroupId)
                if (authorSignature != null) encodeStringElement(descriptor, 9, authorSignature)
                when (forwardFrom) {
                    is User, is Bot -> encodeSerializableElement(
                        descriptor,
                        10,
                        Sender.serializer(),
                        forwardFrom
                    )
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        11,
                        Sender.serializer(),
                        forwardFrom
                    )
                }
                if (forwardFromMessageId != null) encodeLongElement(
                    descriptor,
                    12,
                    forwardFromMessageId
                )
                if (forwardSignature != null) encodeStringElement(descriptor, 13, forwardSignature)
                if (forwardSenderName != null) encodeStringElement(
                    descriptor,
                    14,
                    forwardSenderName
                )
                if (forwardDate != null) encodeLongElement(descriptor, 15, forwardDate)
                if (replyMarkup != null) encodeSerializableElement(
                    descriptor,
                    16,
                    InlineKeyboard.serializer(),
                    replyMarkup
                )
                encodeSerializableElement(descriptor, 17, Poll.serializer(), poll)
            }
        }
    }
}

@Serializable(VenueMessage.Serializer::class)
data class VenueMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    override val replyToMessage: Message? = null,
    override val viaBot: Bot? = null,
    override val lastEditDate: Long? = null,
    override val mediaGroupId: String? = null,
    override val authorSignature: String? = null,
    val venue: Venue,
    override val replyMarkup: InlineKeyboard? = null,
    override val forwardFrom: Sender? = null,
    override val forwardFromMessageId: Long? = null,
    override val forwardSignature: String? = null,
    override val forwardSenderName: String? = null,
    override val forwardDate: Long? = null
) : Message() {
    override fun toMessageContent(): VenueContent {
        return VenueContent(venue)
    }

    object Serializer : KSerializer<VenueMessage> {
        override val descriptor = buildClassSerialDescriptor("VenueMessage") {
            messageElements()
            element<Venue>("venue")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var replyToMessage: Message? = null
            var viaBot: Bot? = null
            var lastEditDate: Long? = null
            var mediaGroupId: String? = null
            var authorSignature: String? = null
            var forwardFrom: Sender? = null
            var forwardFromMessageId: Long? = null
            var forwardSignature: String? = null
            var forwardSenderName: String? = null
            var forwardDate: Long? = null
            var replyMarkup: InlineKeyboard? = null
            var venue: Venue? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    5 -> replyToMessage = decodeSerializableElement(
                        descriptor,
                        5,
                        Message.serializer(),
                        replyToMessage
                    )
                    6 -> viaBot = decodeSerializableElement(descriptor, 6, Bot.serializer(), viaBot)
                    7 -> lastEditDate = decodeLongElement(descriptor, 7)
                    8 -> mediaGroupId = decodeStringElement(descriptor, 8)
                    9 -> authorSignature = decodeStringElement(descriptor, 9)
                    10 -> forwardFrom =
                        decodeSerializableElement(descriptor, 10, Sender.serializer(), forwardFrom)
                    11 -> forwardFrom =
                        decodeSerializableElement(descriptor, 11, Sender.serializer(), forwardFrom)
                    12 -> forwardFromMessageId = decodeLongElement(descriptor, 12)
                    13 -> forwardSignature = decodeStringElement(descriptor, 13)
                    14 -> forwardSenderName = decodeStringElement(descriptor, 14)
                    15 -> forwardDate = decodeLongElement(descriptor, 15)
                    16 -> replyMarkup = decodeSerializableElement(
                        descriptor,
                        16,
                        InlineKeyboard.serializer(),
                        replyMarkup
                    )
                    17 -> venue =
                        decodeSerializableElement(descriptor, 17, Venue.serializer(), venue)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            requireNotNull(venue)
            VenueMessage(
                id,
                sender,
                date,
                chat,
                replyToMessage,
                viaBot,
                lastEditDate,
                mediaGroupId,
                authorSignature,
                venue,
                replyMarkup,
                forwardFrom,
                forwardFromMessageId,
                forwardSignature,
                forwardSenderName,
                forwardDate
            )
        }

        override fun serialize(encoder: Encoder, value: VenueMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat, replyToMessage, viaBot, lastEditDate, mediaGroupId, authorSignature, venue, replyMarkup, forwardFrom, forwardFromMessageId, forwardSignature, forwardSenderName, forwardDate) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                if (replyToMessage != null) encodeSerializableElement(
                    descriptor,
                    5,
                    Message.serializer(),
                    replyToMessage
                )
                if (viaBot != null) encodeSerializableElement(
                    descriptor,
                    6,
                    Bot.serializer(),
                    viaBot
                )
                if (lastEditDate != null) encodeLongElement(descriptor, 7, lastEditDate)
                if (mediaGroupId != null) encodeStringElement(descriptor, 8, mediaGroupId)
                if (authorSignature != null) encodeStringElement(descriptor, 9, authorSignature)
                when (forwardFrom) {
                    is User, is Bot -> encodeSerializableElement(
                        descriptor,
                        10,
                        Sender.serializer(),
                        forwardFrom
                    )
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        11,
                        Sender.serializer(),
                        forwardFrom
                    )
                }
                if (forwardFromMessageId != null) encodeLongElement(
                    descriptor,
                    12,
                    forwardFromMessageId
                )
                if (forwardSignature != null) encodeStringElement(descriptor, 13, forwardSignature)
                if (forwardSenderName != null) encodeStringElement(
                    descriptor,
                    14,
                    forwardSenderName
                )
                if (forwardDate != null) encodeLongElement(descriptor, 15, forwardDate)
                if (replyMarkup != null) encodeSerializableElement(
                    descriptor,
                    16,
                    InlineKeyboard.serializer(),
                    replyMarkup
                )
                encodeSerializableElement(descriptor, 17, Venue.serializer(), venue)
            }
        }
    }
}

@Serializable(LocationMessage.Serializer::class)
data class LocationMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    override val replyToMessage: Message? = null,
    override val viaBot: Bot? = null,
    override val lastEditDate: Long? = null,
    override val mediaGroupId: String? = null,
    override val authorSignature: String? = null,
    val location: Location,
    override val replyMarkup: InlineKeyboard? = null,
    override val forwardFrom: Sender? = null,
    override val forwardFromMessageId: Long? = null,
    override val forwardSignature: String? = null,
    override val forwardSenderName: String? = null,
    override val forwardDate: Long? = null
) : Message() {
    override fun toMessageContent(): LocationContent {
        return LocationContent(location)
    }

    object Serializer : KSerializer<LocationMessage> {
        override val descriptor = buildClassSerialDescriptor("LocationMessage") {
            messageElements()
            element<Location>("location")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var replyToMessage: Message? = null
            var viaBot: Bot? = null
            var lastEditDate: Long? = null
            var mediaGroupId: String? = null
            var authorSignature: String? = null
            var forwardFrom: Sender? = null
            var forwardFromMessageId: Long? = null
            var forwardSignature: String? = null
            var forwardSenderName: String? = null
            var forwardDate: Long? = null
            var replyMarkup: InlineKeyboard? = null
            var location: Location? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    5 -> replyToMessage = decodeSerializableElement(
                        descriptor,
                        5,
                        Message.serializer(),
                        replyToMessage
                    )
                    6 -> viaBot = decodeSerializableElement(descriptor, 6, Bot.serializer(), viaBot)
                    7 -> lastEditDate = decodeLongElement(descriptor, 7)
                    8 -> mediaGroupId = decodeStringElement(descriptor, 8)
                    9 -> authorSignature = decodeStringElement(descriptor, 9)
                    10 -> forwardFrom =
                        decodeSerializableElement(descriptor, 10, Sender.serializer(), forwardFrom)
                    11 -> forwardFrom =
                        decodeSerializableElement(descriptor, 11, Sender.serializer(), forwardFrom)
                    12 -> forwardFromMessageId = decodeLongElement(descriptor, 12)
                    13 -> forwardSignature = decodeStringElement(descriptor, 13)
                    14 -> forwardSenderName = decodeStringElement(descriptor, 14)
                    15 -> forwardDate = decodeLongElement(descriptor, 15)
                    16 -> replyMarkup = decodeSerializableElement(
                        descriptor,
                        16,
                        InlineKeyboard.serializer(),
                        replyMarkup
                    )
                    17 -> location =
                        decodeSerializableElement(descriptor, 17, Location.serializer(), location)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            requireNotNull(location)
            LocationMessage(
                id,
                sender,
                date,
                chat,
                replyToMessage,
                viaBot,
                lastEditDate,
                mediaGroupId,
                authorSignature,
                location,
                replyMarkup,
                forwardFrom,
                forwardFromMessageId,
                forwardSignature,
                forwardSenderName,
                forwardDate
            )
        }

        override fun serialize(encoder: Encoder, value: LocationMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat, replyToMessage, viaBot, lastEditDate, mediaGroupId, authorSignature, location, replyMarkup, forwardFrom, forwardFromMessageId, forwardSignature, forwardSenderName, forwardDate) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                if (replyToMessage != null) encodeSerializableElement(
                    descriptor,
                    5,
                    Message.serializer(),
                    replyToMessage
                )
                if (viaBot != null) encodeSerializableElement(
                    descriptor,
                    6,
                    Bot.serializer(),
                    viaBot
                )
                if (lastEditDate != null) encodeLongElement(descriptor, 7, lastEditDate)
                if (mediaGroupId != null) encodeStringElement(descriptor, 8, mediaGroupId)
                if (authorSignature != null) encodeStringElement(descriptor, 9, authorSignature)
                when (forwardFrom) {
                    is User, is Bot -> encodeSerializableElement(
                        descriptor,
                        10,
                        Sender.serializer(),
                        forwardFrom
                    )
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        11,
                        Sender.serializer(),
                        forwardFrom
                    )
                }
                if (forwardFromMessageId != null) encodeLongElement(
                    descriptor,
                    12,
                    forwardFromMessageId
                )
                if (forwardSignature != null) encodeStringElement(descriptor, 13, forwardSignature)
                if (forwardSenderName != null) encodeStringElement(
                    descriptor,
                    14,
                    forwardSenderName
                )
                if (forwardDate != null) encodeLongElement(descriptor, 15, forwardDate)
                if (replyMarkup != null) encodeSerializableElement(
                    descriptor,
                    16,
                    InlineKeyboard.serializer(),
                    replyMarkup
                )
                encodeSerializableElement(descriptor, 17, Location.serializer(), location)
            }
        }
    }
}

@Serializable(NewChatMembersMessage.Serializer::class)
data class NewChatMembersMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    val newChatMembers: List<Sender>
) : Message() {
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

    object Serializer : KSerializer<NewChatMembersMessage> {
        override val descriptor = buildClassSerialDescriptor("NewChatMembersMessage") {
            messageElements()
            element<Animation>("new_chat_members")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var newChatMembers: List<Sender>? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    17 -> newChatMembers = decodeSerializableElement(
                        descriptor,
                        17,
                        ListSerializer(Sender.serializer()),
                        newChatMembers
                    )
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            requireNotNull(newChatMembers)
            NewChatMembersMessage(id, sender, date, chat, newChatMembers)
        }

        override fun serialize(encoder: Encoder, value: NewChatMembersMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat, newChatMembers) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                encodeSerializableElement(
                    descriptor,
                    17,
                    ListSerializer(Sender.serializer()),
                    newChatMembers
                )
            }
        }
    }
}

@Serializable(LeftChatMemberMessage.Serializer::class)
data class LeftChatMemberMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    val leftChatMember: Sender
) : Message() {
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

    object Serializer : KSerializer<LeftChatMemberMessage> {
        override val descriptor = buildClassSerialDescriptor("LeftChatMemberMessage") {
            messageElements()
            element<Animation>("left_chat_member")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var leftChatMember: Sender? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    17 -> leftChatMember = decodeSerializableElement(
                        descriptor,
                        17,
                        Sender.serializer(),
                        leftChatMember
                    )
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            requireNotNull(leftChatMember)
            LeftChatMemberMessage(id, sender, date, chat, leftChatMember)
        }

        override fun serialize(encoder: Encoder, value: LeftChatMemberMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat, leftChatMember) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                encodeSerializableElement(descriptor, 17, Sender.serializer(), leftChatMember)
            }
        }
    }
}

@Serializable(NewChatTitleMessage.Serializer::class)
data class NewChatTitleMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    val newChatTitle: String
) : Message() {
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

    object Serializer : KSerializer<NewChatTitleMessage> {
        override val descriptor = buildClassSerialDescriptor("NewChatTitleMessage") {
            messageElements()
            element<String>("new_chat_title")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var newChatTitle: String? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    17 -> newChatTitle = decodeStringElement(descriptor, 17)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            requireNotNull(newChatTitle)
            NewChatTitleMessage(id, sender, date, chat, newChatTitle)
        }

        override fun serialize(encoder: Encoder, value: NewChatTitleMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat, newChatTitle) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                encodeStringElement(descriptor, 17, newChatTitle)
            }
        }
    }
}

@Serializable(NewChatPhotoMessage.Serializer::class)
data class NewChatPhotoMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    val newChatPhoto: List<PhotoSize>
) : Message() {
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

    object Serializer : KSerializer<NewChatPhotoMessage> {
        override val descriptor = buildClassSerialDescriptor("NewChatPhotoMessage") {
            messageElements()
            element<List<PhotoSize>>("new_chat_photo")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var newChatPhoto: List<PhotoSize>? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    17 -> newChatPhoto = decodeSerializableElement(
                        descriptor,
                        17,
                        ListSerializer(PhotoSize.serializer()),
                        newChatPhoto
                    )
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            requireNotNull(newChatPhoto)
            NewChatPhotoMessage(id, sender, date, chat, newChatPhoto)
        }

        override fun serialize(encoder: Encoder, value: NewChatPhotoMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat, newChatPhoto) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                encodeSerializableElement(
                    descriptor,
                    17,
                    ListSerializer(PhotoSize.serializer()),
                    newChatPhoto
                )
            }
        }
    }
}

@Serializable(DeleteChatPhotoMessage.Serializer::class)
data class DeleteChatPhotoMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat
) : Message() {
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

    object Serializer : KSerializer<DeleteChatPhotoMessage> {
        override val descriptor = buildClassSerialDescriptor("DeleteChatPhotoMessage") {
            messageElements()
            element<Boolean>("delete_chat_photo")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var deleteChatPhotoMessage = false
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    17 -> deleteChatPhotoMessage = decodeBooleanElement(descriptor, 17)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            check(deleteChatPhotoMessage) { "Not a DeleteChatPhotoMessage!" }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            DeleteChatPhotoMessage(id, sender, date, chat)
        }

        override fun serialize(encoder: Encoder, value: DeleteChatPhotoMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                encodeBooleanElement(descriptor, 17, true)
            }
        }
    }
}

@Serializable(GroupChatCreatedMessage.Serializer::class)
data class GroupChatCreatedMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Group
) : Message() {
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

    object Serializer : KSerializer<GroupChatCreatedMessage> {
        override val descriptor = buildClassSerialDescriptor("GroupChatCreatedMessage") {
            messageElements()
            element<Boolean>("group_chat_created")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Group? = null
            var groupChatCreated = false
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Group.serializer(), chat)
                    17 -> groupChatCreated = decodeBooleanElement(descriptor, 17)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            check(groupChatCreated) { "Not a GroupChatCreatedMessage!" }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            GroupChatCreatedMessage(id, sender, date, chat)
        }

        override fun serialize(encoder: Encoder, value: GroupChatCreatedMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Group.serializer(), chat)
                encodeBooleanElement(descriptor, 17, true)
            }
        }
    }
}

@Serializable(SupergroupChatCreatedMessage.Serializer::class)
data class SupergroupChatCreatedMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Supergroup
) : Message() {
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

    object Serializer : KSerializer<SupergroupChatCreatedMessage> {
        override val descriptor = buildClassSerialDescriptor("SupergroupChatCreatedMessage") {
            messageElements()
            element<Boolean>("supergroup_chat_created")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Supergroup? = null
            var supergroupChatCreated = false
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat =
                        decodeSerializableElement(descriptor, 4, Supergroup.serializer(), chat)
                    17 -> supergroupChatCreated = decodeBooleanElement(descriptor, 17)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            check(supergroupChatCreated) { "Not a SupergroupChatCreatedMessage!" }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            SupergroupChatCreatedMessage(id, sender, date, chat)
        }

        override fun serialize(encoder: Encoder, value: SupergroupChatCreatedMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Supergroup.serializer(), chat)
                encodeBooleanElement(descriptor, 17, true)
            }
        }
    }
}

@Serializable(ChannelChatCreatedMessage.Serializer::class)
data class ChannelChatCreatedMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Channel
) : Message() {
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

    object Serializer : KSerializer<ChannelChatCreatedMessage> {
        override val descriptor = buildClassSerialDescriptor("ChannelChatCreatedMessage") {
            messageElements()
            element<Boolean>("channel_chat_created")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Channel? = null
            var channelChatCreated = false
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Channel.serializer(), chat)
                    17 -> channelChatCreated = decodeBooleanElement(descriptor, 17)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            check(channelChatCreated) { "Not a ChannelChatCreatedMessage!" }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            ChannelChatCreatedMessage(id, sender, date, chat)
        }

        override fun serialize(encoder: Encoder, value: ChannelChatCreatedMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Channel.serializer(), chat)
                encodeBooleanElement(descriptor, 17, true)
            }
        }
    }
}

@Serializable(MigrateToChatIdMessage.Serializer::class)
data class MigrateToChatIdMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    val migrateToChatId: Long
) : Message() {
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

    object Serializer : KSerializer<MigrateToChatIdMessage> {
        override val descriptor = buildClassSerialDescriptor("MigrateToChatIdMessage") {
            messageElements()
            element<Long>("migrate_to_chat_id")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var migrateToChatId: Long? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    17 -> migrateToChatId = decodeLongElement(descriptor, 17)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            requireNotNull(migrateToChatId)
            MigrateToChatIdMessage(id, sender, date, chat, migrateToChatId)
        }

        override fun serialize(encoder: Encoder, value: MigrateToChatIdMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat, migrateToChatId) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                encodeLongElement(descriptor, 17, migrateToChatId)
            }
        }
    }
}

@Serializable(MigrateFromChatIdMessage.Serializer::class)
data class MigrateFromChatIdMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    val migrateFromChatId: Long
) : Message() {
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

    object Serializer : KSerializer<MigrateFromChatIdMessage> {
        override val descriptor = buildClassSerialDescriptor("MigrateFromChatIdMessage") {
            messageElements()
            element<Long>("migrate_from_chat_id")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var migrateFromChatId: Long? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    17 -> migrateFromChatId = decodeLongElement(descriptor, 17)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            requireNotNull(migrateFromChatId)
            MigrateFromChatIdMessage(id, sender, date, chat, migrateFromChatId)
        }

        override fun serialize(encoder: Encoder, value: MigrateFromChatIdMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat, migrateFromChatId) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                encodeLongElement(descriptor, 17, migrateFromChatId)
            }
        }
    }
}

@Serializable(PinnedMessageMessage.Serializer::class)
data class PinnedMessageMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    val pinnedMessage: Message
) : Message() {
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

    object Serializer : KSerializer<PinnedMessageMessage> {
        override val descriptor = buildClassSerialDescriptor("PinnedMessageMessage") {
            messageElements()
            element<Long>("pinned_message")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var pinnedMessage: Message? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    17 -> pinnedMessage = decodeSerializableElement(
                        descriptor,
                        17,
                        Message.serializer(),
                        pinnedMessage
                    )
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            requireNotNull(pinnedMessage)
            PinnedMessageMessage(id, sender, date, chat, pinnedMessage)
        }

        override fun serialize(encoder: Encoder, value: PinnedMessageMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat, pinnedMessage) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                encodeSerializableElement(descriptor, 17, Message.serializer(), pinnedMessage)
            }
        }
    }
}

@Serializable(ProximityAlertTriggeredMessage.Serializer::class)
data class ProximityAlertTriggeredMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    val proximityAlertTriggered: ProximityAlertTriggered
) : Message() {
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

    object Serializer : KSerializer<ProximityAlertTriggeredMessage> {
        override val descriptor = buildClassSerialDescriptor("ProximityAlertTriggeredMessage") {
            messageElements()
            element<ProximityAlertTriggered>("proximity_alert_triggered")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var proximityAlertTriggered: ProximityAlertTriggered? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    17 -> proximityAlertTriggered = decodeSerializableElement(
                        descriptor,
                        17,
                        ProximityAlertTriggered.serializer(),
                        proximityAlertTriggered
                    )
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            requireNotNull(proximityAlertTriggered)
            ProximityAlertTriggeredMessage(id, sender, date, chat, proximityAlertTriggered)
        }

        override fun serialize(encoder: Encoder, value: ProximityAlertTriggeredMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat, proximityAlertTriggered) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                encodeSerializableElement(
                    descriptor,
                    17,
                    ProximityAlertTriggered.serializer(),
                    proximityAlertTriggered
                )
            }
        }
    }
}

@Serializable(UnknownMessage.Serializer::class)
data class UnknownMessage(
    override val id: Long,
    override val sender: Sender,
    override val date: Long = 0L,
    override val chat: Chat,
    override val replyToMessage: Message? = null,
    override val viaBot: Bot? = null,
    override val lastEditDate: Long? = null,
    override val mediaGroupId: String? = null,
    override val authorSignature: String? = null,
    override val replyMarkup: InlineKeyboard? = null,
    override val forwardFrom: Sender? = null,
    override val forwardFromMessageId: Long? = null,
    override val forwardSignature: String? = null,
    override val forwardSenderName: String? = null,
    override val forwardDate: Long? = null
) : Message() {
    override fun toMessageContent(): Nothing? = null

    object Serializer : KSerializer<UnknownMessage> {
        override val descriptor = buildClassSerialDescriptor("UnknownMessage") {
            messageElements()
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var sender: Sender? = null
            var date: Long = 0L
            var chat: Chat? = null
            var replyToMessage: Message? = null
            var viaBot: Bot? = null
            var lastEditDate: Long? = null
            var mediaGroupId: String? = null
            var authorSignature: String? = null
            var forwardFrom: Sender? = null
            var forwardFromMessageId: Long? = null
            var forwardSignature: String? = null
            var forwardSenderName: String? = null
            var forwardDate: Long? = null
            var replyMarkup: InlineKeyboard? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeLongElement(descriptor, 0)
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> sender =
                        decodeSerializableElement(descriptor, 2, Sender.serializer(), sender)
                    3 -> date = decodeLongElement(descriptor, 3)
                    4 -> chat = decodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                    5 -> replyToMessage = decodeSerializableElement(
                        descriptor,
                        5,
                        Message.serializer(),
                        replyToMessage
                    )
                    6 -> viaBot = decodeSerializableElement(descriptor, 6, Bot.serializer(), viaBot)
                    7 -> lastEditDate = decodeLongElement(descriptor, 7)
                    8 -> mediaGroupId = decodeStringElement(descriptor, 8)
                    9 -> authorSignature = decodeStringElement(descriptor, 9)
                    10 -> forwardFrom =
                        decodeSerializableElement(descriptor, 10, Sender.serializer(), forwardFrom)
                    11 -> forwardFrom =
                        decodeSerializableElement(descriptor, 11, Sender.serializer(), forwardFrom)
                    12 -> forwardFromMessageId = decodeLongElement(descriptor, 12)
                    13 -> forwardSignature = decodeStringElement(descriptor, 13)
                    14 -> forwardSenderName = decodeStringElement(descriptor, 14)
                    15 -> forwardDate = decodeLongElement(descriptor, 15)
                    16 -> replyMarkup = decodeSerializableElement(
                        descriptor,
                        16,
                        InlineKeyboard.serializer(),
                        replyMarkup
                    )
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(sender)
            requireNotNull(chat)
            UnknownMessage(
                id,
                sender,
                date,
                chat,
                replyToMessage,
                viaBot,
                lastEditDate,
                mediaGroupId,
                authorSignature,
                replyMarkup,
                forwardFrom,
                forwardFromMessageId,
                forwardSignature,
                forwardSenderName,
                forwardDate
            )
        }

        override fun serialize(encoder: Encoder, value: UnknownMessage) {
            encoder.encodeStructure(descriptor) {
                val (id, sender, date, chat, replyToMessage, viaBot, lastEditDate, mediaGroupId, authorSignature, replyMarkup, forwardFrom, forwardFromMessageId, forwardSignature, forwardSenderName, forwardDate) = value
                encodeLongElement(descriptor, 0, id)
                when (sender) {
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        2,
                        Sender.serializer(),
                        sender
                    )
                    else -> encodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                }
                encodeLongElement(descriptor, 3, date)
                encodeSerializableElement(descriptor, 4, Chat.serializer(), chat)
                if (replyToMessage != null) encodeSerializableElement(
                    descriptor,
                    5,
                    Message.serializer(),
                    replyToMessage
                )
                if (viaBot != null) encodeSerializableElement(
                    descriptor,
                    6,
                    Bot.serializer(),
                    viaBot
                )
                if (lastEditDate != null) encodeLongElement(descriptor, 7, lastEditDate)
                if (mediaGroupId != null) encodeStringElement(descriptor, 8, mediaGroupId)
                if (authorSignature != null) encodeStringElement(descriptor, 9, authorSignature)
                when (forwardFrom) {
                    is User, is Bot -> encodeSerializableElement(
                        descriptor,
                        10,
                        Sender.serializer(),
                        forwardFrom
                    )
                    is Anonymous -> encodeSerializableElement(
                        descriptor,
                        11,
                        Sender.serializer(),
                        forwardFrom
                    )
                }
                if (forwardFromMessageId != null) encodeLongElement(
                    descriptor,
                    12,
                    forwardFromMessageId
                )
                if (forwardSignature != null) encodeStringElement(descriptor, 13, forwardSignature)
                if (forwardSenderName != null) encodeStringElement(
                    descriptor,
                    14,
                    forwardSenderName
                )
                if (forwardDate != null) encodeLongElement(descriptor, 15, forwardDate)
                if (replyMarkup != null) encodeSerializableElement(
                    descriptor,
                    16,
                    InlineKeyboard.serializer(),
                    replyMarkup
                )
            }
        }
    }
}

val Message.chatId get() = chat.chatId
val Message.messageId get() = MessageId(chatId, id)

private fun ClassSerialDescriptorBuilder.messageElements() {
    element<Long>("message_id")
    element<Sender?>("from")
    element<Anonymous?>("sender_chat")
    element<Long?>("date")
    element<Chat>("chat")
    element<Message?>("reply_to_message")
    element<Bot?>("via_bot")
    element<Long?>("edit_date")
    element<String?>("media_group_id")
    element<String?>("author_signature")
    element<Sender?>("forward_from")
    element<Anonymous?>("forward_from_chat")
    element<Long?>("forward_from_message_id")
    element<String?>("forward_signature")
    element<String?>("forward_sender_name")
    element<Long?>("forward_date")
    element<InlineKeyboard?>("reply_markup")
}