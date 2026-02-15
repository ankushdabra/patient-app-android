package com.patient.app.login.api

data class SignUpResponseDto(
    val user: UserDto,
    val token: String
)
