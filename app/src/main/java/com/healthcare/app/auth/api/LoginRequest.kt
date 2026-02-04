package com.healthcare.app.auth.api

data class LoginRequest(
    val email: String,
    val password: String
)
