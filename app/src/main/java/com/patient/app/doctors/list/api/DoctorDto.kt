package com.patient.app.doctors.list.api

import androidx.compose.runtime.Immutable

@Immutable
data class DoctorDto(
    val id: String,
    val name: String,
    val specialization: String,
    val experience: Int,
    val consultationFee: Double,
    val rating: Double,
    val profileImage: String?,
    val nextAvailable: String? = null
)
