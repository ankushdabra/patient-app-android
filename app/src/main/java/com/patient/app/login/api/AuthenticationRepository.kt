package com.patient.app.login.api

import com.patient.app.doctors.detail.api.AppointmentResponseDto
import com.patient.app.core.network.ApiUrlMapper
import com.patient.app.core.network.NetworkModule
import com.patient.app.core.storage.TokenManager
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

    suspend fun getProfile(): Result<UserDto> {
        return try {
            val response = api.getProfile()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(request: ProfileUpdateRequestDto): Result<Unit> {
        return try {
            val response = api.updateProfile(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to update profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            val response = api.logout()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Logout failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
