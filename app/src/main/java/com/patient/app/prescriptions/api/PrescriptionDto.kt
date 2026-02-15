package com.patient.app.prescriptions.api

data class PrescriptionDto(
    val id: String,
    val medications: String,
    val instructions: String,
    val notes: String?,
    val prescriptionDate: String,
    val appointmentId: String,
    val appointmentDate: String,
    val doctorName: String,
    val patientName: String
)
