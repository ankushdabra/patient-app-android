package com.healthcare.app.core.network

import com.healthcare.app.appointments.api.AppointmentDto
import com.healthcare.app.doctors.detail.api.AppointmentRequest
import com.healthcare.app.doctors.detail.api.AppointmentResponse
import com.healthcare.app.doctors.detail.api.DoctorDetailDto
import com.healthcare.app.login.api.LoginRequest
import com.healthcare.app.login.api.LoginResponse
import com.healthcare.app.login.api.RegisterRequest
import com.healthcare.app.login.api.RegisterResponse
import com.healthcare.app.doctors.list.api.DoctorDto
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

    @GET("api/appointments")
    suspend fun getAppointments(): List<AppointmentDto>

}
