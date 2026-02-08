package com.healthcare.app.prescriptions.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.healthcare.app.appointments.api.AppointmentDto
import com.healthcare.app.core.storage.TokenManager
import com.healthcare.app.core.ui.UiState
import com.healthcare.app.core.ui.components.ErrorState
import com.healthcare.app.core.ui.components.LoadingState
import com.healthcare.app.core.ui.theme.HealthcareTheme
import com.healthcare.app.doctors.detail.api.DoctorDetailDto
import com.healthcare.app.login.api.UserDto
import com.healthcare.app.prescriptions.api.PatientDto
import com.healthcare.app.prescriptions.api.PrescriptionDto
import com.healthcare.app.prescriptions.api.PrescriptionsRepository
import com.healthcare.app.prescriptions.viewmodel.PrescriptionDetailViewModel
import com.healthcare.app.prescriptions.viewmodel.PrescriptionDetailViewModelFactory

@Composable
fun PrescriptionDetailScreen(
    prescriptionId: String,
    tokenManager: TokenManager
) {
    val repository = PrescriptionsRepository(tokenManager)
    val viewModel: PrescriptionDetailViewModel = viewModel(
        factory = PrescriptionDetailViewModelFactory(repository, prescriptionId)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFF2F4F8)
                    )
                )
            )
    ) {
        when (val state = uiState) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(
                message = state.message,
                onRetry = viewModel::loadPrescriptionDetail
            )
            is UiState.Success -> {
                PrescriptionDetailContent(
                    prescription = state.data
                )
            }
        }
    }
}

@Composable
fun PrescriptionDetailContent(
    prescription: PrescriptionDto,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // --- Hero Header Section ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                        )
                    ),
                    shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                )
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp, bottom = 40.dp)
        ) {
            Column {
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "PRESCRIPTION DETAILS",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = Color.White
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = prescription.appointment.doctor.name ?: "Unknown Doctor",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = prescription.appointment.doctor.specialization ?: "Specialist",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        
                        Spacer(Modifier.height(8.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarToday,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Dated: ${prescription.prescriptionDate}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        // --- Body Content ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Medications Card
            PrescriptionCard(
                title = "Medications",
                icon = Icons.Outlined.MedicalServices,
                content = prescription.medications
            )

            // Instructions Card
            PrescriptionCard(
                title = "Instructions",
                icon = Icons.Outlined.Description,
                content = prescription.instructions
            )

            // Notes Card (Only if notes are present)
            if (!prescription.notes.isNullOrBlank()) {
                PrescriptionCard(
                    title = "Additional Notes",
                    icon = Icons.Outlined.Notes,
                    content = prescription.notes
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun PrescriptionCard(
    title: String,
    icon: ImageVector,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrescriptionDetailPreview() {
    HealthcareTheme {
        PrescriptionDetailContent(
            prescription = PrescriptionDto(
                id = "1",
                patientId = "p1",
                doctorId = "d1",
                appointment = AppointmentDto(
                    id = "a1",
                    doctor = DoctorDetailDto(
                        id = "d1",
                        name = "Dr. Amit Sharma",
                        specialization = "Cardiology",
                        qualification = "MBBS, MD",
                        experience = 12,
                        rating = 4.5,
                        consultationFee = 800.0,
                        about = null,
                        clinicAddress = null,
                        profileImage = null
                    ),
                    patient = PatientDto(
                        id = "p1",
                        user = UserDto("u1", "John Doe", "john@example.com", "PATIENT"),
                        age = 30,
                        gender = "MALE",
                        bloodGroup = "O+"
                    ),
                    appointmentDate = "2026-02-09",
                    appointmentTime = "10:00:00",
                    status = "COMPLETED",
                    createdAt = "2026-02-08T10:00:00"
                ),
                medications = "1. Amoxicillin 500mg - 3 times a day\n2. Paracetamol 500mg - As needed for fever\n3. Vitamin C 500mg - Once daily",
                instructions = "Complete the full course of antibiotics. Drink plenty of water and take rest.",
                notes = "Follow up in 7 days if symptoms persist.",
                prescriptionDate = "2026-02-09",
                createdAt = "2026-02-09T11:00:00"
            )
        )
    }
}
