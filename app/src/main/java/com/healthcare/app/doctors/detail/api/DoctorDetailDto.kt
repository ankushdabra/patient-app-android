package com.healthcare.app.doctors.detail.api

import com.healthcare.app.login.api.UserDto

data class DoctorDetailDto(
    val id: String,
    val user: UserDto? = null,
    val name: String?,
    val specialization: String?,
    val qualification: String?,
    val experience: Int?,
    val rating: Double?,
    val consultationFee: Double?,
    val about: String?,
    val clinicAddress: String?,
    val profileImage: String?,
    val availability: Map<String, List<DoctorTimeSlotDto>> = emptyMap()
)

data class DoctorTimeSlotDto(
    val startTime: String,  // 10:00
    val endTime: String     // 13:00
)
