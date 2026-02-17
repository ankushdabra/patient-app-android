package com.patient.app.core.network

import com.patient.app.appointments.api.AppointmentDto
import com.patient.app.doctors.detail.api.AppointmentRequestDto
import com.patient.app.doctors.detail.api.AppointmentResponseDto
import com.patient.app.doctors.detail.api.DoctorDetailDto
import com.patient.app.login.api.LoginRequestDto
import com.patient.app.login.api.LoginResponseDto
import com.patient.app.login.api.SignUpRequestDto
import com.patient.app.login.api.SignUpResponseDto
import com.patient.app.login.api.UserDto
import com.patient.app.doctors.list.api.DoctorDto
import com.patient.app.doctors.list.api.PagedResponse
import com.patient.app.prescriptions.api.PrescriptionDto
import retrofit2.Response
import retrofit2.http.*

interface ApiUrlMapper {

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>

    @POST("/api/auth/register")
    suspend fun register(@Body request: SignUpRequestDto): Response<SignUpResponseDto>

    @POST("/api/auth/logout")
    suspend fun logout(): Response<Unit>

    @GET("/api/profile")
    suspend fun getProfile(): UserDto

    @PUT("/api/profile")
    suspend fun updateProfile(@Body user: UserDto): UserDto

    @GET("/api/doctors")
    suspend fun getDoctors(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): PagedResponse<DoctorDto>

    @GET("/api/doctors/{id}")
    suspend fun getDoctorDetail(@Path("id") doctorId: String): DoctorDetailDto

    @POST("/api/appointments")
    suspend fun bookAppointment(@Body request: AppointmentRequestDto): AppointmentResponseDto

    @GET("api/appointments")
    suspend fun getAppointments(): List<AppointmentDto>

    @GET("/api/appointments/{id}")
    suspend fun getAppointmentDetail(@Path("id") appointmentId: String): AppointmentDto

    @GET("/api/prescriptions")
    suspend fun getPrescriptions(): List<PrescriptionDto>
}
