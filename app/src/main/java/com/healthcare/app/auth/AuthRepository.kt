package com.healthcare.app.auth

import com.healthcare.app.core.network.NetworkModule
import com.healthcare.app.core.storage.TokenManager

class AuthRepository(
    tokenManager: TokenManager
) {

    private val api: AuthApi =
        NetworkModule
            .provideRetrofit(tokenManager)
            .create(AuthApi::class.java)

    suspend fun login(
        email: String,
        password: String
    ): Result<String> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                Result.success(response.body()!!.token)
            } else {
                Result.failure(Exception("Invalid credentials"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
