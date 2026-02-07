package com.healthcare.app.doctors.detail.api

data class DoctorAvailabilityDto(
    val day: String,        // MON, WED, FRI
    val startTime: String,  // 10:00
    val endTime: String     // 13:00
)
