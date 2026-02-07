package com.healthcare.app.appointments.api

data class AppointmentRequest(
    val doctorId: String,
    val appointmentDate: String,
    val appointmentTime: String
)
