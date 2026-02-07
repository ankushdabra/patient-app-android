package com.healthcare.app.auth.api

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val status: Int,
    @SerializedName("message")
    val token: String
)
