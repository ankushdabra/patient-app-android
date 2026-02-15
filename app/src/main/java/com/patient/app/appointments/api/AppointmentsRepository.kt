package com.patient.app.appointments.api

import com.patient.app.core.network.ApiUrlMapper
import com.patient.app.core.network.NetworkModule
import com.patient.app.core.storage.TokenManager

class AppointmentsRepository(tokenManager: TokenManager) {

    private val api = NetworkModule.provideRetrofit(tokenManager)
        .create(ApiUrlMapper::class.java)

    suspend fun getAppointments(): Result<List<AppointmentDto>> {
        return try {
            Result.success(api.getAppointments())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAppointmentDetail(id: String): Result<AppointmentDto> {
        return try {
            Result.success(api.getAppointmentDetail(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
