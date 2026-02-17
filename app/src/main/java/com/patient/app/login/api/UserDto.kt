package com.patient.app.login.api

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val age: Int? = null,
    val gender: String? = null,
    val bloodGroup: String? = null,
    val weight: Double? = null,
    val height: Double? = null,
    val specialization: String? = null,
    val qualification: String? = null,
    val experience: Int? = null,
    val consultationFee: Double? = null,
    val about: String? = null,
    val clinicAddress: String? = null,
    val profileImage: String? = null,
    val availability: Map<String, List<AvailabilitySlot>>? = null
)

data class AvailabilitySlot(
    val startTime: String,
    val endTime: String
)

data class TimeSlotDto(
    val startTime: String,
    val endTime: String
)


data class ProfileUpdateRequestDto(
    val name: String,
    val email: String,
    val age: Int? = null,
    val gender: String? = null,
    val bloodGroup: String? = null,
    val weight: Double? = null,
    val height: Double? = null,
    val specialization: String? = null,
    val qualification: String? = null,
    val experience: Int? = null,
    val consultationFee: Double? = null,
    val about: String? = null,
    val clinicAddress: String? = null,
    val profileImage: String? = null,
    val availability: Map<String, List<TimeSlotDto>>? = null
)
