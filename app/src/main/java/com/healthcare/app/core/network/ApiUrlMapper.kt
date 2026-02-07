package com.healthcare.app.core.network

import com.healthcare.app.appointments.api.AppointmentRequest
import com.healthcare.app.appointments.api.AppointmentResponse
import com.healthcare.app.appointments.api.DoctorDetailDto
import com.healthcare.app.auth.api.LoginRequest
import com.healthcare.app.auth.api.LoginResponse
import com.healthcare.app.auth.api.RegisterRequest
import com.healthcare.app.auth.api.RegisterResponse
import com.healthcare.app.doctors.api.DoctorDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiUrlMapper {

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("/api/doctors")
    suspend fun getDoctors(): List<DoctorDto>

    @GET("/api/doctors/{id}")
    suspend fun getDoctorDetail(@Path("id") doctorId: String): DoctorDetailDto

    @POST("/api/appointments")
    suspend fun bookAppointment(@Body request: AppointmentRequest): AppointmentResponse
}
