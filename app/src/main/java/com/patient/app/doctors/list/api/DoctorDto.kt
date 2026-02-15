package com.patient.app.doctors.list.api

data class DoctorDto(
    val id: String,
    val name: String,
    val specialization: String,
    val experience: Int,
    val consultationFee: Double,
    val rating: Double,
    val profileImage: String?
)
