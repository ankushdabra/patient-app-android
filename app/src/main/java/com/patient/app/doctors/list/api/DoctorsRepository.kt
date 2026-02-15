package com.patient.app.doctors.list.api

import com.patient.app.core.network.ApiUrlMapper
import com.patient.app.core.network.NetworkModule
import com.patient.app.core.storage.TokenManager

class DoctorsRepository(tokenManager: TokenManager) {

    private val api = NetworkModule.provideRetrofit(tokenManager)
        .create(ApiUrlMapper::class.java)

    suspend fun getDoctors(page: Int, size: Int): Result<PagedResponse<DoctorDto>> {
        return try {
            Result.success(api.getDoctors(page, size))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
