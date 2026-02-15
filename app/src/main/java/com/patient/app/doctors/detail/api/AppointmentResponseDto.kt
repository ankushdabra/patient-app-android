package com.patient.app.doctors.detail.api

data class AppointmentResponseDto(
    val status: Int,
    val message: String? = null,
    val error: String? = null
)
