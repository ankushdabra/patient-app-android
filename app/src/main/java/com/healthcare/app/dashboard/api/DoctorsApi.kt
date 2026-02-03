package com.healthcare.app.dashboard.api

import retrofit2.http.GET

interface DoctorsApi {

    @GET("/api/doctors")
    suspend fun getDoctors(): List<DoctorDto>
}