package com.patient.app.appointments.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patient.app.appointments.api.AppointmentDto
import com.patient.app.appointments.api.AppointmentsRepository
import com.patient.app.core.storage.TokenManager
import com.patient.app.core.ui.UiState
import com.patient.app.core.ui.components.DashboardHeader
import com.patient.app.core.ui.components.LoadingState
import com.patient.app.core.ui.theme.HealthcareTheme
import com.patient.app.doctors.detail.api.DoctorDetailDto
import com.patient.app.doctors.detail.api.DoctorTimeSlotDto
import com.patient.app.login.api.UserDto
import com.patient.app.prescriptions.api.PatientDto
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AppointmentListScreen(
    tokenManager: TokenManager,
    onAppointmentClick: (String) -> Unit
) {
    val viewModel: AppointmentListViewModel = viewModel(
        factory = AppointmentListViewModelFactory(AppointmentsRepository(tokenManager))
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AppointmentListScreenContent(
        uiState = uiState,
        onRetry = viewModel::loadAppointments,
        onAppointmentClick = onAppointmentClick
    )
}

@Composable
fun AppointmentListScreenContent(
    uiState: UiState<List<AppointmentDto>>,
    onRetry: () -> Unit,
    onAppointmentClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                )
            )
            .imePadding()
    ) {
        when (val state = uiState) {
            is UiState.Loading -> {
                LoadingState()
            }

            is UiState.Success -> {
                val sortedAppointments = remember(state.data) {
                    state.data.sortedBy { it.appointmentDate }
                }
                val groupedAppointments = remember(sortedAppointments) {
                    sortedAppointments.groupBy { it.appointmentDate }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        DashboardHeader(
                            title = "View Your",
                            subtitle = "Appointments",
                            count = state.data.size,
                            countLabel = "Total",
                            icon = Icons.Default.EventAvailable
                        )
                    }

                    if (state.data.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 64.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No appointments found.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        groupedAppointments.forEach { (date, appointments) ->
                            item(key = date) {
                                DateHeader(date = date)
                            }
                            items(
                                items = appointments,
                                key = { it.id }
                            ) { appointment ->
                                AppointmentListItem(
                                    appointment = appointment,
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp)
                                        .clickable { onAppointmentClick(appointment.id) }
                                )
                            }
                        }
                    }
                }
            }

            is UiState.Error -> {
                AppointmentListErrorState(
                    onRetry = onRetry
                )
            }
        }
    }
}

@Composable
fun DateHeader(date: String) {
    val relativeDate = remember(date) { getRelativeDateText(date) }
    
    Text(
        text = relativeDate,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

private fun getRelativeDateText(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString)
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)
        val fullFormatter = DateTimeFormatter.ofPattern("EEE, MMM dd")
        
        when {
            date.isEqual(today) -> "Today, ${date.format(DateTimeFormatter.ofPattern("MMM dd"))}"
            date.isEqual(tomorrow) -> "Tomorrow, ${date.format(DateTimeFormatter.ofPattern("MMM dd"))}"
            date.isAfter(today) && date.isBefore(today.plusDays(7)) -> {
                date.format(fullFormatter)
            }
            else -> {
                val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
                date.format(formatter)
            }
        }
    } catch (e: Exception) {
        dateString
    }
}

@Composable
fun AppointmentListErrorState(
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Unable to load appointments. Please check your internet connection and try again.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(Modifier.height(48.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Try Again",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AppointmentListItem(
    appointment: AppointmentDto,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Profile Circle with soft background
                Surface(
                    modifier = Modifier.size(60.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = appointment.doctor.name ?: "Doctor",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = appointment.doctor.specialization ?: "Specialist",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }

                val (statusColor, statusBgColor) = when (appointment.status) {
                    "BOOKED" -> Color(0xFF4CAF50) to Color(0xFF4CAF50).copy(alpha = 0.1f)
                    "CANCELLED" -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.12f)
                    else -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.12f)
                }

                Surface(
                    color = statusBgColor,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = appointment.status,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        ),
                        color = statusColor
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Sub-container for Time and Action
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = appointment.appointmentTime,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "View Details",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppointmentListScreenPreview() {
    HealthcareTheme {
        val mockAppointments = listOf(
            AppointmentDto(
                id = "1",
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
                    profileImage = null,
                    availability = mapOf(
                        "MON" to listOf(DoctorTimeSlotDto("10:00", "13:00"))
                    )
                ),
                patient = PatientDto(
                    id = "p1",
                    user = UserDto("u1", "John Doe", "john@example.com", "PATIENT"),
                    age = 30,
                    gender = "MALE",
                    bloodGroup = "O+"
                ),
                appointmentDate = LocalDate.now().toString(),
                appointmentTime = "10:00",
                status = "BOOKED",
                createdAt = "2026-02-08T10:00:00"
            ),
            AppointmentDto(
                id = "2",
                doctor = DoctorDetailDto(
                    id = "d2",
                    name = "Dr. Priya Das",
                    specialization = "Dermatology",
                    qualification = "MBBS, MD",
                    experience = 8,
                    rating = 4.2,
                    consultationFee = 600.0,
                    about = null,
                    clinicAddress = null,
                    profileImage = null,
                    availability = mapOf(
                        "MON" to listOf(DoctorTimeSlotDto("14:00", "16:00"))
                    )
                ),
                patient = PatientDto(
                    id = "p1",
                    user = UserDto("u1", "John Doe", "john@example.com", "PATIENT"),
                    age = 30,
                    gender = "MALE",
                    bloodGroup = "O+"
                ),
                appointmentDate = LocalDate.now().plusDays(1).toString(),
                appointmentTime = "15:00",
                status = "BOOKED",
                createdAt = "2026-02-08T11:00:00"
            ),
            AppointmentDto(
                id = "3",
                doctor = DoctorDetailDto(
                    id = "d3",
                    name = "Dr. Sanjay Gupta",
                    specialization = "Neurology",
                    qualification = "MBBS, MD",
                    experience = 20,
                    rating = 4.8,
                    consultationFee = 1200.0,
                    about = null,
                    clinicAddress = null,
                    profileImage = null,
                    availability = mapOf(
                        "TUE" to listOf(DoctorTimeSlotDto("09:00", "12:00"))
                    )
                ),
                patient = PatientDto(
                    id = "p1",
                    user = UserDto("u1", "John Doe", "john@example.com", "PATIENT"),
                    age = 30,
                    gender = "MALE",
                    bloodGroup = "O+"
                ),
                appointmentDate = LocalDate.now().plusDays(2).toString(),
                appointmentTime = "09:30",
                status = "BOOKED",
                createdAt = "2026-02-08T12:00:00"
            )
        )
        AppointmentListScreenContent(
            uiState = UiState.Success(mockAppointments),
            onRetry = {},
            onAppointmentClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppointmentListErrorPreview() {
    HealthcareTheme {
        AppointmentListScreenContent(
            uiState = UiState.Error("Connection timed out"),
            onRetry = {},
            onAppointmentClick = {}
        )
    }
}
