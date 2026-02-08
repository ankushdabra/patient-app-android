package com.healthcare.app.appointments.api

import com.healthcare.app.doctors.detail.api.DoctorDetailDto
import com.healthcare.app.prescriptions.api.PatientDto

data class AppointmentDto(
    val id: String,
    val doctor: DoctorDetailDto,
    val patient: PatientDto,
    val appointmentDate: String,
    val appointmentTime: String,
    val status: String,
    val createdAt: String
)
