package com.healthcare.app.core.network

import com.healthcare.app.appointments.api.AppointmentDto
import com.healthcare.app.doctors.detail.api.AppointmentRequestDto
import com.healthcare.app.doctors.detail.api.AppointmentResponseDto
import com.healthcare.app.doctors.detail.api.DoctorDetailDto
import com.healthcare.app.login.api.LoginRequestDto
import com.healthcare.app.login.api.LoginResponseDto
import com.healthcare.app.login.api.SignUpRequestDto
import com.healthcare.app.login.api.SignUpResponseDto
import com.healthcare.app.doctors.list.api.DoctorDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiUrlMapper {

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>

    @POST("/api/auth/register")
    suspend fun register(@Body request: SignUpRequestDto): Response<SignUpResponseDto>

    @GET("/api/doctors")
    suspend fun getDoctors(): List<DoctorDto>

    @GET("/api/doctors/{id}")
    suspend fun getDoctorDetail(@Path("id") doctorId: String): DoctorDetailDto

    @POST("/api/appointments")
    suspend fun bookAppointment(@Body request: AppointmentRequestDto): AppointmentResponseDto

    @GET("api/appointments")
    suspend fun getAppointments(): List<AppointmentDto>

    @GET("/api/appointments/{id}")
    suspend fun getAppointmentDetail(@Path("id") appointmentId: String): AppointmentDto
}
