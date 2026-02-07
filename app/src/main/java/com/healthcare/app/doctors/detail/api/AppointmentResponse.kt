package com.healthcare.app.doctors.detail.api

data class AppointmentResponse(
    val status: Int,
    val message: String? = null,
    val error: String? = null
)
