package com.healthcare.app.prescriptions.api

import com.healthcare.app.core.network.ApiUrlMapper
import com.healthcare.app.core.network.NetworkModule
import com.healthcare.app.core.storage.TokenManager

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
