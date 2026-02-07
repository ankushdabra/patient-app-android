package com.healthcare.app.auth.api

data class RegisterResponse(
    val user: UserDto,
    val token: String
)

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val role: String
)
