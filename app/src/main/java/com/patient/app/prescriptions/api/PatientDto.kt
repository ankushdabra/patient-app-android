package com.patient.app.prescriptions.api

import com.patient.app.login.api.UserDto

data class PatientDto(
    val id: String,
    val user: UserDto,
    val age: Int,
    val gender: String,
    val bloodGroup: String
)
