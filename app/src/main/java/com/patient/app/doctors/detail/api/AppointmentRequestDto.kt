package com.patient.app.doctors.detail.api

data class AppointmentRequestDto(
    val doctorId: String,
    val appointmentDate: String,
    val appointmentTime: String
)
