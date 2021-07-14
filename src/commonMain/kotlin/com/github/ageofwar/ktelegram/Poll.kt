package com.github.ageofwar.ktelegram

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*

@Serializable
sealed class Poll : Id<Long> {
    abstract val question: String
    abstract val options: List<PollOption>
    abstract val totalVoterCount: Int
    abstract val isClosed: Boolean
    abstract val isAnonymous: Boolean
    abstract val openPeriod: Int?
    abstract val closeDate: Int?

    @Serializable
    @SerialName("regular")
    data class Regular(
        override val id: Long,
        override val question: String,
        override val options: List<PollOption>,
        @SerialName("total_voter_count") override val totalVoterCount: Int,
        @SerialName("is_closed") override val isClosed: Boolean,
        @SerialName("is_anonymous") override val isAnonymous: Boolean,
        @SerialName("allows_multiple_answers") val allowsMultipleAnswers: Boolean,
        @SerialName("open_period") override val openPeriod: Int? = null,
        @SerialName("close_date") override val closeDate: Int? = null
    ) : Poll()

    @Serializable(Quiz.Serializer::class)
    @SerialName("quiz")
    data class Quiz(
        override val id: Long,
        override val question: String,
        override val options: List<PollOption>,
        override val totalVoterCount: Int,
        override val isClosed: Boolean,
        override val isAnonymous: Boolean,
        val correctOptionId: Int? = null,
        val explanation: Text? = null,
        override val openPeriod: Int? = null,
        override val closeDate: Int? = null
    ) : Poll() {
        object Serializer : KSerializer<Quiz> {
            override val descriptor = buildClassSerialDescriptor("quiz") {
                element<Long>("id")
                element<String>("question")
                element<List<PollOption>>("options")
                element<Int>("total_voter_count")
                element<Boolean>("is_closed")
                element<Boolean>("is_anonymous")
                element<Int?>("correct_option_id")
                element<String?>("explanation")
                element<List<MessageEntity>?>("explanation_entities")
                element<Int?>("open_period")
                element<Int?>("close_date")
            }

            override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
                var id: Long? = null
                var question: String? = null
                var options: List<PollOption>? = null
                var totalVoterCount: Int? = null
                var isClosed: Boolean? = null
                var isAnonymous: Boolean? = null
                var correctOptionId: Int? = null
                var explanation: String? = null
                var explanationEntities: List<MessageEntity>? = null
                var openPeriod: Int? = null
                var closeDate: Int? = null
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> id = decodeLongElement(descriptor, 0)
                        1 -> question = decodeStringElement(descriptor, 1)
                        2 -> options = decodeSerializableElement(
                            descriptor,
                            2,
                            ListSerializer(PollOption.serializer()),
                            options
                        )
                        3 -> totalVoterCount = decodeIntElement(descriptor, 3)
                        4 -> isClosed = decodeBooleanElement(descriptor, 4)
                        5 -> isAnonymous = decodeBooleanElement(descriptor, 5)
                        6 -> correctOptionId = decodeIntElement(descriptor, 6)
                        7 -> explanation = decodeStringElement(descriptor, 7)
                        8 -> explanationEntities = decodeSerializableElement(
                            descriptor,
                            8,
                            ListSerializer(MessageEntity.serializer()),
                            explanationEntities
                        )
                        9 -> openPeriod = decodeIntElement(descriptor, 9)
                        10 -> closeDate = decodeIntElement(descriptor, 10)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
                requireNotNull(id)
                requireNotNull(question)
                requireNotNull(options)
                requireNotNull(totalVoterCount)
                requireNotNull(isClosed)
                requireNotNull(isAnonymous)
                val text = explanation?.let { Text(it, explanationEntities ?: emptyList()) }
                Quiz(
                    id,
                    question,
                    options,
                    totalVoterCount,
                    isClosed,
                    isAnonymous,
                    correctOptionId,
                    text,
                    openPeriod,
                    closeDate
                )
            }

            override fun serialize(encoder: Encoder, value: Quiz) =
                encoder.encodeStructure(descriptor) {
                    encodeLongElement(descriptor, 0, value.id)
                    encodeStringElement(descriptor, 1, value.question)
                    encodeSerializableElement(
                        descriptor,
                        2,
                        ListSerializer(PollOption.serializer()),
                        value.options
                    )
                    encodeIntElement(descriptor, 3, value.totalVoterCount)
                    encodeBooleanElement(descriptor, 4, value.isClosed)
                    encodeBooleanElement(descriptor, 5, value.isAnonymous)
                    if (value.correctOptionId != null) encodeIntElement(descriptor, 6, value.correctOptionId)
                    if (value.explanation != null) encodeStringElement(
                        descriptor,
                        7,
                        value.explanation.toString()
                    )
                    if (value.explanation != null && value.explanation.entities.isNotEmpty()) encodeSerializableElement(
                        descriptor,
                        8,
                        ListSerializer(MessageEntity.serializer()),
                        value.explanation.entities
                    )
                    if (value.openPeriod != null) encodeIntElement(descriptor, 9, value.openPeriod)
                    if (value.closeDate != null) encodeIntElement(descriptor, 10, value.closeDate)
                }
        }
    }
}

@Serializable
data class PollAnswer(
    val pollId: String,
    val user: User,
    val optionIds: List<Int>
)

@Serializable
data class PollOption(
    val text: String,
    @SerialName("voter_count") val voterCount: Int
)
