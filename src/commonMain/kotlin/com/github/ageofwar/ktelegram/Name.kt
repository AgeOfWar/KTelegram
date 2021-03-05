package com.github.ageofwar.ktelegram

interface Name {
    val firstName: String
    val lastName: String?
    val name get() = if (lastName == null) firstName else "$firstName $lastName"
}