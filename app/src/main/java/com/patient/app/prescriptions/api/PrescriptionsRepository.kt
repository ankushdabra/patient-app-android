package com.patient.app.prescriptions.api

import com.patient.app.core.network.ApiUrlMapper
import com.patient.app.core.network.NetworkModule
import com.patient.app.core.storage.TokenManager

class PrescriptionsRepository(tokenManager: TokenManager) {
    private val api: ApiUrlMapper = NetworkModule.provideRetrofit(tokenManager)
        .create(ApiUrlMapper::class.java)

    suspend fun getPrescriptions(): Result<List<PrescriptionDto>> {
        return try {
            val response = api.getPrescriptions()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
