package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Contact(
    @SerialName("phone_number") val phoneNumber: String,
    @SerialName("first_name") override val firstName: String,
    @SerialName("last_name") override val lastName: String? = null,
    @SerialName("user_id") val userId: Long? = null,
    val vcard: String? = null
) : Name
