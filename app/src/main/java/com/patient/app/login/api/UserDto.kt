package com.patient.app.login.api

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val age: Int? = null,
    val gender: String? = null,
    val bloodGroup: String? = null,
    val weight: Double? = null,
    val height: Double? = null
)
