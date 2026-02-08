package com.healthcare.app.doctors.detail.api

data class DoctorDetailDto(
    val id: String,
    val name: String?,
    val specialization: String?,
    val qualification: String?,
    val experience: Int?,
    val rating: Double?,
    val consultationFee: Double?,
    val about: String?,
    val clinicAddress: String?,
    val profileImage: String?,
    val availability: List<DoctorAvailabilityDto> = emptyList()
)

data class DoctorAvailabilityDto(
    val day: String,        // MON, WED, FRI
    val startTime: String,  // 10:00
    val endTime: String     // 13:00
)
