package com.healthcare.app.appointments.api

data class DoctorDetailDto(
    val id: String,
    val name: String?,
    val specialization: String,
    val qualification: String?,
    val experience: Int,
    val rating: Double?,
    val consultationFee: Int,
    val about: String?,
    val clinicAddress: String?,
    val profileImage: String?,
    val availability: List<DoctorAvailabilityDto> = emptyList()
)
