package com.github.ageofwar.ktelegram

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable(MessageContent.Serializer::class)
sealed class MessageContent<T : Message> {
    object Serializer : JsonContentPolymorphicSerializer<MessageContent<*>>(MessageContent::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out MessageContent<*>> {
            val json = element.jsonObject
            return when {
                "text" in json -> TextContent.serializer()
                "latitude" in json -> if ("title" in json) VenueContent.serializer() else LocationContent.serializer()
                "phone_number" in json -> ContactContent.serializer()
                "photo" in json -> PhotoContent.serializer()
                "audio" in json -> AudioContent.serializer()
                "animation" in json -> AnimationContent.serializer()
                "document" in json -> DocumentContent.serializer()
                "sticker" in json -> StickerContent.serializer()
                "video" in json -> VideoContent.serializer()
                "video_note" in json -> VideoNoteContent.serializer()
                "voice" in json -> VoiceContent.serializer()
                "emoji" in json -> DiceContent.serializer()
                "game_short_name" in json -> GameContent.serializer()
                "question" in json -> PollContent.serializer()
                else -> throw SerializationException("Unknown MessageContent")
            }
        }
    }

    open class AlternateSerializer(private val textSerializer: KSerializer<Text>) : JsonContentPolymorphicSerializer<MessageContent<*>>(MessageContent::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out MessageContent<*>> {
            val json = element.jsonObject
            return when {
                "text" in json -> TextContent.AlternateSerializer(textSerializer)
                "latitude" in json -> if ("title" in json) VenueContent.serializer() else LocationContent.serializer()
                "phone_number" in json -> ContactContent.serializer()
                "photo" in json -> PhotoContent.AlternateSerializer(textSerializer)
                "audio" in json -> AudioContent.AlternateSerializer(textSerializer)
                "animation" in json -> AnimationContent.AlternateSerializer(textSerializer)
                "document" in json -> DocumentContent.AlternateSerializer(textSerializer)
                "sticker" in json -> StickerContent.serializer()
                "video" in json -> VideoContent.AlternateSerializer(textSerializer)
                "video_note" in json -> VideoNoteContent.serializer()
                "voice" in json -> VoiceContent.AlternateSerializer(textSerializer)
                "emoji" in json -> DiceContent.serializer()
                "game_short_name" in json -> GameContent.serializer()
                "question" in json -> PollContent.serializer()
                else -> throw SerializationException("Unknown MessageContent")
            }
        }
    }

    object MarkdownSerializer : AlternateSerializer(Text.MarkdownSerializer)
    object HtmlSerializer : AlternateSerializer(Text.HtmlSerializer)
    object PlainSerializer : AlternateSerializer(Text.PlainSerializer)
}

@Serializable(TextContent.Serializer::class)
data class TextContent(
    val text: Text,
    val disableWebPagePreview: Boolean = false
) : MessageContent<TextMessage>() {
    object Serializer : KSerializer<TextContent> {
        override val descriptor = buildClassSerialDescriptor("TextContent") {
            element<String>("text")
            element<List<MessageEntity>?>("entities")
            element<Boolean?>("disable_web_page_preview")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var messageText: String? = null
            var entities: List<MessageEntity> = emptyList()
            var disableWebPagePreview = false
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> messageText = decodeStringElement(descriptor, 0)
                    1 -> entities = decodeSerializableElement(
                        descriptor,
                        1,
                        ListSerializer(MessageEntity.serializer()),
                        entities
                    )
                    2 -> disableWebPagePreview = decodeBooleanElement(descriptor, 2)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(messageText)
            TextContent(Text(messageText, entities), disableWebPagePreview)
        }

        override fun serialize(encoder: Encoder, value: TextContent) =
            encoder.encodeStructure(descriptor) {
                encodeStringElement(descriptor, 0, value.text.text)
                if (value.text.entities.isNotEmpty()) encodeSerializableElement(
                    descriptor,
                    1,
                    ListSerializer(MessageEntity.serializer()),
                    value.text.entities
                )
                if (value.disableWebPagePreview) encodeBooleanElement(descriptor, 2, true)
            }
    }

    open class AlternateSerializer(private val textSerializer: KSerializer<Text>) : KSerializer<TextContent> {
        override val descriptor = buildClassSerialDescriptor("TextContent") {
            element<String>("text")
            element<Boolean?>("disable_web_page_preview")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var text: Text? = null
            var disableWebPagePreview = false
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> text = decodeSerializableElement(descriptor, 0, textSerializer, text)
                    1 -> disableWebPagePreview = decodeBooleanElement(descriptor, 2)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(text)
            TextContent(text, disableWebPagePreview)
        }

        override fun serialize(encoder: Encoder, value: TextContent) =
            encoder.encodeStructure(descriptor) {
                encodeSerializableElement(descriptor, 0, textSerializer, value.text)
                if (value.disableWebPagePreview) encodeBooleanElement(descriptor, 1, true)
            }
    }

    object MarkdownSerializer : AlternateSerializer(Text.MarkdownSerializer)
    object HtmlSerializer : AlternateSerializer(Text.HtmlSerializer)
    object PlainSerializer : AlternateSerializer(Text.PlainSerializer)
}

@Serializable(PhotoContent.Serializer::class)
data class PhotoContent(
    val photo: OutputFile,
    val caption: Text? = null
) : MessageContent<PhotoMessage>() {
    object Serializer : KSerializer<PhotoContent> {
        override val descriptor = buildClassSerialDescriptor("PhotoContent") {
            element<OutputFile>("photo")
            element<String?>("caption")
            element<List<MessageEntity>?>("caption_entities")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var photo: OutputFile? = null
            var caption: String? = null
            var entities: List<MessageEntity> = emptyList()
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> photo =
                        decodeSerializableElement(descriptor, 0, OutputFile.serializer(), photo)
                    1 -> caption = decodeStringElement(descriptor, 1)
                    2 -> entities = decodeSerializableElement(
                        descriptor,
                        2,
                        ListSerializer(MessageEntity.serializer()),
                        entities
                    )
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(photo)
            val text = caption?.let { Text(it, entities) }
            PhotoContent(photo, text)
        }

        override fun serialize(encoder: Encoder, value: PhotoContent) =
            encoder.encodeStructure(descriptor) {
                val (photo, caption) = value
                encodeSerializableElement(descriptor, 0, OutputFile.serializer(), photo)
                if (caption != null) {
                    encodeStringElement(descriptor, 1, caption.text)
                    if (caption.entities.isNotEmpty()) encodeSerializableElement(
                        descriptor,
                        2,
                        ListSerializer(MessageEntity.serializer()),
                        caption.entities
                    )
                }
            }
    }

    open class AlternateSerializer(private val textSerializer: KSerializer<Text>) : KSerializer<PhotoContent> {
        override val descriptor = buildClassSerialDescriptor("PhotoContent") {
            element<OutputFile>("photo")
            element<Text?>("caption")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var photo: OutputFile? = null
            var caption: Text? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> photo = decodeSerializableElement(descriptor, 0, OutputFile.serializer(), photo)
                    1 -> caption = decodeSerializableElement(descriptor, 1, textSerializer, caption)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(photo)
            PhotoContent(photo, caption)
        }

        override fun serialize(encoder: Encoder, value: PhotoContent) =
            encoder.encodeStructure(descriptor) {
                val (photo, caption) = value
                encodeSerializableElement(descriptor, 0, OutputFile.serializer(), photo)
                if (caption != null) encodeSerializableElement(descriptor, 1, textSerializer, caption)
            }
    }

    object MarkdownSerializer : AlternateSerializer(Text.MarkdownSerializer)
    object HtmlSerializer : AlternateSerializer(Text.HtmlSerializer)
    object PlainSerializer : AlternateSerializer(Text.PlainSerializer)
}

@Serializable(AudioContent.Serializer::class)
data class AudioContent(
    val audio: OutputFile,
    val caption: Text? = null,
    val duration: Int? = null,
    val performer: String? = null,
    val title: String? = null,
    val thumbnail: OutputFile? = null
) : MessageContent<AudioMessage>() {
    object Serializer : KSerializer<AudioContent> {
        override val descriptor = buildClassSerialDescriptor("AudioContent") {
            element<OutputFile>("audio")
            element<String?>("caption")
            element<List<MessageEntity>?>("caption_entities")
            element<Int?>("duration")
            element<String?>("performer")
            element<String?>("title")
            element<OutputFile?>("thumb")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var audio: OutputFile? = null
            var caption: String? = null
            var entities: List<MessageEntity> = emptyList()
            var duration: Int? = null
            var performer: String? = null
            var title: String? = null
            var thumbnail: OutputFile? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> audio =
                        decodeSerializableElement(descriptor, 0, OutputFile.serializer(), audio)
                    1 -> caption = decodeStringElement(descriptor, 1)
                    2 -> entities = decodeSerializableElement(
                        descriptor,
                        2,
                        ListSerializer(MessageEntity.serializer()),
                        entities
                    )
                    3 -> duration = decodeIntElement(descriptor, 3)
                    4 -> performer = decodeStringElement(descriptor, 4)
                    5 -> title = decodeStringElement(descriptor, 5)
                    6 -> thumbnail =
                        decodeSerializableElement(descriptor, 6, OutputFile.serializer(), thumbnail)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(audio)
            val text = caption?.let { Text(it, entities) }
            AudioContent(audio, text, duration, performer, title, thumbnail)
        }

        override fun serialize(encoder: Encoder, value: AudioContent) =
            encoder.encodeStructure(descriptor) {
                val (audio, caption, duration, performer, title, thumbnail) = value
                encodeSerializableElement(descriptor, 0, OutputFile.serializer(), audio)
                if (caption != null) {
                    encodeStringElement(descriptor, 1, caption.text)
                    if (caption.entities.isNotEmpty()) encodeSerializableElement(
                        descriptor,
                        2,
                        ListSerializer(MessageEntity.serializer()),
                        caption.entities
                    )
                }
                if (duration != null) encodeIntElement(descriptor, 3, duration)
                if (performer != null) encodeStringElement(descriptor, 4, performer)
                if (title != null) encodeStringElement(descriptor, 5, title)
                if (thumbnail != null) encodeSerializableElement(
                    descriptor,
                    6,
                    OutputFile.serializer(),
                    thumbnail
                )
            }
    }

    open class AlternateSerializer(private val textSerializer: KSerializer<Text>) : KSerializer<AudioContent> {
        override val descriptor = buildClassSerialDescriptor("AudioContent") {
            element<OutputFile>("audio")
            element<Text?>("caption")
            element<Int?>("duration")
            element<String?>("performer")
            element<String?>("title")
            element<OutputFile?>("thumb")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var audio: OutputFile? = null
            var caption: Text? = null
            var duration: Int? = null
            var performer: String? = null
            var title: String? = null
            var thumbnail: OutputFile? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> audio = decodeSerializableElement(descriptor, 0, OutputFile.serializer(), audio)
                    1 -> caption = decodeSerializableElement(descriptor, 1, textSerializer, caption)
                    2 -> duration = decodeIntElement(descriptor, 2)
                    3 -> performer = decodeStringElement(descriptor, 3)
                    4 -> title = decodeStringElement(descriptor, 4)
                    5 -> thumbnail = decodeSerializableElement(descriptor, 5, OutputFile.serializer(), thumbnail)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(audio)
            AudioContent(audio, caption, duration, performer, title, thumbnail)
        }

        override fun serialize(encoder: Encoder, value: AudioContent) =
            encoder.encodeStructure(descriptor) {
                val (audio, caption, duration, performer, title, thumbnail) = value
                encodeSerializableElement(descriptor, 0, OutputFile.serializer(), audio)
                if (caption != null) encodeSerializableElement(descriptor, 1, textSerializer, caption)
                if (duration != null) encodeIntElement(descriptor, 2, duration)
                if (performer != null) encodeStringElement(descriptor, 3, performer)
                if (title != null) encodeStringElement(descriptor, 4, title)
                if (thumbnail != null) encodeSerializableElement(descriptor, 5, OutputFile.serializer(), thumbnail)
            }
    }

    object MarkdownSerializer : AlternateSerializer(Text.MarkdownSerializer)
    object HtmlSerializer : AlternateSerializer(Text.HtmlSerializer)
    object PlainSerializer : AlternateSerializer(Text.PlainSerializer)
}

@Serializable(AnimationContent.Serializer::class)
data class AnimationContent(
    val animation: OutputFile,
    val caption: Text? = null,
    val duration: Int? = null,
    val width: Int? = null,
    val height: Int? = null,
    val thumbnail: OutputFile? = null
) : MessageContent<AnimationMessage>() {
    object Serializer : KSerializer<AnimationContent> {
        override val descriptor = buildClassSerialDescriptor("AnimationContent") {
            element<OutputFile>("animation")
            element<String?>("caption")
            element<List<MessageEntity>?>("caption_entities")
            element<Int?>("duration")
            element<Int?>("width")
            element<Int?>("height")
            element<OutputFile?>("thumb")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var animation: OutputFile? = null
            var caption: String? = null
            var entities: List<MessageEntity> = emptyList()
            var duration: Int? = null
            var width: Int? = null
            var height: Int? = null
            var thumbnail: OutputFile? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> animation =
                        decodeSerializableElement(descriptor, 0, OutputFile.serializer(), animation)
                    1 -> caption = decodeStringElement(descriptor, 1)
                    2 -> entities = decodeSerializableElement(
                        descriptor,
                        2,
                        ListSerializer(MessageEntity.serializer()),
                        entities
                    )
                    3 -> duration = decodeIntElement(descriptor, 3)
                    4 -> width = decodeIntElement(descriptor, 4)
                    5 -> height = decodeIntElement(descriptor, 5)
                    6 -> thumbnail =
                        decodeSerializableElement(descriptor, 6, OutputFile.serializer(), thumbnail)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(animation)
            val text = caption?.let { Text(it, entities) }
            AnimationContent(animation, text, duration, width, height, thumbnail)
        }

        override fun serialize(encoder: Encoder, value: AnimationContent) =
            encoder.encodeStructure(descriptor) {
                val (animation, caption, duration, width, height, thumbnail) = value
                encodeSerializableElement(descriptor, 0, OutputFile.serializer(), animation)
                if (caption != null) {
                    encodeStringElement(descriptor, 1, caption.text)
                    if (caption.entities.isNotEmpty()) encodeSerializableElement(
                        descriptor,
                        2,
                        ListSerializer(MessageEntity.serializer()),
                        caption.entities
                    )
                }
                if (duration != null) encodeIntElement(descriptor, 3, duration)
                if (width != null) encodeIntElement(descriptor, 4, width)
                if (height != null) encodeIntElement(descriptor, 5, height)
                if (thumbnail != null) encodeSerializableElement(
                    descriptor,
                    6,
                    OutputFile.serializer(),
                    thumbnail
                )
            }
    }

    open class AlternateSerializer(private val textSerializer: KSerializer<Text>) : KSerializer<AnimationContent> {
        override val descriptor = buildClassSerialDescriptor("AnimationContent") {
            element<OutputFile>("animation")
            element<Text?>("caption")
            element<Int?>("duration")
            element<Int?>("width")
            element<Int?>("height")
            element<OutputFile?>("thumb")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var animation: OutputFile? = null
            var caption: Text? = null
            var duration: Int? = null
            var width: Int? = null
            var height: Int? = null
            var thumbnail: OutputFile? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> animation =
                        decodeSerializableElement(descriptor, 0, OutputFile.serializer(), animation)
                    1 -> caption = decodeSerializableElement(descriptor, 1, textSerializer, caption)
                    3 -> duration = decodeIntElement(descriptor, 3)
                    4 -> width = decodeIntElement(descriptor, 4)
                    5 -> height = decodeIntElement(descriptor, 5)
                    6 -> thumbnail =
                        decodeSerializableElement(descriptor, 6, OutputFile.serializer(), thumbnail)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(animation)
            AnimationContent(animation, caption, duration, width, height, thumbnail)
        }

        override fun serialize(encoder: Encoder, value: AnimationContent) =
            encoder.encodeStructure(descriptor) {
                val (animation, caption, duration, width, height, thumbnail) = value
                encodeSerializableElement(descriptor, 0, OutputFile.serializer(), animation)
                if (caption != null) encodeSerializableElement(descriptor, 1, textSerializer, caption)
                if (duration != null) encodeIntElement(descriptor, 2, duration)
                if (width != null) encodeIntElement(descriptor, 3, width)
                if (height != null) encodeIntElement(descriptor, 4, height)
                if (thumbnail != null) encodeSerializableElement(descriptor, 5, OutputFile.serializer(), thumbnail)
            }
    }

    object MarkdownSerializer : AlternateSerializer(Text.MarkdownSerializer)
    object HtmlSerializer : AlternateSerializer(Text.HtmlSerializer)
    object PlainSerializer : AlternateSerializer(Text.PlainSerializer)
}

@Serializable(DocumentContent.Serializer::class)
data class DocumentContent(
    val document: OutputFile,
    val caption: Text? = null,
    val thumbnail: OutputFile? = null,
    val disableContentTypeDetection: Boolean = false
) : MessageContent<DocumentMessage>() {
    object Serializer : KSerializer<DocumentContent> {
        override val descriptor = buildClassSerialDescriptor("DocumentContent") {
            element<OutputFile>("document")
            element<String?>("caption")
            element<List<MessageEntity>?>("caption_entities")
            element<OutputFile?>("thumb")
            element<Boolean?>("disable_content_type_detection")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var document: OutputFile? = null
            var caption: String? = null
            var entities: List<MessageEntity> = emptyList()
            var thumbnail: OutputFile? = null
            var disableContentTypeDetection = false
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> document =
                        decodeSerializableElement(descriptor, 0, OutputFile.serializer(), document)
                    1 -> caption = decodeStringElement(descriptor, 1)
                    2 -> entities = decodeSerializableElement(
                        descriptor,
                        2,
                        ListSerializer(MessageEntity.serializer()),
                        entities
                    )
                    3 -> thumbnail =
                        decodeSerializableElement(descriptor, 3, OutputFile.serializer(), thumbnail)
                    4 -> disableContentTypeDetection = decodeBooleanElement(descriptor, 4)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(document)
            val text = caption?.let { Text(it, entities) }
            DocumentContent(document, text, thumbnail, disableContentTypeDetection)
        }

        override fun serialize(encoder: Encoder, value: DocumentContent) =
            encoder.encodeStructure(descriptor) {
                val (document, caption, thumbnail, disableContentTypeDetection) = value
                encodeSerializableElement(descriptor, 0, OutputFile.serializer(), document)
                if (caption != null) {
                    encodeStringElement(descriptor, 1, caption.text)
                    if (caption.entities.isNotEmpty()) encodeSerializableElement(
                        descriptor,
                        2,
                        ListSerializer(MessageEntity.serializer()),
                        caption.entities
                    )
                }
                if (thumbnail != null) encodeSerializableElement(
                    descriptor,
                    3,
                    OutputFile.serializer(),
                    thumbnail
                )
                if (disableContentTypeDetection) encodeBooleanElement(descriptor, 4, true)
            }
    }

    open class AlternateSerializer(private val textSerializer: KSerializer<Text>) : KSerializer<DocumentContent> {
        override val descriptor = buildClassSerialDescriptor("DocumentContent") {
            element<OutputFile>("document")
            element<Text?>("caption")
            element<OutputFile?>("thumb")
            element<Boolean?>("disable_content_type_detection")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var document: OutputFile? = null
            var caption: Text? = null
            var thumbnail: OutputFile? = null
            var disableContentTypeDetection = false
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> document =
                        decodeSerializableElement(descriptor, 0, OutputFile.serializer(), document)
                    1 -> caption = decodeSerializableElement(descriptor, 1, textSerializer, caption)
                    2 -> thumbnail =
                        decodeSerializableElement(descriptor, 2, OutputFile.serializer(), thumbnail)
                    3 -> disableContentTypeDetection = decodeBooleanElement(descriptor, 3)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(document)
            DocumentContent(document, caption, thumbnail, disableContentTypeDetection)
        }

        override fun serialize(encoder: Encoder, value: DocumentContent) =
            encoder.encodeStructure(descriptor) {
                val (document, caption, thumbnail, disableContentTypeDetection) = value
                encodeSerializableElement(descriptor, 0, OutputFile.serializer(), document)
                if (caption != null) encodeSerializableElement(descriptor, 1, textSerializer, caption)
                if (thumbnail != null) encodeSerializableElement(descriptor, 2, OutputFile.serializer(), thumbnail)
                if (disableContentTypeDetection) encodeBooleanElement(descriptor, 3, true)
            }
    }

    object MarkdownSerializer : AlternateSerializer(Text.MarkdownSerializer)
    object HtmlSerializer : AlternateSerializer(Text.HtmlSerializer)
    object PlainSerializer : AlternateSerializer(Text.PlainSerializer)
}

@Serializable
data class StickerContent(
    val sticker: OutputFile
) : MessageContent<StickerMessage>()

@Serializable(VideoContent.Serializer::class)
data class VideoContent(
    val video: OutputFile,
    val caption: Text? = null,
    val duration: Int? = null,
    val width: Int? = null,
    val height: Int? = null,
    val thumbnail: OutputFile? = null,
    val supportsStreaming: Boolean = false
) : MessageContent<VideoMessage>() {
    object Serializer : KSerializer<VideoContent> {
        override val descriptor = buildClassSerialDescriptor("VideoContent") {
            element<OutputFile>("video")
            element<String?>("caption")
            element<List<MessageEntity>?>("caption_entities")
            element<Int?>("duration")
            element<Int?>("width")
            element<Int?>("height")
            element<OutputFile?>("thumb")
            element<Boolean?>("supports_streaming")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var video: OutputFile? = null
            var caption: String? = null
            var entities: List<MessageEntity> = emptyList()
            var duration: Int? = null
            var width: Int? = null
            var height: Int? = null
            var thumbnail: OutputFile? = null
            var supportsStreaming = false
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> video =
                        decodeSerializableElement(descriptor, 0, OutputFile.serializer(), video)
                    1 -> caption = decodeStringElement(descriptor, 1)
                    2 -> entities = decodeSerializableElement(
                        descriptor,
                        2,
                        ListSerializer(MessageEntity.serializer()),
                        entities
                    )
                    3 -> duration = decodeIntElement(descriptor, 3)
                    4 -> width = decodeIntElement(descriptor, 4)
                    5 -> height = decodeIntElement(descriptor, 5)
                    6 -> thumbnail =
                        decodeSerializableElement(descriptor, 6, OutputFile.serializer(), thumbnail)
                    7 -> supportsStreaming = decodeBooleanElement(descriptor, 7)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(video)
            val text = caption?.let { Text(it, entities) }
            VideoContent(video, text, duration, width, height, thumbnail, supportsStreaming)
        }

        override fun serialize(encoder: Encoder, value: VideoContent) =
            encoder.encodeStructure(descriptor) {
                val (video, caption, duration, width, height, thumbnail, supportsStreaming) = value
                encodeSerializableElement(descriptor, 0, OutputFile.serializer(), video)
                if (caption != null) {
                    encodeStringElement(descriptor, 1, caption.text)
                    if (caption.entities.isNotEmpty()) encodeSerializableElement(
                        descriptor,
                        2,
                        ListSerializer(MessageEntity.serializer()),
                        caption.entities
                    )
                }
                if (duration != null) encodeIntElement(descriptor, 3, duration)
                if (width != null) encodeIntElement(descriptor, 4, width)
                if (height != null) encodeIntElement(descriptor, 5, height)
                if (thumbnail != null) encodeSerializableElement(
                    descriptor,
                    6,
                    OutputFile.serializer(),
                    thumbnail
                )
                if (supportsStreaming) encodeBooleanElement(descriptor, 7, true)
            }
    }

    open class AlternateSerializer(private val textSerializer: KSerializer<Text>) : KSerializer<VideoContent> {
        override val descriptor = buildClassSerialDescriptor("VideoContent") {
            element<OutputFile>("video")
            element<Text?>("caption")
            element<Int?>("duration")
            element<Int?>("width")
            element<Int?>("height")
            element<OutputFile?>("thumb")
            element<Boolean?>("supports_streaming")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var video: OutputFile? = null
            var caption: Text? = null
            var duration: Int? = null
            var width: Int? = null
            var height: Int? = null
            var thumbnail: OutputFile? = null
            var supportsStreaming = false
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> video =
                        decodeSerializableElement(descriptor, 0, OutputFile.serializer(), video)
                    1 -> caption = decodeSerializableElement(descriptor, 1, textSerializer, caption)
                    2 -> duration = decodeIntElement(descriptor, 2)
                    3 -> width = decodeIntElement(descriptor, 3)
                    4 -> height = decodeIntElement(descriptor, 4)
                    5 -> thumbnail = decodeSerializableElement(descriptor, 5, OutputFile.serializer(), thumbnail)
                    6 -> supportsStreaming = decodeBooleanElement(descriptor, 6)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(video)
            VideoContent(video, caption, duration, width, height, thumbnail, supportsStreaming)
        }

        override fun serialize(encoder: Encoder, value: VideoContent) =
            encoder.encodeStructure(descriptor) {
                val (video, caption, duration, width, height, thumbnail, supportsStreaming) = value
                encodeSerializableElement(descriptor, 0, OutputFile.serializer(), video)
                if (caption != null) encodeSerializableElement(descriptor, 1, textSerializer, caption)
                if (duration != null) encodeIntElement(descriptor, 2, duration)
                if (width != null) encodeIntElement(descriptor, 3, width)
                if (height != null) encodeIntElement(descriptor, 4, height)
                if (thumbnail != null) encodeSerializableElement(descriptor, 5, OutputFile.serializer(), thumbnail)
                if (supportsStreaming) encodeBooleanElement(descriptor, 6, true)
            }
    }

    object MarkdownSerializer : AlternateSerializer(Text.MarkdownSerializer)
    object HtmlSerializer : AlternateSerializer(Text.HtmlSerializer)
    object PlainSerializer : AlternateSerializer(Text.PlainSerializer)
}

@Serializable
data class VideoNoteContent(
    @SerialName("video_note") val videoNote: OutputFile,
    val duration: Int? = null,
    val length: Int? = null,
    @SerialName("thumb") val thumbnail: OutputFile? = null
) : MessageContent<VideoNoteMessage>()

@Serializable(VoiceContent.Serializer::class)
data class VoiceContent(
    val voice: OutputFile,
    val caption: Text? = null,
    val duration: Int? = null
) : MessageContent<VoiceMessage>() {
    object Serializer : KSerializer<VoiceContent> {
        override val descriptor = buildClassSerialDescriptor("VoiceContent") {
            element<OutputFile>("voice")
            element<String?>("caption")
            element<List<MessageEntity>?>("caption_entities")
            element<Int?>("duration")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var voice: OutputFile? = null
            var caption: String? = null
            var entities: List<MessageEntity> = emptyList()
            var duration: Int? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> voice =
                        decodeSerializableElement(descriptor, 0, OutputFile.serializer(), voice)
                    1 -> caption = decodeStringElement(descriptor, 1)
                    2 -> entities = decodeSerializableElement(
                        descriptor,
                        2,
                        ListSerializer(MessageEntity.serializer()),
                        entities
                    )
                    3 -> duration = decodeIntElement(descriptor, 3)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(voice)
            val text = caption?.let { Text(it, entities) }
            VoiceContent(voice, text, duration)
        }

        override fun serialize(encoder: Encoder, value: VoiceContent) =
            encoder.encodeStructure(descriptor) {
                val (voice, caption, duration) = value
                encodeSerializableElement(descriptor, 0, OutputFile.serializer(), voice)
                if (caption != null) {
                    encodeStringElement(descriptor, 1, caption.text)
                    if (caption.entities.isNotEmpty()) encodeSerializableElement(
                        descriptor,
                        2,
                        ListSerializer(MessageEntity.serializer()),
                        caption.entities
                    )
                }
                if (duration != null) encodeIntElement(descriptor, 3, duration)
            }
    }

    open class AlternateSerializer(private val textSerializer: KSerializer<Text>) : KSerializer<VoiceContent> {
        override val descriptor = buildClassSerialDescriptor("VoiceContent") {
            element<OutputFile>("voice")
            element<Text?>("caption")
            element<Int?>("duration")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var voice: OutputFile? = null
            var caption: Text? = null
            var duration: Int? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> voice = decodeSerializableElement(descriptor, 0, OutputFile.serializer(), voice)
                    1 -> caption = decodeSerializableElement(descriptor, 1, textSerializer, caption)
                    2 -> duration = decodeIntElement(descriptor, 2)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(voice)
            VoiceContent(voice, caption, duration)
        }

        override fun serialize(encoder: Encoder, value: VoiceContent) =
            encoder.encodeStructure(descriptor) {
                val (voice, caption, duration) = value
                encodeSerializableElement(descriptor, 0, OutputFile.serializer(), voice)
                if (caption != null) encodeSerializableElement(descriptor, 1, textSerializer, caption)
                if (duration != null) encodeIntElement(descriptor, 2, duration)
            }
    }

    object MarkdownSerializer : AlternateSerializer(Text.MarkdownSerializer)
    object HtmlSerializer : AlternateSerializer(Text.HtmlSerializer)
    object PlainSerializer : AlternateSerializer(Text.PlainSerializer)
}

@Serializable
data class ContactContent(
    @SerialName("phone_number") val phoneNumber: String,
    @SerialName("first_name") override val firstName: String,
    @SerialName("last_name") override val lastName: String? = null,
    val vcard: String? = null
) : MessageContent<ContactMessage>(), Name

@Serializable
data class DiceContent(
    val emoji: String
) : MessageContent<DiceMessage>()

@Serializable
data class GameContent(
    @SerialName("game_short_name") val gameShortName: String
) : MessageContent<GameMessage>()

@Serializable
sealed class PollContent : MessageContent<PollMessage>() {
    abstract val question: String
    abstract val options: List<String>
    abstract val isAnonymous: Boolean
    abstract val openPeriod: Int?
    abstract val closeDate: Int?
    abstract val isClosed: Boolean

    @Serializable
    @SerialName("regular")
    data class Regular(
        override val question: String,
        override val options: List<String>,
        @SerialName("is_anonymous") override val isAnonymous: Boolean = true,
        @SerialName("open_period") override val openPeriod: Int? = null,
        @SerialName("close_date") override val closeDate: Int? = null,
        @SerialName("is_closed") override val isClosed: Boolean = false,
        @SerialName("allow_multiple_answers") val allowsMultipleAnswers: Boolean = false
    ) : PollContent()

    @Serializable
    @SerialName("quiz")
    data class Quiz(
        override val question: String,
        override val options: List<String>,
        override val isAnonymous: Boolean = true,
        override val openPeriod: Int? = null,
        override val closeDate: Int? = null,
        override val isClosed: Boolean = false,
        val correctOption: Int,
        val explanation: Text? = null
    ) : PollContent() {
        object Serializer : KSerializer<Quiz> {
            override val descriptor = buildClassSerialDescriptor("quiz") {
                element<String>("question")
                element<List<String>>("options")
                element<Boolean?>("is_closed")
                element<Boolean?>("is_anonymous")
                element<Int>("correct_option_id")
                element<String?>("explanation")
                element<List<MessageEntity>?>("explanation_entities")
                element<Int?>("open_period")
                element<Int?>("close_date")
            }

            override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
                var question: String? = null
                var options: List<String>? = null
                var isClosed: Boolean? = null
                var isAnonymous: Boolean? = null
                var correctOptionId: Int? = null
                var explanation: String? = null
                var explanationEntities: List<MessageEntity> = emptyList()
                var openPeriod: Int? = null
                var closeDate: Int? = null
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> question = decodeStringElement(descriptor, 0)
                        1 -> options = decodeSerializableElement(
                            descriptor,
                            1,
                            ListSerializer(String.serializer()),
                            options
                        )
                        2 -> isClosed = decodeBooleanElement(descriptor, 2)
                        3 -> isAnonymous = decodeBooleanElement(descriptor, 3)
                        4 -> correctOptionId = decodeIntElement(descriptor, 4)
                        5 -> explanation = decodeStringElement(descriptor, 5)
                        6 -> explanationEntities = decodeSerializableElement(
                            descriptor,
                            6,
                            ListSerializer(MessageEntity.serializer()),
                            explanationEntities
                        )
                        7 -> openPeriod = decodeIntElement(descriptor, 7)
                        8 -> closeDate = decodeIntElement(descriptor, 8)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
                requireNotNull(question)
                requireNotNull(options)
                requireNotNull(isClosed)
                requireNotNull(isAnonymous)
                requireNotNull(correctOptionId)
                val text = explanation?.let { Text(it, explanationEntities) }
                Quiz(
                    question,
                    options,
                    isAnonymous,
                    openPeriod,
                    closeDate,
                    isClosed,
                    correctOptionId,
                    text
                )
            }

            override fun serialize(encoder: Encoder, value: Quiz) =
                encoder.encodeStructure(descriptor) {
                    val (question, options, isAnonymous, openPeriod, closeDate, isClosed, correctOption, explanation) = value
                    encodeStringElement(descriptor, 0, question)
                    encodeSerializableElement(
                        descriptor,
                        1,
                        ListSerializer(String.serializer()),
                        options
                    )
                    encodeBooleanElement(descriptor, 2, isClosed)
                    encodeBooleanElement(descriptor, 3, isAnonymous)
                    encodeIntElement(descriptor, 4, correctOption)
                    if (explanation != null) encodeStringElement(descriptor, 5, explanation.text)
                    if (explanation != null && explanation.entities.isNotEmpty()) encodeSerializableElement(
                        descriptor,
                        6,
                        ListSerializer(MessageEntity.serializer()),
                        explanation.entities
                    )
                    if (openPeriod != null) encodeIntElement(descriptor, 7, openPeriod)
                    if (closeDate != null) encodeIntElement(descriptor, 8, closeDate)
                }
        }
    }
}

@Serializable(VenueContent.Serializer::class)
data class VenueContent(val venue: Venue) : MessageContent<VenueMessage>() {
    object Serializer : KSerializer<VenueContent> {
        override val descriptor = Venue.serializer().descriptor

        override fun deserialize(decoder: Decoder) =
            VenueContent(decoder.decodeSerializableValue(Venue.serializer()))

        override fun serialize(encoder: Encoder, value: VenueContent) =
            encoder.encodeSerializableValue(Venue.serializer(), value.venue)
    }
}

@Serializable(LocationContent.Serializer::class)
data class LocationContent(val location: Location) : MessageContent<LocationMessage>() {
    object Serializer : KSerializer<LocationContent> {
        override val descriptor = Location.serializer().descriptor

        override fun deserialize(decoder: Decoder) =
            LocationContent(decoder.decodeSerializableValue(Location.serializer()))

        override fun serialize(encoder: Encoder, value: LocationContent) =
            encoder.encodeSerializableValue(Location.serializer(), value.location)
    }
}
