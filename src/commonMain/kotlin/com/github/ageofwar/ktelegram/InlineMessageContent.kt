package com.github.ageofwar.ktelegram

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
sealed class InlineMessageContent {
    object Serializer :
        JsonContentPolymorphicSerializer<InlineMessageContent>(InlineMessageContent::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out InlineMessageContent> {
            val json = element.jsonObject
            return when {
                "message_text" in json -> InlineTextContent.serializer()
                "latitude" in json -> if ("title" in json) InlineVenueContent.serializer() else InlineLocationContent.serializer()
                "phone_number" in json -> InlineContactContent.serializer()
                else -> throw SerializationException("Unknown InlineMessageContent")
            }
        }
    }
}

@Serializable(InlineTextContent.Serializer::class)
data class InlineTextContent(
    val text: Text,
    val disableWebPagePreview: Boolean = false
) : InlineMessageContent() {
    object Serializer : KSerializer<InlineTextContent> {
        override val descriptor = buildClassSerialDescriptor("InlineTextContent") {
            element<String>("message_text")
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
            InlineTextContent(Text(messageText, entities), disableWebPagePreview)
        }

        override fun serialize(encoder: Encoder, value: InlineTextContent) =
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
}

@Serializable(InlineLocationContent.Serializer::class)
data class InlineLocationContent(val location: Location) : InlineMessageContent() {
    object Serializer : KSerializer<InlineLocationContent> {
        override val descriptor = Location.serializer().descriptor

        override fun deserialize(decoder: Decoder) =
            InlineLocationContent(decoder.decodeSerializableValue(Location.serializer()))

        override fun serialize(encoder: Encoder, value: InlineLocationContent) =
            encoder.encodeSerializableValue(Location.serializer(), value.location)
    }
}

@Serializable(InlineVenueContent.Serializer::class)
data class InlineVenueContent(val venue: Venue) : InlineMessageContent() {
    object Serializer : KSerializer<InlineVenueContent> {
        override val descriptor = Venue.serializer().descriptor

        override fun deserialize(decoder: Decoder) =
            InlineVenueContent(decoder.decodeSerializableValue(Venue.serializer()))

        override fun serialize(encoder: Encoder, value: InlineVenueContent) =
            encoder.encodeSerializableValue(Venue.serializer(), value.venue)
    }
}

@Serializable
data class InlineContactContent(
    @SerialName("phone_number") val phoneNumber: String,
    @SerialName("first_name") override val firstName: String,
    @SerialName("last_name") override val lastName: String? = null,
    val vcard: String? = null
) : InlineMessageContent(), Name

@Serializable
data class InlineInvoiceContent(
    val title: String,
    val description: String,
    val payload: String,
    @SerialName("provider_token") val providerToken: String,
    val currency: String,
    val prices: List<LabeledPrice>,
    @SerialName("max_tip_amount") val maxTipAmount: Long? = null,
    @SerialName("suggested_tip_amounts") val suggestedTipAmounts: List<Long> = emptyList(),
    @SerialName("start_parameter") val startParameter: String? = null,
    @SerialName("provider_data") val providerData: String? = null,
    @SerialName("photo_url") val photoUrl: String? = null,
    @SerialName("photo_size") val photoSize: Int? = null,
    @SerialName("photo_width") val photoWidth: Int? = null,
    @SerialName("photo_height") val photoHeight: Int? = null,
    @SerialName("need_name") val needName: Boolean = false,
    @SerialName("need_phone_number") val needPhoneNumber: Boolean = false,
    @SerialName("need_email") val needEmail: Boolean = false,
    @SerialName("need_shipping_address") val needShippingAddress: Boolean = false,
    @SerialName("send_phone_number_to_provider") val sendPhoneNumberToProvider: Boolean = false,
    @SerialName("send_email_to_provider") val sendEmailToProvider: Boolean = false,
    @SerialName("is_flexible") val flexible: Boolean = false
) : InlineMessageContent()
