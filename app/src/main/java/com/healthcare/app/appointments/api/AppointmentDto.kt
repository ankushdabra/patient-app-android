package com.healthcare.app.appointments.api

data class AppointmentDto(
    val id: String,
    val doctorId: String,
    val doctorName: String,
    val specialization: String,
    val appointmentDate: String,
    val appointmentTime: String,
    val status: String
)
