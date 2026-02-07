package com.healthcare.app.appointments.api

import com.healthcare.app.core.network.ApiUrlMapper
import com.healthcare.app.core.network.NetworkModule
import com.healthcare.app.core.storage.TokenManager

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
}
