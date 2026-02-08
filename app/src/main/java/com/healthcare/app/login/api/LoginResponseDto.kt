package com.healthcare.app.login.api

import com.google.gson.annotations.SerializedName

data class LoginResponseDto(
    val status: Int,
    @SerializedName("message")
    val token: String
)
