package com.patient.app.appointments.api

import com.patient.app.doctors.detail.api.DoctorDetailDto
import com.patient.app.prescriptions.api.PatientDto

data class AppointmentDto(
    val id: String,
    val doctor: DoctorDetailDto,
    val patient: PatientDto,
    val appointmentDate: String,
    val appointmentTime: String,
    val status: String,
    val createdAt: String
)
