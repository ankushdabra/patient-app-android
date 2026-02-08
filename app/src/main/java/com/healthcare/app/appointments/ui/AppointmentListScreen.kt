package com.healthcare.app.appointments.ui

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.healthcare.app.appointments.api.AppointmentDto
import com.healthcare.app.appointments.api.AppointmentsRepository
import com.healthcare.app.core.storage.TokenManager
import com.healthcare.app.core.ui.UiState
import com.healthcare.app.core.ui.components.ErrorState
import com.healthcare.app.core.ui.components.LoadingState
import com.healthcare.app.core.ui.theme.HealthcareTheme

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
                        Color(0xFFFFFFFF),
                        Color(0xFFF2F4F8)
                    )
                )
            )
    ) {
        when (val state = uiState) {
            is UiState.Loading -> {
                LoadingState()
            }
            is UiState.Success -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    AppointmentListHeader()
                    
                    if (state.data.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No appointments found.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = state.data,
                                key = { it.id }
                            ) { appointment ->
                                AppointmentListItem(
                                    appointment = appointment,
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .clickable { onAppointmentClick(appointment.id) }
                                )
                            }
                        }
                    }
                }
            }
            is UiState.Error -> {
                ErrorState(
                    message = state.message,
                    onRetry = onRetry
                )
            }
        }
    }
}

@Composable
fun AppointmentListHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "My Appointments",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Track and manage your upcoming visits",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun AppointmentListItem(
    appointment: AppointmentDto,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFCFCFE)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Event,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appointment.doctorName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = appointment.specialization,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = appointment.appointmentDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = appointment.appointmentTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = appointment.status,
                style = MaterialTheme.typography.labelMedium,
                color = if (appointment.status == "BOOKED") 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
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
                doctorId = "d1",
                doctorName = "Dr. Amit Sharma",
                specialization = "Cardiology",
                appointmentDate = "2026-02-09",
                appointmentTime = "10:00:00",
                status = "BOOKED"
            ),
            AppointmentDto(
                id = "2",
                doctorId = "d1",
                doctorName = "Dr. Amit Sharma",
                specialization = "Cardiology",
                appointmentDate = "2026-02-09",
                appointmentTime = "10:30:00",
                status = "BOOKED"
            ),
            AppointmentDto(
                id = "3",
                doctorId = "d1",
                doctorName = "Dr. Amit Sharma",
                specialization = "Cardiology",
                appointmentDate = "2026-02-09",
                appointmentTime = "11:00:00",
                status = "BOOKED"
            )
        )
        AppointmentListScreenContent(
            uiState = UiState.Success(mockAppointments),
            onRetry = {},
            onAppointmentClick = {}
        )
    }
}
