package com.healthcare.app.doctors.detail.api

import com.healthcare.app.core.network.ApiUrlMapper
import com.healthcare.app.core.network.NetworkModule
import com.healthcare.app.core.storage.TokenManager

class BookAppointmentRepository(tokenManager: TokenManager) {
    private val api = NetworkModule.provideRetrofit(tokenManager)
        .create(ApiUrlMapper::class.java)

    suspend fun getDoctorDetail(doctorId: String): Result<DoctorDetailDto> {
        return try {
            Result.success(api.getDoctorDetail(doctorId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun bookAppointment(request: AppointmentRequestDto): Result<AppointmentResponseDto> {
        return try {
            Result.success(api.bookAppointment(request))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
