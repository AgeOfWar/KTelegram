package com.github.ageofwar.ktelegram

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*

@Serializable
data class Game(
    override val title: String,
    val description: String,
    val photo: PhotoSize,
    override val text: Text? = null,
    val animation: Animation? = null
) : WithText, Title {
    object Serializer : KSerializer<Game> {
        override val descriptor = buildClassSerialDescriptor("game") {
            element<String>("title")
            element<String>("description")
            element<PhotoSize>("photo")
            element<String?>("text")
            element<List<MessageEntity>?>("text_entities")
            element<Animation?>("animation")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var title: String? = null
            var description: String? = null
            var photo: PhotoSize? = null
            var plainText: String? = null
            var entities: List<MessageEntity> = emptyList()
            var animation: Animation? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> title = decodeStringElement(descriptor, 0)
                    1 -> description = decodeStringElement(descriptor, 1)
                    2 -> photo =
                        decodeSerializableElement(descriptor, 2, PhotoSize.serializer(), photo)
                    3 -> plainText = decodeStringElement(descriptor, 3)
                    4 -> entities = decodeSerializableElement(
                        descriptor, 4, ListSerializer(MessageEntity.serializer()), entities
                    )
                    5 -> animation =
                        decodeSerializableElement(descriptor, 5, Animation.serializer(), animation)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(title)
            requireNotNull(description)
            requireNotNull(photo)
            val text = plainText?.let { Text(it, entities) }
            Game(title, description, photo, text, animation)
        }

        override fun serialize(encoder: Encoder, value: Game) {
            encoder.encodeStructure(descriptor) {
                val (title, description, photo, text, animation) = value
                encodeStringElement(descriptor, 0, title)
                encodeStringElement(descriptor, 1, description)
                encodeSerializableElement(descriptor, 2, PhotoSize.serializer(), photo)
                if (text != null) {
                    encodeStringElement(descriptor, 3, text.text)
                    if (text.entities.isNotEmpty()) encodeSerializableElement(
                        descriptor, 4, ListSerializer(MessageEntity.serializer()), text.entities
                    )
                }
                if (animation != null) encodeSerializableElement(
                    descriptor, 5, Animation.serializer(), animation
                )
            }
        }
    }
}

@Serializable
data class GameHighScore(
    val position: Int,
    val user: User,
    val score: Int
)
