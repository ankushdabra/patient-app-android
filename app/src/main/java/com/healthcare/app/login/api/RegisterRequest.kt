package com.healthcare.app.login.api

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val age: Int,
    val gender: String,
    val bloodGroup: String,
    val role: String = "PATIENT"
)
