package com.healthcare.app.appointments.api

import com.healthcare.app.core.network.NetworkModule
import com.healthcare.app.core.storage.TokenManager

class DoctorDetailRepository(
    tokenManager: TokenManager
) {
    private val api = NetworkModule
        .provideRetrofit(tokenManager)
        .create(DoctorsApi::class.java)

    suspend fun getDoctorDetail(
        doctorId: String
    ): Result<DoctorDetailDto> {
        return try {
            Result.success(api.getDoctorDetail(doctorId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
