package com.healthcare.app.doctors.detail.api

data class AppointmentRequestDto(
    val doctorId: String,
    val appointmentDate: String,
    val appointmentTime: String
)
