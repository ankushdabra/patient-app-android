package com.healthcare.app.doctors.detail.api

data class AppointmentRequest(
    val doctorId: String,
    val appointmentDate: String,
    val appointmentTime: String
)
