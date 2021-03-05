package com.github.ageofwar.ktelegram

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*

@Serializable
sealed class OutputMedia {
    abstract val media: OutputFile
}

@Serializable(OutputMediaPhoto.Serializer::class)
@SerialName("photo")
data class OutputMediaPhoto(
    override val media: OutputFile,
    override val text: Text? = null,
) : OutputMedia(), WithText {
    object Serializer : KSerializer<OutputMediaPhoto> {
        override val descriptor = buildClassSerialDescriptor("photo") {
            element<OutputFile>("media")
            element<String?>("caption")
            element<String?>("caption_entities")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var media: OutputFile? = null
            var caption: String? = null
            var entities: List<MessageEntity> = emptyList()
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> media =
                        decodeSerializableElement(descriptor, 0, OutputFile.serializer(), media)
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
            requireNotNull(media)
            val text = caption?.let { Text(it, entities) }
            OutputMediaPhoto(media, text)
        }

        override fun serialize(encoder: Encoder, value: OutputMediaPhoto) =
            encoder.encodeStructure(descriptor) {
                val (media, text) = value
                encodeSerializableElement(descriptor, 0, OutputFile.serializer(), media)
                if (text != null) {
                    encodeStringElement(descriptor, 1, text.text)
                    if (text.entities.isNotEmpty()) encodeSerializableElement(
                        descriptor,
                        2,
                        ListSerializer(MessageEntity.serializer()),
                        text.entities
                    )
                }
            }
    }
}

@Serializable(OutputMediaVideo.Serializer::class)
@SerialName("video")
data class OutputMediaVideo(
    override val media: OutputFile,
    override val text: Text? = null,
    val thumbnail: OutputFile? = null,
    val width: Int? = null,
    val height: Int? = null,
    val duration: Int? = null,
    val supportsStreaming: Boolean = false
) : OutputMedia(), WithText {
    object Serializer : KSerializer<OutputMediaVideo> {
        override val descriptor = buildClassSerialDescriptor("video") {
            element<OutputFile>("media")
            element<String?>("caption")
            element<String?>("caption_entities")
            element<String?>("thumb")
            element<Int?>("width")
            element<Int?>("height")
            element<Int?>("duration")
            element<Boolean?>("supports_streaming")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var media: OutputFile? = null
            var caption: String? = null
            var entities: List<MessageEntity> = emptyList()
            var thumbnail: OutputFile? = null
            var width: Int? = null
            var height: Int? = null
            var duration: Int? = null
            var supportsStreaming = false
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> media =
                        decodeSerializableElement(descriptor, 0, OutputFile.serializer(), media)
                    1 -> caption = decodeStringElement(descriptor, 1)
                    2 -> entities = decodeSerializableElement(
                        descriptor,
                        2,
                        ListSerializer(MessageEntity.serializer()),
                        entities
                    )
                    3 -> thumbnail =
                        decodeSerializableElement(descriptor, 3, OutputFile.serializer(), thumbnail)
                    4 -> width = decodeIntElement(descriptor, 4)
                    5 -> height = decodeIntElement(descriptor, 5)
                    6 -> duration = decodeIntElement(descriptor, 6)
                    7 -> supportsStreaming = decodeBooleanElement(descriptor, 7)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(media)
            val text = caption?.let { Text(it, entities) }
            OutputMediaVideo(media, text, thumbnail, width, height, duration, supportsStreaming)
        }

        override fun serialize(encoder: Encoder, value: OutputMediaVideo) =
            encoder.encodeStructure(descriptor) {
                val (media, text, thumbnail, width, height, duration, supportsStreaming) = value
                encodeSerializableElement(descriptor, 0, OutputFile.serializer(), media)
                if (text != null) {
                    encodeStringElement(descriptor, 1, text.text)
                    if (text.entities.isNotEmpty()) encodeSerializableElement(
                        descriptor,
                        2,
                        ListSerializer(MessageEntity.serializer()),
                        text.entities
                    )
                }
                if (thumbnail != null) encodeSerializableElement(
                    descriptor,
                    3,
                    OutputFile.serializer(),
                    thumbnail
                )
                if (width != null) encodeIntElement(descriptor, 4, width)
                if (height != null) encodeIntElement(descriptor, 5, height)
                if (duration != null) encodeIntElement(descriptor, 6, duration)
                if (supportsStreaming) encodeBooleanElement(descriptor, 7, supportsStreaming)
            }
    }
}

@Serializable(OutputMediaAnimation.Serializer::class)
@SerialName("animation")
data class OutputMediaAnimation(
    override val media: OutputFile,
    override val text: Text? = null,
    val thumbnail: OutputFile? = null,
    val width: Int? = null,
    val height: Int? = null,
    val duration: Int? = null
) : OutputMedia(), WithText {
    object Serializer : KSerializer<OutputMediaAnimation> {
        override val descriptor = buildClassSerialDescriptor("animation") {
            element<OutputFile>("media")
            element<String?>("caption")
            element<String?>("caption_entities")
            element<String?>("thumb")
            element<Int?>("width")
            element<Int?>("height")
            element<Int?>("duration")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var media: OutputFile? = null
            var caption: String? = null
            var entities: List<MessageEntity> = emptyList()
            var thumbnail: OutputFile? = null
            var width: Int? = null
            var height: Int? = null
            var duration: Int? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> media =
                        decodeSerializableElement(descriptor, 0, OutputFile.serializer(), media)
                    1 -> caption = decodeStringElement(descriptor, 1)
                    2 -> entities = decodeSerializableElement(
                        descriptor,
                        2,
                        ListSerializer(MessageEntity.serializer()),
                        entities
                    )
                    3 -> thumbnail =
                        decodeSerializableElement(descriptor, 3, OutputFile.serializer(), thumbnail)
                    4 -> width = decodeIntElement(descriptor, 4)
                    5 -> height = decodeIntElement(descriptor, 5)
                    6 -> duration = decodeIntElement(descriptor, 6)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(media)
            val text = caption?.let { Text(it, entities) }
            OutputMediaAnimation(media, text, thumbnail, width, height, duration)
        }

        override fun serialize(encoder: Encoder, value: OutputMediaAnimation) =
            encoder.encodeStructure(descriptor) {
                val (media, text, thumbnail, width, height, duration) = value
                encodeSerializableElement(descriptor, 0, OutputFile.serializer(), media)
                if (text != null) {
                    encodeStringElement(descriptor, 1, text.text)
                    if (text.entities.isNotEmpty()) encodeSerializableElement(
                        descriptor,
                        2,
                        ListSerializer(MessageEntity.serializer()),
                        text.entities
                    )
                }
                if (thumbnail != null) encodeSerializableElement(
                    descriptor,
                    3,
                    OutputFile.serializer(),
                    thumbnail
                )
                if (width != null) encodeIntElement(descriptor, 4, width)
                if (height != null) encodeIntElement(descriptor, 5, height)
                if (duration != null) encodeIntElement(descriptor, 6, duration)
            }
    }
}

@Serializable(OutputMediaAudio.Serializer::class)
@SerialName("audio")
data class OutputMediaAudio(
    override val media: OutputFile,
    override val text: Text? = null,
    val thumbnail: OutputFile? = null,
    val duration: Int? = null,
    val performer: String? = null,
    val title: String? = null
) : OutputMedia(), WithText {
    object Serializer : KSerializer<OutputMediaAudio> {
        override val descriptor = buildClassSerialDescriptor("audio") {
            element<OutputFile>("media")
            element<String?>("caption")
            element<String?>("caption_entities")
            element<String?>("thumb")
            element<Int?>("duration")
            element<String?>("performer")
            element<String?>("title")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var media: OutputFile? = null
            var caption: String? = null
            var entities: List<MessageEntity> = emptyList()
            var thumbnail: OutputFile? = null
            var duration: Int? = null
            var performer: String? = null
            var title: String? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> media =
                        decodeSerializableElement(descriptor, 0, OutputFile.serializer(), media)
                    1 -> caption = decodeStringElement(descriptor, 1)
                    2 -> entities = decodeSerializableElement(
                        descriptor,
                        2,
                        ListSerializer(MessageEntity.serializer()),
                        entities
                    )
                    3 -> thumbnail =
                        decodeSerializableElement(descriptor, 3, OutputFile.serializer(), thumbnail)
                    4 -> duration = decodeIntElement(descriptor, 4)
                    5 -> performer = decodeStringElement(descriptor, 5)
                    6 -> title = decodeStringElement(descriptor, 6)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(media)
            val text = caption?.let { Text(it, entities) }
            OutputMediaAudio(media, text, thumbnail, duration, performer, title)
        }

        override fun serialize(encoder: Encoder, value: OutputMediaAudio) =
            encoder.encodeStructure(descriptor) {
                val (media, text, thumbnail, duration, performer, title) = value
                encodeSerializableElement(descriptor, 0, OutputFile.serializer(), media)
                if (text != null) {
                    encodeStringElement(descriptor, 1, text.text)
                    if (text.entities.isNotEmpty()) encodeSerializableElement(
                        descriptor,
                        2,
                        ListSerializer(MessageEntity.serializer()),
                        text.entities
                    )
                }
                if (thumbnail != null) encodeSerializableElement(
                    descriptor,
                    3,
                    OutputFile.serializer(),
                    thumbnail
                )
                if (duration != null) encodeIntElement(descriptor, 4, duration)
                if (performer != null) encodeStringElement(descriptor, 5, performer)
                if (title != null) encodeStringElement(descriptor, 6, title)
            }
    }
}

@Serializable(OutputMediaDocument.Serializer::class)
@SerialName("document")
data class OutputMediaDocument(
    override val media: OutputFile,
    override val text: Text? = null,
    val thumbnail: OutputFile? = null,
    val disableContentTypeDetection: Boolean = false
) : OutputMedia(), WithText {
    object Serializer : KSerializer<OutputMediaDocument> {
        override val descriptor = buildClassSerialDescriptor("document") {
            element<OutputFile>("media")
            element<String?>("caption")
            element<String?>("caption_entities")
            element<String?>("thumb")
            element<Boolean?>("disable_content_type_detection")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var media: OutputFile? = null
            var caption: String? = null
            var entities: List<MessageEntity> = emptyList()
            var thumbnail: OutputFile? = null
            var disableContentTypeDetection = false
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> media =
                        decodeSerializableElement(descriptor, 0, OutputFile.serializer(), media)
                    1 -> caption = decodeStringElement(descriptor, 1)
                    2 -> entities = decodeSerializableElement(
                        descriptor,
                        2,
                        ListSerializer(MessageEntity.serializer()),
                        entities
                    )
                    3 -> thumbnail =
                        decodeSerializableElement(descriptor, 3, OutputFile.serializer(), thumbnail)
                    6 -> disableContentTypeDetection = decodeBooleanElement(descriptor, 6)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(media)
            val text = caption?.let { Text(it, entities) }
            OutputMediaDocument(media, text, thumbnail, disableContentTypeDetection)
        }

        override fun serialize(encoder: Encoder, value: OutputMediaDocument) =
            encoder.encodeStructure(descriptor) {
                val (media, text, thumbnail, disableContentTypeDetection) = value
                encodeSerializableElement(descriptor, 0, OutputFile.serializer(), media)
                if (text != null) {
                    encodeStringElement(descriptor, 1, text.text)
                    if (text.entities.isNotEmpty()) encodeSerializableElement(
                        descriptor,
                        2,
                        ListSerializer(MessageEntity.serializer()),
                        text.entities
                    )
                }
                if (thumbnail != null) encodeSerializableElement(
                    descriptor,
                    3,
                    OutputFile.serializer(),
                    thumbnail
                )
                if (disableContentTypeDetection) encodeBooleanElement(
                    descriptor,
                    6,
                    disableContentTypeDetection
                )
            }
    }
}