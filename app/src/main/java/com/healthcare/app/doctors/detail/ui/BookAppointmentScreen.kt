package com.healthcare.app.doctors.detail.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.outlined.CurrencyRupee
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.healthcare.app.core.storage.TokenManager
import com.healthcare.app.core.ui.UiState
import com.healthcare.app.core.ui.components.ErrorState
import com.healthcare.app.core.ui.components.LoadingState
import com.healthcare.app.core.ui.theme.HealthcareTheme
import com.healthcare.app.doctors.detail.api.DoctorAvailabilityDto
import com.healthcare.app.doctors.detail.api.DoctorDetailDto
import com.healthcare.app.doctors.detail.viewmodel.BookAppointmentData
import com.healthcare.app.doctors.detail.viewmodel.BookingState
import com.healthcare.app.doctors.detail.viewmodel.BookAppointmentViewModel
import com.healthcare.app.doctors.detail.viewmodel.BookAppointmentViewModelFactory
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

// --- Utility Functions ---

fun getNextDateForDay(day: String): String {
    val dayOfWeek = when (day.uppercase()) {
        "MON" -> DayOfWeek.MONDAY
        "TUE" -> DayOfWeek.TUESDAY
        "WED" -> DayOfWeek.WEDNESDAY
        "THU" -> DayOfWeek.THURSDAY
        "FRI" -> DayOfWeek.FRIDAY
        "SAT" -> DayOfWeek.SATURDAY
        "SUN" -> DayOfWeek.SUNDAY
        else -> DayOfWeek.MONDAY
    }

    val today = LocalDate.now()
    val nextDate = if (today.dayOfWeek == dayOfWeek) {
        today
    } else {
        today.with(TemporalAdjusters.next(dayOfWeek))
    }

    return nextDate.format(DateTimeFormatter.ISO_DATE)
}

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

// --- Main Screen ---

@Composable
fun BookAppointmentScreen(
    doctorId: String,
    tokenManager: TokenManager,
    onBookingSuccess: () -> Unit = {}
) {
    val viewModel: BookAppointmentViewModel = viewModel(
        factory = BookAppointmentViewModelFactory(tokenManager, doctorId)
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(state) {
        val s = state
        if (s is UiState.Success) {
            val bookingState = s.data.bookingState
            if (bookingState is BookingState.Success) {
                Toast.makeText(context, bookingState.message, Toast.LENGTH_SHORT).show()
                onBookingSuccess()
                viewModel.clearBookingState()
            } else if (bookingState is BookingState.Error) {
                Toast.makeText(context, bookingState.message, Toast.LENGTH_LONG).show()
                viewModel.clearBookingState()
            }
        }
    }

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
        when (val s = state) {
            is UiState.Loading -> LoadingState()
            is UiState.Error -> ErrorState(
                message = s.message,
                onRetry = { /* Handle retry if needed */ })

            is UiState.Success -> {
                DoctorDetailBookingContent(
                    doctor = s.data.doctor,
                    isBooking = s.data.bookingState is BookingState.Loading,
                    onBookAppointment = { day, time ->
                        val actualDate = getNextDateForDay(day)
                        viewModel.bookAppointment(doctorId, actualDate, time)
                    }
                )
            }
        }
    }
}

// --- Consolidated UI Components ---

@Composable
fun DoctorDetailBookingContent(
    doctor: DoctorDetailDto,
    isBooking: Boolean,
    onBookAppointment: (String, String) -> Unit
) {
    val availableDays = doctor.availability.map { it.day }
    var selectedDate by remember { mutableStateOf(availableDays.firstOrNull()) }

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
    val isBookingEnabled = selectedDate != null && selectedTime != null && !isBooking

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        DoctorDetailHeader(
            name = doctor.name ?: "Doctor",
            specialization = doctor.specialization ?: "",
            experience = "${doctor.experience} yrs experience",
            fee = "₹${doctor.consultationFee}",
            rating = doctor.rating
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFCFE)),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                DateSelector(
                    selectedDate = selectedDate,
                    dates = availableDays,
                    onDateSelected = { selectedDate = it }
                )

                Spacer(Modifier.height(24.dp))

                TimeSelector(
                    selectedTime = selectedTime,
                    times = timeSlots,
                    onTimeSelected = { selectedTime = it }
                )
            }
        }

        Spacer(Modifier.weight(1f))

        if (isBooking) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            BookAppointmentButton(
                enabled = isBookingEnabled,
                onClick = {
                    selectedDate?.let { day ->
                        selectedTime?.let { time ->
                            onBookAppointment(day, time)
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun DoctorDetailHeader(
    name: String,
    specialization: String,
    experience: String,
    fee: String,
    rating: Double? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.width(20.dp))

        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = specialization,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoTag(icon = Icons.Outlined.Star, text = rating?.toString() ?: "N/A")
                InfoTag(icon = Icons.Outlined.WorkOutline, text = experience)
                InfoTag(icon = Icons.Outlined.CurrencyRupee, text = fee)
            }
        }
    }
}

@Composable
fun InfoTag(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DateSelector(selectedDate: String?, dates: List<String>, onDateSelected: (String) -> Unit) {
    Column {
        Text(
            text = "Select Date",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            dates.forEach { date ->
                val isSelected = selectedDate == date
                AssistChip(
                    onClick = { onDateSelected(date) },
                    label = { Text(date) },
                    shape = RoundedCornerShape(12.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        labelColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                    ),
                    border = if (isSelected) {
                        null
                    } else {
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    }
                )
            }
        }
    }
}

@Composable
fun TimeSelector(selectedTime: String?, times: List<String>, onTimeSelected: (String) -> Unit) {
    Column {
        Text(
            text = "Select Time",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            times.forEach { time ->
                FilterChip(
                    selected = selectedTime == time,
                    onClick = { onTimeSelected(time) },
                    label = { Text(time) },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun BookAppointmentButton(enabled: Boolean, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        enabled = enabled,
        onClick = onClick
    ) {
        Text(
            text = "Book Appointment",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DoctorDetailBookingPreview() {
    HealthcareTheme {
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
            ),
            isBooking = false,
            onBookAppointment = { _, _ -> }
        )
    }
}
