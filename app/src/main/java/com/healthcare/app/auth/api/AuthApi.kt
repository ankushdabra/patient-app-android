package com.healthcare.app.auth.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>
}
