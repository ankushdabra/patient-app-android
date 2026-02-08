package com.healthcare.app.appointments.api

import com.healthcare.app.doctors.detail.api.DoctorDetailDto

data class AppointmentDto(
    val id: String,
    val doctor: DoctorDetailDto,
    val appointmentDate: String,
    val appointmentTime: String,
    val status: String
)
