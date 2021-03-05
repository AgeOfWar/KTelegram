package com.github.ageofwar.ktelegram

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*

@Serializable
sealed class InlineQueryResult : Id<String> {
    @Serializable
    @SerialName("article")
    data class Article(
        override val id: String,
        val title: String,
        @SerialName("input_message_content") val content: InlineMessageContent,
        @SerialName("reply_markup") val replyMarkup: InlineKeyboard? = null,
        val url: String? = null,
        @SerialName("hide_url") val hideUrl: Boolean = false,
        val description: String? = null,
        @SerialName("thumb_url") val thumbnailUrl: String? = null,
        @SerialName("thumb_width") val thumbnailWidth: Int? = null,
        @SerialName("thumb_height") val thumbnailHeight: Int? = null
    ) : InlineQueryResult()

    @Serializable(Photo.Serializer::class)
    @SerialName("photo")
    data class Photo(
        override val id: String,
        val photo: String,
        val thumbnailUrl: String? = null,
        val photoWidth: Int? = null,
        val photoHeight: Int? = null,
        val title: String? = null,
        val description: String? = null,
        val caption: Text? = null,
        val replyMarkup: InlineKeyboard? = null,
        val content: InlineMessageContent? = null
    ) : InlineQueryResult() {
        object Serializer : KSerializer<Photo> {
            override val descriptor = buildClassSerialDescriptor("photo") {
                element<String>("id")
                element<String?>("photo_file_id")
                element<String?>("photo_url")
                element<String?>("thumb_url")
                element<Int?>("photo_width")
                element<Int?>("photo_height")
                element<String?>("title")
                element<String?>("description")
                element<String?>("caption")
                element<List<MessageEntity>?>("caption_entities")
                element<InlineKeyboard?>("reply_markup")
                element<InlineMessageContent?>("input_message_content")
            }

            override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
                var id: String? = null
                var photo: String? = null
                var thumbnailUrl: String? = null
                var photoWidth: Int? = null
                var photoHeight: Int? = null
                var title: String? = null
                var description: String? = null
                var caption: String? = null
                var entities: List<MessageEntity> = emptyList()
                var replyMarkup: InlineKeyboard? = null
                var content: InlineMessageContent? = null
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> id = decodeStringElement(descriptor, 0)
                        1 -> photo = decodeStringElement(descriptor, 1)
                        2 -> photo = decodeStringElement(descriptor, 2)
                        3 -> thumbnailUrl = decodeStringElement(descriptor, 3)
                        4 -> photoWidth = decodeIntElement(descriptor, 4)
                        5 -> photoHeight = decodeIntElement(descriptor, 5)
                        6 -> title = decodeStringElement(descriptor, 6)
                        7 -> description = decodeStringElement(descriptor, 7)
                        8 -> caption = decodeStringElement(descriptor, 8)
                        9 -> entities = decodeSerializableElement(
                            descriptor,
                            9,
                            ListSerializer(MessageEntity.serializer()),
                            entities
                        )
                        10 -> replyMarkup = decodeSerializableElement(
                            descriptor,
                            10,
                            InlineKeyboard.serializer(),
                            replyMarkup
                        )
                        11 -> content = decodeSerializableElement(
                            descriptor,
                            11,
                            InlineMessageContent.serializer(),
                            content
                        )
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
                requireNotNull(id)
                requireNotNull(photo)
                requireNotNull(thumbnailUrl)
                val text = caption?.let { Text(it, entities) }
                Photo(
                    id,
                    photo,
                    thumbnailUrl,
                    photoWidth,
                    photoHeight,
                    title,
                    description,
                    text,
                    replyMarkup,
                    content
                )
            }

            override fun serialize(encoder: Encoder, value: Photo) =
                encoder.encodeStructure(descriptor) {
                    val (id, photo, thumbnailUrl, photoWidth, photoHeight, title, description, caption, replyMarkup, content) = value
                    encodeStringElement(descriptor, 0, id)
                    if (photo.matches(Regex("[A-Za-z0-9\\-_]+"))) {
                        encodeStringElement(descriptor, 1, photo)
                    } else {
                        encodeStringElement(descriptor, 2, photo)
                    }
                    if (thumbnailUrl != null) encodeStringElement(descriptor, 3, thumbnailUrl)
                    if (photoWidth != null) encodeIntElement(descriptor, 4, photoWidth)
                    if (photoHeight != null) encodeIntElement(descriptor, 5, photoHeight)
                    if (title != null) encodeStringElement(descriptor, 6, title)
                    if (description != null) encodeStringElement(descriptor, 7, description)
                    if (caption != null) {
                        encodeStringElement(descriptor, 8, caption.text)
                        if (caption.entities.isNotEmpty()) encodeSerializableElement(
                            descriptor,
                            9,
                            ListSerializer(MessageEntity.serializer()),
                            caption.entities
                        )
                    }
                    if (replyMarkup != null) encodeSerializableElement(
                        descriptor,
                        10,
                        InlineKeyboard.serializer(),
                        replyMarkup
                    )
                    if (content != null) encodeSerializableElement(
                        descriptor,
                        11,
                        InlineMessageContent.serializer(),
                        content
                    )
                }
        }
    }

    @Serializable(Gif.Serializer::class)
    @SerialName("gif")
    data class Gif(
        override val id: String,
        val gif: String,
        val gifWidth: Int? = null,
        val gifHeight: Int? = null,
        val gifDuration: Int? = null,
        val thumbnailUrl: String? = null,
        val thumbnailMimeType: String? = null,
        val title: String? = null,
        val caption: Text? = null,
        val replyMarkup: InlineKeyboard? = null,
        val content: InlineMessageContent? = null
    ) : InlineQueryResult() {
        object Serializer : KSerializer<Gif> {
            override val descriptor = buildClassSerialDescriptor("gif") {
                element<String>("id")
                element<String?>("gif_file_id")
                element<String?>("gif_url")
                element<Int?>("gif_width")
                element<Int?>("gif_height")
                element<Int?>("gif_duration")
                element<String>("thumb_url")
                element<String?>("thumb_mime_type")
                element<String?>("title")
                element<String?>("caption")
                element<List<MessageEntity>?>("caption_entities")
                element<InlineKeyboard?>("reply_markup")
                element<InlineMessageContent?>("input_message_content")
            }

            override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
                var id: String? = null
                var gif: String? = null
                var gifWidth: Int? = null
                var gifHeight: Int? = null
                var gifDuration: Int? = null
                var thumbnailUrl: String? = null
                var thumbnailMimeType: String? = null
                var title: String? = null
                var caption: String? = null
                var entities: List<MessageEntity> = emptyList()
                var replyMarkup: InlineKeyboard? = null
                var content: InlineMessageContent? = null
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> id = decodeStringElement(descriptor, 0)
                        1 -> gif = decodeStringElement(descriptor, 1)
                        2 -> gif = decodeStringElement(descriptor, 2)
                        3 -> gifWidth = decodeIntElement(descriptor, 3)
                        4 -> gifHeight = decodeIntElement(descriptor, 4)
                        5 -> gifDuration = decodeIntElement(descriptor, 5)
                        6 -> thumbnailUrl = decodeStringElement(descriptor, 6)
                        7 -> thumbnailMimeType = decodeStringElement(descriptor, 7)
                        8 -> title = decodeStringElement(descriptor, 8)
                        9 -> caption = decodeStringElement(descriptor, 9)
                        10 -> entities = decodeSerializableElement(
                            descriptor,
                            10,
                            ListSerializer(MessageEntity.serializer()),
                            entities
                        )
                        11 -> replyMarkup = decodeSerializableElement(
                            descriptor,
                            11,
                            InlineKeyboard.serializer(),
                            replyMarkup
                        )
                        12 -> content = decodeSerializableElement(
                            descriptor,
                            12,
                            InlineMessageContent.serializer(),
                            content
                        )
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
                requireNotNull(id)
                requireNotNull(gif)
                requireNotNull(thumbnailUrl)
                val text = caption?.let { Text(it, entities) }
                Gif(
                    id,
                    gif,
                    gifWidth,
                    gifHeight,
                    gifDuration,
                    thumbnailUrl,
                    thumbnailMimeType,
                    title,
                    text,
                    replyMarkup,
                    content
                )
            }

            override fun serialize(encoder: Encoder, value: Gif) =
                encoder.encodeStructure(descriptor) {
                    val (id, gif, gifWidth, gifHeight, gifDuration, thumbnailUrl, thumbnailMimeType, title, caption, replyMarkup, content) = value
                    encodeStringElement(descriptor, 0, id)
                    if (gif.matches(Regex("[A-Za-z0-9\\-_]+"))) {
                        encodeStringElement(descriptor, 1, gif)
                    } else {
                        encodeStringElement(descriptor, 2, gif)
                    }
                    if (gifWidth != null) encodeIntElement(descriptor, 2, gifWidth)
                    if (gifHeight != null) encodeIntElement(descriptor, 3, gifHeight)
                    if (gifDuration != null) encodeIntElement(descriptor, 4, gifDuration)
                    if (thumbnailUrl != null) encodeStringElement(descriptor, 5, thumbnailUrl)
                    if (thumbnailMimeType != null) encodeStringElement(
                        descriptor,
                        6,
                        thumbnailMimeType
                    )
                    if (title != null) encodeStringElement(descriptor, 7, title)
                    if (caption != null) {
                        encodeStringElement(descriptor, 8, caption.text)
                        if (caption.entities.isNotEmpty()) encodeSerializableElement(
                            descriptor,
                            9,
                            ListSerializer(MessageEntity.serializer()),
                            caption.entities
                        )
                    }
                    if (replyMarkup != null) encodeSerializableElement(
                        descriptor,
                        10,
                        InlineKeyboard.serializer(),
                        replyMarkup
                    )
                    if (content != null) encodeSerializableElement(
                        descriptor,
                        11,
                        InlineMessageContent.serializer(),
                        content
                    )
                }
        }
    }

    @Serializable(Mpeg4Gif.Serializer::class)
    @SerialName("mpeg4_gif")
    data class Mpeg4Gif(
        override val id: String,
        val mpeg4Gif: String,
        val mpeg4Width: Int? = null,
        val mpeg4Height: Int? = null,
        val mpeg4Duration: Int? = null,
        val thumbnailUrl: String? = null,
        val thumbnailMimeType: String? = null,
        val title: String? = null,
        val caption: Text? = null,
        val replyMarkup: InlineKeyboard? = null,
        val content: InlineMessageContent? = null
    ) : InlineQueryResult() {
        object Serializer : KSerializer<Mpeg4Gif> {
            override val descriptor = buildClassSerialDescriptor("mpeg4_gif") {
                element<String>("id")
                element<String?>("mpeg4_file_id")
                element<String?>("mpeg4_url")
                element<Int?>("mpeg4_width")
                element<Int?>("mpeg4_height")
                element<Int?>("mpeg4_duration")
                element<String>("thumb_url")
                element<String?>("thumb_mime_type")
                element<String?>("title")
                element<String?>("caption")
                element<List<MessageEntity>?>("caption_entities")
                element<InlineKeyboard?>("reply_markup")
                element<InlineMessageContent?>("input_message_content")
            }

            override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
                var id: String? = null
                var mpeg4Gif: String? = null
                var mpeg4Width: Int? = null
                var mpeg4Height: Int? = null
                var mpeg4Duration: Int? = null
                var thumbnailUrl: String? = null
                var thumbnailMimeType: String? = null
                var title: String? = null
                var caption: String? = null
                var entities: List<MessageEntity> = emptyList()
                var replyMarkup: InlineKeyboard? = null
                var content: InlineMessageContent? = null
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> id = decodeStringElement(descriptor, 0)
                        1 -> mpeg4Gif = decodeStringElement(descriptor, 1)
                        2 -> mpeg4Gif = decodeStringElement(descriptor, 2)
                        3 -> mpeg4Width = decodeIntElement(descriptor, 3)
                        4 -> mpeg4Height = decodeIntElement(descriptor, 4)
                        5 -> mpeg4Duration = decodeIntElement(descriptor, 5)
                        6 -> thumbnailUrl = decodeStringElement(descriptor, 6)
                        7 -> thumbnailMimeType = decodeStringElement(descriptor, 7)
                        8 -> title = decodeStringElement(descriptor, 8)
                        9 -> caption = decodeStringElement(descriptor, 9)
                        10 -> entities = decodeSerializableElement(
                            descriptor,
                            10,
                            ListSerializer(MessageEntity.serializer()),
                            entities
                        )
                        11 -> replyMarkup = decodeSerializableElement(
                            descriptor,
                            11,
                            InlineKeyboard.serializer(),
                            replyMarkup
                        )
                        12 -> content = decodeSerializableElement(
                            descriptor,
                            12,
                            InlineMessageContent.serializer(),
                            content
                        )
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
                requireNotNull(id)
                requireNotNull(mpeg4Gif)
                requireNotNull(thumbnailUrl)
                val text = caption?.let { Text(it, entities) }
                Mpeg4Gif(
                    id,
                    mpeg4Gif,
                    mpeg4Width,
                    mpeg4Height,
                    mpeg4Duration,
                    thumbnailUrl,
                    thumbnailMimeType,
                    title,
                    text,
                    replyMarkup,
                    content
                )
            }

            override fun serialize(encoder: Encoder, value: Mpeg4Gif) =
                encoder.encodeStructure(descriptor) {
                    val (id, mpeg4Gif, mpeg4Width, mpeg4Height, mpeg4Duration, thumbnailUrl, thumbnailMimeType, title, caption, replyMarkup, content) = value
                    encodeStringElement(descriptor, 0, id)
                    if (mpeg4Gif.matches(Regex("[A-Za-z0-9\\-_]+"))) {
                        encodeStringElement(descriptor, 1, mpeg4Gif)
                    } else {
                        encodeStringElement(descriptor, 2, mpeg4Gif)
                    }
                    if (mpeg4Width != null) encodeIntElement(descriptor, 3, mpeg4Width)
                    if (mpeg4Height != null) encodeIntElement(descriptor, 4, mpeg4Height)
                    if (mpeg4Duration != null) encodeIntElement(descriptor, 5, mpeg4Duration)
                    if (thumbnailUrl != null) encodeStringElement(descriptor, 6, thumbnailUrl)
                    if (thumbnailMimeType != null) encodeStringElement(
                        descriptor,
                        7,
                        thumbnailMimeType
                    )
                    if (title != null) encodeStringElement(descriptor, 8, title)
                    if (caption != null) {
                        encodeStringElement(descriptor, 9, caption.text)
                        if (caption.entities.isNotEmpty()) encodeSerializableElement(
                            descriptor,
                            10,
                            ListSerializer(MessageEntity.serializer()),
                            caption.entities
                        )
                    }
                    if (replyMarkup != null) encodeSerializableElement(
                        descriptor,
                        11,
                        InlineKeyboard.serializer(),
                        replyMarkup
                    )
                    if (content != null) encodeSerializableElement(
                        descriptor,
                        12,
                        InlineMessageContent.serializer(),
                        content
                    )
                }
        }
    }

    @Serializable(Video.Serializer::class)
    @SerialName("video")
    data class Video(
        override val id: String,
        val video: String,
        val videoWidth: Int? = null,
        val videoHeight: Int? = null,
        val videoDuration: Int? = null,
        val thumbnailUrl: String? = null,
        val mimeType: String? = null,
        val title: String,
        val description: String? = null,
        val caption: Text? = null,
        val replyMarkup: InlineKeyboard? = null,
        val content: InlineMessageContent? = null
    ) : InlineQueryResult() {
        object Serializer : KSerializer<Video> {
            override val descriptor = buildClassSerialDescriptor("video") {
                element<String>("id")
                element<String?>("video_file_id")
                element<String?>("video_url")
                element<Int?>("video_width")
                element<Int?>("video_height")
                element<Int?>("video_duration")
                element<String?>("thumb_url")
                element<String?>("mime_type")
                element<String>("title")
                element<String?>("description")
                element<String?>("caption")
                element<List<MessageEntity>?>("caption_entities")
                element<InlineKeyboard?>("reply_markup")
                element<InlineMessageContent?>("input_message_content")
            }

            override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
                var id: String? = null
                var video: String? = null
                var videoWidth: Int? = null
                var videoHeight: Int? = null
                var videoDuration: Int? = null
                var thumbnailUrl: String? = null
                var mimeType: String? = null
                var title: String? = null
                var description: String? = null
                var caption: String? = null
                var entities: List<MessageEntity> = emptyList()
                var replyMarkup: InlineKeyboard? = null
                var content: InlineMessageContent? = null
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> id = decodeStringElement(descriptor, 0)
                        1 -> video = decodeStringElement(descriptor, 1)
                        2 -> video = decodeStringElement(descriptor, 2)
                        3 -> videoWidth = decodeIntElement(descriptor, 3)
                        4 -> videoHeight = decodeIntElement(descriptor, 4)
                        5 -> videoDuration = decodeIntElement(descriptor, 5)
                        6 -> thumbnailUrl = decodeStringElement(descriptor, 6)
                        7 -> mimeType = decodeStringElement(descriptor, 7)
                        8 -> title = decodeStringElement(descriptor, 8)
                        9 -> description = decodeStringElement(descriptor, 9)
                        10 -> caption = decodeStringElement(descriptor, 10)
                        11 -> entities = decodeSerializableElement(
                            descriptor,
                            11,
                            ListSerializer(MessageEntity.serializer()),
                            entities
                        )
                        12 -> replyMarkup = decodeSerializableElement(
                            descriptor,
                            12,
                            InlineKeyboard.serializer(),
                            replyMarkup
                        )
                        13 -> content = decodeSerializableElement(
                            descriptor,
                            13,
                            InlineMessageContent.serializer(),
                            content
                        )
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
                requireNotNull(id)
                requireNotNull(video)
                requireNotNull(mimeType)
                requireNotNull(title)
                requireNotNull(thumbnailUrl)
                val text = caption?.let { Text(it, entities) }
                Video(
                    id,
                    video,
                    videoWidth,
                    videoHeight,
                    videoDuration,
                    thumbnailUrl,
                    mimeType,
                    title,
                    description,
                    text,
                    replyMarkup,
                    content
                )
            }

            override fun serialize(encoder: Encoder, value: Video) =
                encoder.encodeStructure(descriptor) {
                    val (id, video, videoWidth, videoHeight, videoDuration, thumbnailUrl, mimeType, title, description, caption, replyMarkup, content) = value
                    encodeStringElement(descriptor, 0, id)
                    if (video.matches(Regex("[A-Za-z0-9\\-_]+"))) {
                        encodeStringElement(descriptor, 1, video)
                    } else {
                        encodeStringElement(descriptor, 2, video)
                    }
                    if (videoWidth != null) encodeIntElement(descriptor, 3, videoWidth)
                    if (videoHeight != null) encodeIntElement(descriptor, 4, videoHeight)
                    if (videoDuration != null) encodeIntElement(descriptor, 5, videoDuration)
                    if (thumbnailUrl != null) encodeStringElement(descriptor, 6, thumbnailUrl)
                    if (mimeType != null) encodeStringElement(descriptor, 7, mimeType)
                    encodeStringElement(descriptor, 8, title)
                    if (description != null) encodeStringElement(descriptor, 9, description)
                    if (caption != null) {
                        encodeStringElement(descriptor, 10, caption.text)
                        if (caption.entities.isNotEmpty()) encodeSerializableElement(
                            descriptor,
                            11,
                            ListSerializer(MessageEntity.serializer()),
                            caption.entities
                        )
                    }
                    if (replyMarkup != null) encodeSerializableElement(
                        descriptor,
                        12,
                        InlineKeyboard.serializer(),
                        replyMarkup
                    )
                    if (content != null) encodeSerializableElement(
                        descriptor,
                        13,
                        InlineMessageContent.serializer(),
                        content
                    )
                }
        }
    }

    @Serializable(Audio.Serializer::class)
    @SerialName("audio")
    data class Audio(
        override val id: String,
        val audio: String,
        val title: String,
        val caption: Text? = null,
        val performer: String? = null,
        val audioDuration: Int? = null,
        val replyMarkup: InlineKeyboard? = null,
        val content: InlineMessageContent? = null
    ) : InlineQueryResult() {
        object Serializer : KSerializer<Audio> {
            override val descriptor = buildClassSerialDescriptor("audio") {
                element<String>("id")
                element<String>("audio_file_id")
                element<String>("audio_url")
                element<String>("title")
                element<String?>("caption")
                element<List<MessageEntity>?>("caption_entities")
                element<String?>("performer")
                element<Int?>("audio_duration")
                element<InlineKeyboard?>("reply_markup")
                element<InlineMessageContent?>("input_message_content")
            }

            override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
                var id: String? = null
                var audio: String? = null
                var title: String? = null
                var caption: String? = null
                var entities: List<MessageEntity> = emptyList()
                var performer: String? = null
                var audioDuration: Int? = null
                var replyMarkup: InlineKeyboard? = null
                var content: InlineMessageContent? = null
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> id = decodeStringElement(descriptor, 0)
                        1 -> audio = decodeStringElement(descriptor, 1)
                        2 -> audio = decodeStringElement(descriptor, 2)
                        3 -> title = decodeStringElement(descriptor, 3)
                        4 -> caption = decodeStringElement(descriptor, 4)
                        5 -> entities = decodeSerializableElement(
                            descriptor,
                            5,
                            ListSerializer(MessageEntity.serializer()),
                            entities
                        )
                        6 -> performer = decodeStringElement(descriptor, 6)
                        7 -> audioDuration = decodeIntElement(descriptor, 7)
                        8 -> replyMarkup = decodeSerializableElement(
                            descriptor,
                            8,
                            InlineKeyboard.serializer(),
                            replyMarkup
                        )
                        9 -> content = decodeSerializableElement(
                            descriptor,
                            9,
                            InlineMessageContent.serializer(),
                            content
                        )
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
                requireNotNull(id)
                requireNotNull(audio)
                requireNotNull(title)
                val text = caption?.let { Text(it, entities) }
                Audio(id, audio, title, text, performer, audioDuration, replyMarkup, content)
            }

            override fun serialize(encoder: Encoder, value: Audio) =
                encoder.encodeStructure(descriptor) {
                    val (id, audio, title, caption, performer, audioDuration, replyMarkup, content) = value
                    encodeStringElement(descriptor, 0, id)
                    if (audio.matches(Regex("[A-Za-z0-9\\-_]+"))) {
                        encodeStringElement(descriptor, 1, audio)
                    } else {
                        encodeStringElement(descriptor, 2, audio)
                    }
                    encodeStringElement(descriptor, 2, title)
                    if (caption != null) {
                        encodeStringElement(descriptor, 3, caption.text)
                        if (caption.entities.isNotEmpty()) encodeSerializableElement(
                            descriptor,
                            4,
                            ListSerializer(MessageEntity.serializer()),
                            caption.entities
                        )
                    }
                    if (performer != null) encodeStringElement(descriptor, 5, performer)
                    if (audioDuration != null) encodeIntElement(descriptor, 6, audioDuration)
                    if (replyMarkup != null) encodeSerializableElement(
                        descriptor,
                        7,
                        InlineKeyboard.serializer(),
                        replyMarkup
                    )
                    if (content != null) encodeSerializableElement(
                        descriptor,
                        8,
                        InlineMessageContent.serializer(),
                        content
                    )
                }
        }
    }

    @Serializable(Voice.Serializer::class)
    @SerialName("voice")
    data class Voice(
        override val id: String,
        val voice: String,
        val title: String,
        val caption: Text? = null,
        val audioDuration: Int? = null,
        val replyMarkup: InlineKeyboard? = null,
        val content: InlineMessageContent? = null
    ) : InlineQueryResult() {
        object Serializer : KSerializer<Voice> {
            override val descriptor = buildClassSerialDescriptor("voice") {
                element<String>("id")
                element<String>("voice_file_id")
                element<String>("voice_url")
                element<String>("title")
                element<String?>("caption")
                element<List<MessageEntity>?>("caption_entities")
                element<Int?>("audio_duration")
                element<InlineKeyboard?>("reply_markup")
                element<InlineMessageContent?>("input_message_content")
            }

            override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
                var id: String? = null
                var voice: String? = null
                var title: String? = null
                var caption: String? = null
                var entities: List<MessageEntity> = emptyList()
                var voiceDuration: Int? = null
                var replyMarkup: InlineKeyboard? = null
                var content: InlineMessageContent? = null
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> id = decodeStringElement(descriptor, 0)
                        1 -> voice = decodeStringElement(descriptor, 1)
                        2 -> voice = decodeStringElement(descriptor, 2)
                        3 -> title = decodeStringElement(descriptor, 3)
                        4 -> caption = decodeStringElement(descriptor, 4)
                        5 -> entities = decodeSerializableElement(
                            descriptor,
                            5,
                            ListSerializer(MessageEntity.serializer()),
                            entities
                        )
                        6 -> voiceDuration = decodeIntElement(descriptor, 6)
                        7 -> replyMarkup = decodeSerializableElement(
                            descriptor,
                            7,
                            InlineKeyboard.serializer(),
                            replyMarkup
                        )
                        8 -> content = decodeSerializableElement(
                            descriptor,
                            8,
                            InlineMessageContent.serializer(),
                            content
                        )
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
                requireNotNull(id)
                requireNotNull(voice)
                requireNotNull(title)
                val text = caption?.let { Text(it, entities) }
                Voice(id, voice, title, text, voiceDuration, replyMarkup, content)
            }

            override fun serialize(encoder: Encoder, value: Voice) =
                encoder.encodeStructure(descriptor) {
                    val (id, voice, title, caption, voiceDuration, replyMarkup, content) = value
                    encodeStringElement(descriptor, 0, id)
                    if (voice.matches(Regex("[A-Za-z0-9\\-_]+"))) {
                        encodeStringElement(descriptor, 1, voice)
                    } else {
                        encodeStringElement(descriptor, 2, voice)
                    }
                    encodeStringElement(descriptor, 3, title)
                    if (caption != null) {
                        encodeStringElement(descriptor, 4, caption.text)
                        if (caption.entities.isNotEmpty()) encodeSerializableElement(
                            descriptor,
                            5,
                            ListSerializer(MessageEntity.serializer()),
                            caption.entities
                        )
                    }
                    if (voiceDuration != null) encodeIntElement(descriptor, 6, voiceDuration)
                    if (replyMarkup != null) encodeSerializableElement(
                        descriptor,
                        7,
                        InlineKeyboard.serializer(),
                        replyMarkup
                    )
                    if (content != null) encodeSerializableElement(
                        descriptor,
                        8,
                        InlineMessageContent.serializer(),
                        content
                    )
                }
        }
    }

    @Serializable(Document.Serializer::class)
    @SerialName("document")
    data class Document(
        override val id: String,
        val title: String,
        val caption: Text? = null,
        val document: String,
        val mimeType: String? = null,
        val description: String? = null,
        val replyMarkup: InlineKeyboard? = null,
        val content: InlineMessageContent? = null,
        val thumbnailUrl: String? = null,
        val thumbnailWidth: Int? = null,
        val thumbnailHeight: Int? = null
    ) : InlineQueryResult() {
        object Serializer : KSerializer<Document> {
            override val descriptor = buildClassSerialDescriptor("document") {
                element<String>("id")
                element<String>("title")
                element<String?>("caption")
                element<List<MessageEntity>?>("caption_entities")
                element<String?>("document_file_id")
                element<String?>("document_url")
                element<String?>("mime_type")
                element<String?>("description")
                element<InlineKeyboard?>("reply_markup")
                element<InlineMessageContent?>("input_message_content")
                element<String?>("thumb_url")
                element<Int?>("thumb_width")
                element<Int?>("thumb_height")
            }

            override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
                var id: String? = null
                var title: String? = null
                var caption: String? = null
                var entities: List<MessageEntity> = emptyList()
                var document: String? = null
                var mimeType: String? = null
                var description: String? = null
                var replyMarkup: InlineKeyboard? = null
                var content: InlineMessageContent? = null
                var thumbnailUrl: String? = null
                var thumbnailWidth: Int? = null
                var thumbnailHeight: Int? = null
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> id = decodeStringElement(descriptor, 0)
                        1 -> title = decodeStringElement(descriptor, 1)
                        2 -> caption = decodeStringElement(descriptor, 2)
                        3 -> entities = decodeSerializableElement(
                            descriptor,
                            3,
                            ListSerializer(MessageEntity.serializer()),
                            entities
                        )
                        4 -> document = decodeStringElement(descriptor, 4)
                        5 -> document = decodeStringElement(descriptor, 5)
                        6 -> mimeType = decodeStringElement(descriptor, 6)
                        7 -> description = decodeStringElement(descriptor, 7)
                        8 -> replyMarkup = decodeSerializableElement(
                            descriptor,
                            8,
                            InlineKeyboard.serializer(),
                            replyMarkup
                        )
                        9 -> content = decodeSerializableElement(
                            descriptor,
                            9,
                            InlineMessageContent.serializer(),
                            content
                        )
                        10 -> thumbnailUrl = decodeStringElement(descriptor, 10)
                        11 -> thumbnailWidth = decodeIntElement(descriptor, 11)
                        12 -> thumbnailHeight = decodeIntElement(descriptor, 12)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
                requireNotNull(id)
                requireNotNull(document)
                requireNotNull(title)
                requireNotNull(mimeType)
                val text = caption?.let { Text(it, entities) }
                Document(
                    id,
                    title,
                    text,
                    document,
                    mimeType,
                    description,
                    replyMarkup,
                    content,
                    thumbnailUrl,
                    thumbnailWidth,
                    thumbnailHeight
                )
            }

            override fun serialize(encoder: Encoder, value: Document) =
                encoder.encodeStructure(descriptor) {
                    val (id, title, caption, document, mimeType, description, replyMarkup, content, thumbnailUrl, thumbnailWidth, thumbnailHeight) = value
                    encodeStringElement(descriptor, 0, id)
                    encodeStringElement(descriptor, 1, title)
                    if (caption != null) {
                        encodeStringElement(descriptor, 2, caption.text)
                        if (caption.entities.isNotEmpty()) encodeSerializableElement(
                            descriptor,
                            3,
                            ListSerializer(MessageEntity.serializer()),
                            caption.entities
                        )
                    }
                    if (document.matches(Regex("[A-Za-z0-9\\-_]+"))) {
                        encodeStringElement(descriptor, 4, document)
                    } else {
                        encodeStringElement(descriptor, 5, document)
                    }
                    if (mimeType != null) encodeStringElement(descriptor, 6, mimeType)
                    if (description != null) encodeStringElement(descriptor, 7, description)
                    if (replyMarkup != null) encodeSerializableElement(
                        descriptor,
                        8,
                        InlineKeyboard.serializer(),
                        replyMarkup
                    )
                    if (content != null) encodeSerializableElement(
                        descriptor,
                        9,
                        InlineMessageContent.serializer(),
                        content
                    )
                    if (thumbnailUrl != null) encodeStringElement(descriptor, 10, thumbnailUrl)
                    if (thumbnailWidth != null) encodeIntElement(descriptor, 11, thumbnailWidth)
                    if (thumbnailHeight != null) encodeIntElement(descriptor, 12, thumbnailHeight)
                }
        }
    }

    @Serializable
    @SerialName("location")
    data class Location(
        override val id: String,
        val latitude: Double,
        val longitude: Double,
        val title: String,
        @SerialName("horizontal_accuracy") val horizontalAccuracy: Float? = null,
        @SerialName("live_period") val livePeriod: Int? = null,
        val heading: Int? = null,
        @SerialName("proximity_alert_radius") val proximityAlertRadius: Int? = null,
        @SerialName("reply_markup") val replyMarkup: InlineKeyboard? = null,
        @SerialName("input_message_content") val content: InlineMessageContent,
        @SerialName("thumb_url") val thumbnailUrl: String? = null,
        @SerialName("thumb_width") val thumbnailWidth: Int? = null,
        @SerialName("thumb_height") val thumbnailHeight: Int? = null
    ) : InlineQueryResult()

    @Serializable
    @SerialName("venue")
    data class Venue(
        override val id: String,
        val latitude: Double,
        val longitude: Double,
        val title: String,
        val address: String,
        @SerialName("foursquare_id") val foursquareId: String? = null,
        @SerialName("foursquare_type") val foursquareType: String? = null,
        @SerialName("google_place_id") val googlePlaceId: String? = null,
        @SerialName("google_place_type") val googlePlaceType: String? = null,
        @SerialName("reply_markup") val replyMarkup: InlineKeyboard? = null,
        @SerialName("input_message_content") val content: InlineMessageContent,
        @SerialName("thumb_url") val thumbnailUrl: String? = null,
        @SerialName("thumb_width") val thumbnailWidth: Int? = null,
        @SerialName("thumb_height") val thumbnailHeight: Int? = null
    ) : InlineQueryResult()

    @Serializable
    @SerialName("contact")
    data class Contact(
        override val id: String,
        @SerialName("phone_number") val phoneNumber: String,
        @SerialName("first_name") val firstName: String,
        @SerialName("last_name") val lastName: String? = null,
        val vcard: String? = null,
        @SerialName("reply_markup") val replyMarkup: InlineKeyboard? = null,
        @SerialName("input_message_content") val content: InlineMessageContent,
        @SerialName("thumb_url") val thumbnailUrl: String? = null,
        @SerialName("thumb_width") val thumbnailWidth: Int? = null,
        @SerialName("thumb_height") val thumbnailHeight: Int? = null
    ) : InlineQueryResult()

    @Serializable
    @SerialName("game")
    data class Game(
        override val id: String,
        @SerialName("game_short_name") val gameShortName: String,
        @SerialName("reply_markup") val replyMarkup: InlineKeyboard? = null
    ) : InlineQueryResult()

    @Serializable
    @SerialName("sticker")
    data class Sticker(
        override val id: String,
        @SerialName("sticker_file_id") val sticker: String,
        @SerialName("reply_markup") val replyMarkup: InlineKeyboard? = null,
        @SerialName("input_message_content") val content: InlineMessageContent? = null
    ) : InlineQueryResult()
}
