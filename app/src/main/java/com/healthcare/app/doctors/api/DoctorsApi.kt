package com.healthcare.app.doctors.api

import retrofit2.http.GET

interface DoctorsApi {

    @GET("/api/doctors")
    suspend fun getDoctors(): List<DoctorDto>
}