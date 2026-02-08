package com.healthcare.app.prescriptions.api

import com.healthcare.app.login.api.UserDto

data class PatientDto(
    val id: String,
    val user: UserDto,
    val age: Int,
    val gender: String,
    val bloodGroup: String
)
