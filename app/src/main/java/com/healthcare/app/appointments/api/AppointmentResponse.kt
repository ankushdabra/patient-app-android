package com.healthcare.app.appointments.api

data class AppointmentResponse(
    val status: Int,
    val message: String? = null,
    val error: String? = null
)
