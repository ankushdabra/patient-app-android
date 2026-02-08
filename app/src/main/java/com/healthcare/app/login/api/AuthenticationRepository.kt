package com.healthcare.app.login.api

import com.healthcare.app.doctors.detail.api.AppointmentResponseDto
import com.healthcare.app.core.network.ApiUrlMapper
import com.healthcare.app.core.network.NetworkModule
import com.healthcare.app.core.storage.TokenManager
import com.google.gson.Gson
import retrofit2.HttpException

class AuthenticationRepository(tokenManager: TokenManager) {
    private val api: ApiUrlMapper = NetworkModule.provideRetrofit(tokenManager)
        .create(ApiUrlMapper::class.java)

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val response = api.login(LoginRequestDto(email, password))
            if (response.isSuccessful) {
                Result.success(response.body()!!.token)
            } else {
                Result.failure(Exception("Invalid credentials"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: SignUpRequestDto): Result<SignUpResponseDto> {
        return try {
            val response = api.register(request)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Registration failed: ${response.code()}"))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                val errorResponse = Gson().fromJson(errorBody, AppointmentResponseDto::class.java)
                errorResponse.error ?: errorResponse.message ?: "Registration failed"
            } catch (jsonEx: Exception) {
                "Registration failed"
            }
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
