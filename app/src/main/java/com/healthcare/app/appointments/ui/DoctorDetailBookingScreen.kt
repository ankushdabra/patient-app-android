package com.healthcare.app.appointments.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.healthcare.app.appointments.api.DoctorAvailabilityDto
import com.healthcare.app.appointments.api.DoctorDetailDto
import com.healthcare.app.appointments.api.DoctorDetailUiState
import com.healthcare.app.appointments.viewmodel.DoctorDetailViewModel
import com.healthcare.app.appointments.viewmodel.DoctorDetailViewModelFactory
import com.healthcare.app.core.storage.TokenManager
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun generateTimeSlots(
    start: String,
    end: String,
    intervalMinutes: Int = 30
): List<String> {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    var current = LocalTime.parse(start, formatter)
    val endTime = LocalTime.parse(end, formatter)

    val slots = mutableListOf<String>()
    while (current.isBefore(endTime)) {
        slots.add(current.format(formatter))
        current = current.plusMinutes(intervalMinutes.toLong())
    }
    return slots
}

@Composable
fun DoctorDetailBookingScreen(
    doctorId: String,
    tokenManager: TokenManager
) {
    val viewModel: DoctorDetailViewModel = viewModel(
        factory = DoctorDetailViewModelFactory(tokenManager, doctorId)
    )
    val state by viewModel.state.collectAsState()

    when (val currentState = state) {
        is DoctorDetailUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is DoctorDetailUiState.Success -> {
            DoctorDetailBookingContent(doctor = currentState.doctor)
        }
        is DoctorDetailUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = currentState.message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun DoctorDetailBookingContent(
    doctor: DoctorDetailDto
) {
    // 🔹 Map available days
    val availableDays = doctor.availability.map { it.day }

    var selectedDate by remember { mutableStateOf(availableDays.firstOrNull()) }

    // 🔹 Map time slots for selected day
    val timeSlots = remember(selectedDate) {
        doctor.availability
            .firstOrNull { it.day == selectedDate }
            ?.let {
                generateTimeSlots(
                    start = it.startTime,
                    end = it.endTime
                )
            } ?: emptyList()
    }

    var selectedTime by remember(selectedDate) { mutableStateOf(timeSlots.firstOrNull()) }

    val isBookingEnabled = selectedDate != null && selectedTime != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        DoctorDetailHeader(
            name = doctor.name ?: "Doctor",
            specialization = doctor.specialization,
            experience = "${doctor.experience} yrs experience",
            fee = "₹${doctor.consultationFee}",
            rating = doctor.rating
        )

        HorizontalDivider()

        // ✅ DATE SELECTION (FROM API)
        DateSelector(
            selectedDate = selectedDate,
            dates = availableDays,
            onDateSelected = {
                selectedDate = it
            }
        )

        // ✅ TIME SELECTION (FROM API)
        TimeSelector(
            selectedTime = selectedTime,
            times = timeSlots,
            onTimeSelected = { selectedTime = it }
        )

        Spacer(Modifier.height(16.dp))

        BookAppointmentButton(
            enabled = isBookingEnabled,
            onClick = {
                println(
                    "Booking for $selectedDate at $selectedTime for doctor ${doctor.id}"
                )
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun DoctorDetailBookingPreview() {
    MaterialTheme {
        DoctorDetailBookingContent(
            doctor = DoctorDetailDto(
                id = "1",
                name = "Dr. Amit Sharma",
                specialization = "Cardiologist",
                qualification = null,
                experience = 10,
                rating = 4.8,
                consultationFee = 500,
                about = null,
                clinicAddress = null,
                profileImage = null,
                availability = listOf(
                    DoctorAvailabilityDto("MON", "10:00", "13:00"),
                    DoctorAvailabilityDto("WED", "14:00", "18:00")
                )
            )
        )
    }
}
