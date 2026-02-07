package com.healthcare.app.doctors.api

import com.healthcare.app.core.network.ApiUrlMapper
import com.healthcare.app.core.network.NetworkModule
import com.healthcare.app.core.storage.TokenManager

class DoctorsRepository(tokenManager: TokenManager) {

    private val api = NetworkModule.provideRetrofit(tokenManager)
        .create(ApiUrlMapper::class.java)

    suspend fun getDoctors(): Result<List<DoctorDto>> {
        return try {
            Result.success(api.getDoctors())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
