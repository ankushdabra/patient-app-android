package com.healthcare.app.prescriptions.api

import com.healthcare.app.appointments.api.AppointmentDto

data class PrescriptionDto(
    val id: String,
    val patientId: String,
    val doctorId: String,
    val appointment: AppointmentDto,
    val medications: String,
    val instructions: String,
    val notes: String?,
    val prescriptionDate: String,
    val createdAt: String
)
