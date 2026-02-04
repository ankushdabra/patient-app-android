package com.healthcare.app.appointments.api

import retrofit2.http.GET
import retrofit2.http.Path

interface DoctorsApi {

    @GET("/api/doctors/{id}")
    suspend fun getDoctorDetail(
        @Path("id") doctorId: String
    ): DoctorDetailDto
}
