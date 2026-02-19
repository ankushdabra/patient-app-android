package com.patient.app.doctors.detail.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.CurrencyRupee
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patient.app.core.storage.TokenManager
import com.patient.app.core.ui.UiState
import com.patient.app.core.ui.components.ErrorState
import com.patient.app.core.ui.components.LoadingState
import com.patient.app.core.ui.theme.HeaderPrimaryDarkBlue
import com.patient.app.core.ui.theme.HeaderSecondaryDarkBlue
import com.patient.app.core.ui.theme.HealthcareTheme
import com.patient.app.doctors.detail.api.DoctorDetailDto
import com.patient.app.doctors.detail.api.DoctorTimeSlotDto
import com.patient.app.doctors.detail.viewmodel.BookAppointmentViewModel
import com.patient.app.doctors.detail.viewmodel.BookAppointmentViewModelFactory
import com.patient.app.doctors.detail.viewmodel.BookingState
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// --- Main Screen ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookAppointmentScreen(
    doctorId: String,
    tokenManager: TokenManager,
    onBookingSuccess: () -> Unit = {},
    onBackClick: () -> Unit = {}
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val s = state) {
                is UiState.Loading -> LoadingState()
                is UiState.Error -> ErrorState(
                    message = s.message,
                    onRetry = { viewModel.loadDoctor(doctorId) })

                is UiState.Success -> {
                    DoctorDetailBookingContent(
                        doctor = s.data.doctor,
                        isBooking = s.data.bookingState is BookingState.Loading,
                        onBookAppointment = { date, time ->
                            viewModel.bookAppointment(doctorId, date, time)
                        },
                        bottomPadding = padding.calculateBottomPadding()
                    )
                }
            }
        }
    }
}

// --- Consolidated UI Components ---

@Composable
fun DoctorDetailBookingContent(
    doctor: DoctorDetailDto,
    isBooking: Boolean,
    onBookAppointment: (String, String) -> Unit,
    bottomPadding: Dp = 0.dp
) {
    val availableDates = remember(doctor.availability) {
        doctor.availability.keys.toList().sorted()
    }
    var selectedDate by remember(availableDates) { mutableStateOf(availableDates.firstOrNull()) }

    val timeSlots = remember(selectedDate, doctor.availability) {
        doctor.availability[selectedDate] ?: emptyList()
    }

    var selectedTime by remember(timeSlots) {
        mutableStateOf(timeSlots.firstOrNull()?.startTime)
    }

    val isBookingEnabled = selectedDate != null && selectedTime != null && !isBooking

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background,
            )
            .verticalScroll(rememberScrollState())
            .padding(bottom = bottomPadding)
    ) {
        // --- Hero Header Section ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            HeaderPrimaryDarkBlue,
                            HeaderSecondaryDarkBlue.copy(alpha = 0.8f)
                        )
                    )
                )
        ) {
            // Decorative background elements
            Box(
                modifier = Modifier
                    .offset(x = 260.dp, y = (-30).dp)
                    .size(180.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.08f),
                        shape = CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .offset(x = (-20).dp, y = 120.dp)
                    .size(100.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.05f),
                        shape = CircleShape
                    )
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 48.dp, bottom = 40.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "BOOK APPOINTMENT",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            modifier = Modifier.size(44.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = doctor.name ?: "Doctor",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.5).sp
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = (doctor.specialization ?: "Specialist").uppercase(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.School,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = doctor.qualification ?: "MBBS, MD",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.AccountBalance,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = doctor.clinicAddress ?: "Healthcare Clinic, City Center",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }

                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            HeaderInfoTag(
                                icon = Icons.Outlined.Star,
                                text = doctor.rating?.toString() ?: "N/A"
                            )
                            HeaderInfoTag(
                                icon = Icons.Outlined.WorkOutline,
                                text = "${doctor.experience ?: 0} yrs"
                            )
                            HeaderInfoTag(
                                icon = Icons.Outlined.CurrencyRupee,
                                text = "${doctor.consultationFee ?: 0.0}"
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // About Doctor Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "About Doctor",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = doctor.about
                            ?: "${doctor.name ?: "Doctor"} is a highly experienced ${doctor.specialization?.lowercase() ?: "specialist"} with over ${doctor.experience ?: 0} years of clinical practice. They are known for their patient-centric approach and expertise in advanced medical treatments.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Selection Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    if (doctor.availability.isEmpty()) {
                        Text(
                            text = "No slots available",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        DateSelector(
                            selectedDate = selectedDate,
                            dates = availableDates,
                            onDateSelected = { selectedDate = it }
                        )

                        Spacer(Modifier.height(24.dp))

                        TimeSelector(
                            selectedTime = selectedTime,
                            times = timeSlots.map { it.startTime },
                            onTimeSelected = { selectedTime = it }
                        )
                    }
                }
            }

            if (isBooking) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (doctor.availability.isNotEmpty()) {
                BookAppointmentButton(
                    enabled = isBookingEnabled,
                    onClick = {
                        selectedDate?.let { date ->
                            selectedTime?.let { time ->
                                onBookAppointment(date, time)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun HeaderInfoTag(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun DateSelector(selectedDate: String?, dates: List<String>, onDateSelected: (String) -> Unit) {
    Column {
        Text(
            text = "Select Date",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(16.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(dates) { dateStr ->
                val isSelected = selectedDate == dateStr
                val actualDate = remember(dateStr) { LocalDate.parse(dateStr) }
                val isToday = remember(dateStr) { actualDate == LocalDate.now() }
                val isTomorrow = remember(dateStr) { actualDate == LocalDate.now().plusDays(1) }
                val dayName = remember(actualDate) {
                    actualDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase()
                }

                Card(
                    onClick = { onDateSelected(dateStr) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(
                            alpha = 0.3f
                        ),
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.widthIn(min = 80.dp),
                    border = if (isSelected) null else BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = when {
                                isToday -> "Today"
                                else -> dayName
                            },
                            textAlign = TextAlign.Left,
                            maxLines = 1,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = actualDate.dayOfMonth.toString(),
                            textAlign = TextAlign.Left,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimeSelector(selectedTime: String?, times: List<String>, onTimeSelected: (String) -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Select Time",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.weight(1f))
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${times.size} slots",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            times.forEach { time ->
                val isSelected = selectedTime == time
                // Handle "09:00 AM" format
                val hour = time.split(":")[0].toIntOrNull() ?: 0
                val isPm = time.contains("PM", ignoreCase = true)
                val isMorning = if (isPm) hour == 12 else hour < 12 || hour == 12

                FilterChip(
                    selected = isSelected,
                    onClick = { onTimeSelected(time) },
                    label = {
                        Text(
                            text = time,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isMorning) Icons.Outlined.WbSunny else Icons.Outlined.NightsStay,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurface,
                        iconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(
                            alpha = 0.3f
                        ),
                        enabled = true,
                        selected = isSelected,
                        borderWidth = 1.dp
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
        BookAppointmentPreviewContent()
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DoctorDetailBookingDarkPreview() {
    HealthcareTheme(darkTheme = true) {
        BookAppointmentPreviewContent()
    }
}

@Composable
private fun BookAppointmentPreviewContent() {
    DoctorDetailBookingContent(
        doctor = DoctorDetailDto(
            id = "33333333-3333-3333-3333-333333333333",
            name = "Dr Amit Sharma",
            specialization = "Cardiology",
            qualification = "MBBS, MD (Cardiology)",
            experience = 12,
            rating = 4.7,
            consultationFee = 800.0,
            about = "Experienced cardiologist with 12+ years of practice",
            clinicAddress = "Delhi Heart Clinic, New Delhi",
            profileImage = "profile.jpg",
            availability = mapOf(
                "2026-02-20" to listOf(
                    DoctorTimeSlotDto("10:00 AM", "11:00 AM"),
                    DoctorTimeSlotDto("12:00 PM", "01:00 PM")
                ),
                "2026-02-21" to listOf(DoctorTimeSlotDto("11:00 AM", "12:00 PM"))
            )
        ),
        isBooking = false,
        onBookAppointment = { _, _ -> }
    )
}
